package com.google.sps.data;

// Public immutable class representing an image uploaded to a website
public class Image {
  // The image can be accessed at "/image-handler?blob-key=${blobKey}
  private final String blobKey; 
  private final String author;
  private final long timestamp;

  public Image(String blobKey, String author, long timestamp) {
    this.blobKey = blobKey;
    this.author = author;
    this.timestamp = timestamp;
  }

  public String getBlobKey() {
    return this.blobKey;
  }

  public String getAuthor() {
    return this.author;
  }

  public long getTimestamp() {
    return this.timestamp;
  }
}