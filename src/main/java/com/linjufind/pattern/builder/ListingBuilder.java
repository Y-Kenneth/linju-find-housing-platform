package com.linjufind.pattern.builder;

import com.linjufind.entity.Listing;

// DESIGN PATTERN: Builder (Creational)
/*
    When a user creates a listing, they fill out a form with many fields. The ListingBuilder allows us to 
    construct a Listing object step by step, improving readability and maintainability of the code that creates listings.
    Without this, the code would pass all 7 fields into one giant constructor call that is easy to mix up and hard to read.
*/

public class ListingBuilder {

    private Integer userId;
    private String title;
    private String description;
    private String city;
    private String address;
    private Double price;
    private String propertyType;
    private String contactPhone;
    private String contactWechat;
    private String contactEmail;

    public ListingBuilder userId(Integer userId) {
        this.userId = userId;
        return this;
    }

    public ListingBuilder title(String title) {
        this.title = title;
        return this;
    }

    public ListingBuilder description(String description) {
        this.description = description;
        return this;
    }

    public ListingBuilder city(String city) {
        this.city = city;
        return this;
    }

    public ListingBuilder address(String address) {
        this.address = address;
        return this;
    }

    public ListingBuilder price(Double price) {
        this.price = price;
        return this;
    }

    public ListingBuilder propertyType(String propertyType) {
        this.propertyType = propertyType;
        return this;
    }

    public ListingBuilder contactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
        return this;
    }

    public ListingBuilder contactWechat(String contactWechat) {
        this.contactWechat = contactWechat;
        return this;
    }

    public ListingBuilder contactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
        return this;
    }

    public Listing build() {
        Listing listing = new Listing();
        listing.setUserId(this.userId);
        listing.setTitle(this.title);
        listing.setDescription(this.description);
        listing.setCity(this.city);
        listing.setAddress(this.address);
        listing.setPrice(this.price);
        listing.setPropertyType(this.propertyType);
        listing.setContactPhone(this.contactPhone);
        listing.setContactWechat(this.contactWechat);
        listing.setContactEmail(this.contactEmail);
        listing.setStatus("pending");
        return listing;
    }
}
