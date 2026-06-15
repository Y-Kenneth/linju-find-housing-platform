package com.linjufind.entity;

import java.util.Date;

public class Review {

    private Integer id;
    private Integer listingId;
    private Integer userId;
    private Integer rating;
    private String reviewText;
    private Date createdAt;

    // This field is not stored in the database, but is used to display the username in the UI
    private String username;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getListingId() { return listingId; }
    public void setListingId(Integer listingId) { this.listingId = listingId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getReviewText() { return reviewText; }
    public void setReviewText(String reviewText) { this.reviewText = reviewText; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}
