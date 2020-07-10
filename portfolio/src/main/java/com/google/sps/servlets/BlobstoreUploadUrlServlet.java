package com.google.sps.servlets;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet that returns a URL allowing users to upload files to Blobstore.
 */
@WebServlet("/blobstore-upload-url")
public class BlobstoreUploadUrlServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    BlobstoreService blobstoreService = 
        BlobstoreServiceFactory.getBlobstoreService();
    // We link the upload URL Blobstore generates to /image-handler so that 
    // whenever a blob is sent to the URL, after Blobstore processes it,
    // it will redirect to /image-handler so we can save it in Datastore
    String uploadUrl = blobstoreService.createUploadUrl("/image-handler");

    response.setContentType("text/html");
    response.getWriter().println(uploadUrl);
  }
}