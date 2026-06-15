package com.linjufind.dto;

/**
 * Lightweight, display-only view of a Listing used by the public login page
 * marquee. It bundles the listing's basic fields together with its calculated
 * rating and a single representative review snippet, so the template can render
 * everything without extra lookups.
 *
 * Kept separate from {@link com.linjufind.entity.Listing} because it carries
 * presentation-only data (review snippet + author) that does not belong on the
 * persistence entity.
 */
public class ShowcaseListing {

    private Integer id;
    private String title;
    private String city;
    private Double price;
    private String propertyType;
    private String imageUrl;
    private Double averageRating;
    private int reviewCount;
    private String reviewSnippet;
    private String reviewAuthor;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getPropertyType() { return propertyType; }
    public void setPropertyType(String propertyType) { this.propertyType = propertyType; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }

    public int getReviewCount() { return reviewCount; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }

    public String getReviewSnippet() { return reviewSnippet; }
    public void setReviewSnippet(String reviewSnippet) { this.reviewSnippet = reviewSnippet; }

    public String getReviewAuthor() { return reviewAuthor; }
    public void setReviewAuthor(String reviewAuthor) { this.reviewAuthor = reviewAuthor; }
}
