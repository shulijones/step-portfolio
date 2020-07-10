package com.google.sps.servlets;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

/**
 * Servlet to process image upload requests using the URL that Blobstore gives.
 */
@WebServlet("/image-handler")
public class ImageHandlerServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    long timestamp = System.currentTimeMillis();
    String blobKey = getUploadedFileKey(request);

    // Store image in datastore (with its blob key for re-access later)
    Entity imageEntity = new Entity("Image");
    imageEntity.setProperty("blobKey", blobKey);
    imageEntity.setProperty("author", ""); //TODO
    imageEntity.setProperty("timestamp", timestamp);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(imageEntity);

    response.sendRedirect("/index.html");
  }

  /** Returns the blobstore key to access the uploaded file, or null if the 
  user didn't upload a file. The file can be re-accessed later by sending a GET 
  request to ImageServerServlet with that key as a parameter. */
  private String getUploadedFileKey(HttpServletRequest request) {
    
    // Uploading the file has already sent it to blobstore, so now we want to 
    // retrieve the contents of blobstore
    BlobstoreService blobstoreService =       
        BlobstoreServiceFactory.getBlobstoreService();
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    // Specifically, we want all the objects it contains of type image
    List<BlobKey> blobKeys = blobs.get("image");

    // User submitted form without selecting a file, so we have no images (dev server)
    if (blobKeys == null || blobKeys.isEmpty()) { 
      return null; 
    }

    // There's only one image input in index.html, so the first blob
    // will be the one we want
    BlobKey blobKey = blobKeys.get(0);

    // User submitted form without selecting a file, so there is no image saved 
    // at that key (live server)
    BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);
    if (blobInfo.getSize() == 0) {
      blobstoreService.delete(blobKey);
      return null;
    }

    // TODO: check the validity of the file here to make sure it's an image file
    // https://stackoverflow.com/q/10779564/873165

    // Finally, we can just get the blob's key
    return blobKeys.get(0).getKeyString();
  }
}
