package com.dsvangel.spring.segurity.postgresql.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CommentRequest {
    
    @NotNull
    private Long tweetId;
    
    @NotBlank
    @Size(max = 500, message = "Comment cannot exceed 500 characters")
    private String content;
    
    private Long parentCommentId; // Optional, for replies

    public CommentRequest() {}

    public CommentRequest(Long tweetId, String content) {
        this.tweetId = tweetId;
        this.content = content;
    }

    public CommentRequest(Long tweetId, String content, Long parentCommentId) {
        this.tweetId = tweetId;
        this.content = content;
        this.parentCommentId = parentCommentId;
    }

    // Getters and Setters
    public Long getTweetId() {
        return tweetId;
    }

    public void setTweetId(Long tweetId) {
        this.tweetId = tweetId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(Long parentCommentId) {
        this.parentCommentId = parentCommentId;
    }
}