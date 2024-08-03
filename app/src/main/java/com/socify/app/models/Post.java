package com.socify.app.models;

public class Post {

    public static final String POSTS_DB      = "Posts";
    public static final String LIKES_DB      = "Likes";
    public static final String SAVES_DB      = "Saves";
    public static final String POSTS_STORAGE = "posts";

    private String postId;
    private String postImage;
    private String description;
    private String publisher;

    public Post() {
    }

    public Post(String postId, String postImage, String description, String publisher) {
        this.postId = postId;
        this.postImage = postImage;
        this.description = description;
        this.publisher = publisher;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
