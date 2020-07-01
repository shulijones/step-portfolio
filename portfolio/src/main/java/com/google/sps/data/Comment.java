package com.google.sps.data;
import java.time.LocalDate;

// Public immutable class representing a comment on a website
public class Comment {
  private final String text;
  private final String author;
  private final String timestamp;

  public Comment(String text, String author, String timestamp) {
    this.text = text;
    this.author = author;
    this.timestamp = timestamp;
  }

  public String getText() {
    return this.text;
  }

  public String getAuthor() {
    return this.author;
  }

  public String getTimestamp() {
    return this.timestamp;
  }
}