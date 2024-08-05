package com.socify.app.models;

import java.util.ArrayList;

public class Chat {

  public static final String CHATS_DB = "Chats";

  private String id;
  private String sender;
  private String receiver;
  private String message;
  private boolean isSeen;
  private String hideFor;
  private boolean isEdited;

  public Chat() { }

  public Chat(String id, String sender, String receiver, String message, boolean isSeen, String hideFor, boolean isEdited) {
    this.id = id;
    this.sender = sender;
    this.receiver = receiver;
    this.message = message;
    this.isSeen = isSeen;
    this.hideFor = hideFor;
    this.isEdited = isEdited;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
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

  public String getHideFor() {
    return hideFor;
  }

  public void setHideFor(String hideFor) {
    this.hideFor = hideFor;
  }

  public boolean isEdited() {
    return isEdited;
  }

  public void setEdited(boolean edited) {
    isEdited = edited;
  }
}
