package com.socify.app.ui.models;

public class User {

  private String id;
  private String username;
  private String fullname;
  private String imageUrl;
  private String bio;
  private String status;

  public User(String id, String username, String fullname, String imageUrl, String bio, String status) {
    this.id = id;
    this.username = username;
    this.fullname = fullname;
    this.imageUrl = imageUrl;
    this.bio = bio;
    this.status = status;
  }

  public User () { }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getFullname() {
    return fullname;
  }

  public void setFullname(String fullname) {
    this.fullname = fullname;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public String getBio() {
    return bio;
  }

  public void setBio(String bio) {
    this.bio = bio;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
