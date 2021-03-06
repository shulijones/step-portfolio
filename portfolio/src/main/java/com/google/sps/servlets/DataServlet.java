// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import com.google.gson.Gson;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.sps.data.Comment;
import java.time.LocalDate;
import java.time.Instant;
import java.time.ZoneId;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

/** Servlet that handles guestbook comments */
@WebServlet("/guestbook")
public class DataServlet extends HttpServlet {
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html;charset=UTF-8;");
    ArrayList<Comment> comments = new ArrayList<Comment>();
    final int maxComments = Integer.parseInt(
      request.getParameter("max-comments"));
    final String lang = request.getParameter("lang");

    Query query = new Query("Comment").addSort(
      "timestamp", SortDirection.DESCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    Translate translate = TranslateOptions.getDefaultInstance().getService();
    
    for (Entity entity : results.asIterable()) {
      if (comments.size() == maxComments) {
        break;
      }
      /* Convert its message to the requested langauge */
      Translation translation =
          translate.translate((String)entity.getProperty("text"), 
          Translate.TranslateOption.targetLanguage(lang),
          Translate.TranslateOption.format("text"));
      String translatedMessage = translation.getTranslatedText();
      
      Comment comment = new Comment(translatedMessage,
          (String)entity.getProperty("author"), 
          (long)entity.getProperty("timestamp"));
      comments.add(comment);
    }
    
    Gson gson = new Gson();
    response.getWriter().println(gson.toJson(comments));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    long timestamp = System.currentTimeMillis();
    String comment = request.getParameter("message");
    String author = request.getParameter("name");
    response.setContentType("text/html");

    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("text", comment);
    commentEntity.setProperty("timestamp", timestamp);
    commentEntity.setProperty("author", author);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);

    response.sendRedirect("/index.html");
  }
}
