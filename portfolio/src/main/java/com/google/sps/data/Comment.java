package com.google.sps.data;

// Public immutable class representing a comment on a website
public class Comment {
  private final String text;
  private final String author;
  private final long timestamp;

  public Comment(String text, String author, long timestamp) {
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

  public long getTimestamp() {
    return this.timestamp;
  }
}