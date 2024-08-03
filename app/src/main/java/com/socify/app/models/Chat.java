package com.socify.app.models;

public class Chat {

  public static final String CHATS_DB = "Chats";

  private String sender;
  private String receiver;
  private String message;
  private boolean isSeen;

  public Chat() { }

  public Chat(String sender, String receiver, String message, boolean isSeen) {
    this.sender = sender;
    this.receiver = receiver;
    this.message = message;
    this.isSeen = isSeen;
  }

  public String getSender() {
    return sender;
  }

  public void setSender(String sender) {
    this.sender = sender;
  }

  public String getReceiver() {
    return receiver;
  }

  public void setReceiver(String receiver) {
    this.receiver = receiver;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public boolean isSeen() {
    return isSeen;
  }

  public void setSeen(boolean seen) {
    isSeen = seen;
  }
}
