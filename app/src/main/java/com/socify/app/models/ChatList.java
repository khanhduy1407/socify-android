package com.socify.app.models;

public class ChatList {

  public static final String CHAT_LIST_DB = "ChatList";

  private String id;

  public ChatList() { }

  public ChatList(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
}
