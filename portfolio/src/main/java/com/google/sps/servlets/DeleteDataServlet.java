package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.gson.JsonObject;

/** Servlet that handles deleting data */
@WebServlet("/delete-data")
public class DeleteDataServlet extends HttpServlet {
@Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String password = request.getParameter("password");
    JsonObject jsonResponse = new JsonObject();

    if (!password.equals("shuli-super-secret-password")) {
      jsonResponse.addProperty("success", false);
      jsonResponse.addProperty("errorMessage", "Incorrect password.");
    }
    else {
      jsonResponse.addProperty("success", true);
      response.setStatus(HttpServletResponse.SC_OK);
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      Query query = new Query("Comment");
      PreparedQuery results = datastore.prepare(query);
      for (Entity entity : results.asIterable()) {
        datastore.delete(entity.getKey());
      }
    }

    response.setContentType("application/json;");
    response.getWriter().println(jsonResponse.toString());
  }
}