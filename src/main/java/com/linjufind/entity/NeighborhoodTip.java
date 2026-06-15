package com.linjufind.entity;

import java.util.Date;

public class NeighborhoodTip {

    private Integer id;
    private Integer neighborhoodId;
    private Integer userId;
    private String tipText;
    private Date createdAt;

    // Joined from user table — not stored in neighborhood_tip table
    private String username;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getNeighborhoodId() { return neighborhoodId; }
    public void setNeighborhoodId(Integer neighborhoodId) { this.neighborhoodId = neighborhoodId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getTipText() { return tipText; }
    public void setTipText(String tipText) { this.tipText = tipText; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}
