package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebServlet;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

/**
 * Servlet that returns a Blobstore image based on its key.
 */
@WebServlet("/image-server")
public class ImageServerServlet extends HttpServlet {
    private BlobstoreService blobstoreService =   
      BlobstoreServiceFactory.getBlobstoreService();

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res)
        throws IOException {
            BlobKey blobKey = new BlobKey(req.getParameter("blob-key"));
            // The serve request returns a special message telling AppEngine to 
            // replace it with the blob (image) with this blobKey
            blobstoreService.serve(blobKey, res);
        }
}