package com.linjufind.pattern.decorator;

import com.linjufind.entity.Listing;

import java.util.Date;

// DESIGN PATTERN: Decorator (Structural)
/*
    Decorator adds extra fields (averageRating and reviewCount) to the original Listing object without 
    modifying its structure.

    When a user sees "4.5 / 5" on a listing detail page, that number was calculated and attached to the listing 
    just for display. Without this, the average rating would have to be stored as a permanent column in 
    the database, meaning it would go out of date every time a new review is submitted.
*/

public class ListingDecorator {

    private final Listing listing;
    private Double averageRating;
    private Integer reviewCount;

    public ListingDecorator(Listing listing) {
        this.listing = listing;
    }

    public ListingDecorator withAverageRating(Double averageRating) {
        this.averageRating = averageRating;
        return this;
    }

    public ListingDecorator withReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
        return this;
    }

    // Delegates all original Listing fields
    public Integer getId()           { return listing.getId(); }
    public Integer getUserId()       { return listing.getUserId(); }
    public String getTitle()         { return listing.getTitle(); }
    public String getDescription()   { return listing.getDescription(); }
    public String getCity()          { return listing.getCity(); }
    public String getAddress()       { return listing.getAddress(); }
    public Double getPrice()         { return listing.getPrice(); }
    public String getPropertyType()  { return listing.getPropertyType(); }
    public String getStatus()        { return listing.getStatus(); }
    public String getContactPhone()  { return listing.getContactPhone(); }
    public String getContactWechat() { return listing.getContactWechat(); }
    public String getContactEmail()  { return listing.getContactEmail(); }
    public Date getCreatedAt()       { return listing.getCreatedAt(); }

    // Decorated (calculated) fields
    public Double getAverageRating() { return averageRating; }
    public Integer getReviewCount()  { return reviewCount; }
}
