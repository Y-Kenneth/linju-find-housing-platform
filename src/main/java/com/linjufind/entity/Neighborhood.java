package com.linjufind.entity;

public class Neighborhood {

    private Integer id;
    private String name;
    private String city;
    private String description;

    // Calculated — averaged from neighborhood_rating table
    private Double avgSafety;
    private Double avgTransport;
    private Double avgFoodAccess;
    private Double avgForeignerFriendly;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getAvgSafety() { return avgSafety; }
    public void setAvgSafety(Double avgSafety) { this.avgSafety = avgSafety; }

    public Double getAvgTransport() { return avgTransport; }
    public void setAvgTransport(Double avgTransport) { this.avgTransport = avgTransport; }

    public Double getAvgFoodAccess() { return avgFoodAccess; }
    public void setAvgFoodAccess(Double avgFoodAccess) { this.avgFoodAccess = avgFoodAccess; }

    public Double getAvgForeignerFriendly() { return avgForeignerFriendly; }
    public void setAvgForeignerFriendly(Double avgForeignerFriendly) { this.avgForeignerFriendly = avgForeignerFriendly; }
}
