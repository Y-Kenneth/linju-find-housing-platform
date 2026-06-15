package com.linjufind.entity;

public class NeighborhoodRating {

    private Integer id;
    private Integer neighborhoodId;
    private Integer userId;
    private Integer safety;
    private Integer transport;
    private Integer foodAccess;
    private Integer foreignerFriendly;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getNeighborhoodId() { return neighborhoodId; }
    public void setNeighborhoodId(Integer neighborhoodId) { this.neighborhoodId = neighborhoodId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getSafety() { return safety; }
    public void setSafety(Integer safety) { this.safety = safety; }

    public Integer getTransport() { return transport; }
    public void setTransport(Integer transport) { this.transport = transport; }

    public Integer getFoodAccess() { return foodAccess; }
    public void setFoodAccess(Integer foodAccess) { this.foodAccess = foodAccess; }

    public Integer getForeignerFriendly() { return foreignerFriendly; }
    public void setForeignerFriendly(Integer foreignerFriendly) { this.foreignerFriendly = foreignerFriendly; }
}
