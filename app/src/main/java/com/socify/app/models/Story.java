package com.socify.app.models;

public class Story {

  public static final String STORIES_DB      = "Stories";
  public static final String STORIES_STORAGE = "stories";

  private String storyId;
  private String userId;
  private String imageUrl;
  private long timeStart;
  private long timeEnd;

  public Story() { }

  public Story(String storyId, String userId, String imageUrl, long timeStart, long timeEnd) {
    this.storyId = storyId;
    this.userId = userId;
    this.imageUrl = imageUrl;
    this.timeStart = timeStart;
    this.timeEnd = timeEnd;
  }

  public String getStoryId() {
    return storyId;
  }

  public void setStoryId(String storyId) {
    this.storyId = storyId;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public long getTimeStart() {
    return timeStart;
  }

  public void setTimeStart(long timeStart) {
    this.timeStart = timeStart;
  }

  public long getTimeEnd() {
    return timeEnd;
  }

  public void setTimeEnd(long timeEnd) {
    this.timeEnd = timeEnd;
  }
}
