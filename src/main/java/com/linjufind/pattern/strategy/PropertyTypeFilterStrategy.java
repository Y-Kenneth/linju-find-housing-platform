package com.linjufind.pattern.strategy;

import com.linjufind.entity.Listing;

import java.util.List;
import java.util.stream.Collectors;

public class PropertyTypeFilterStrategy implements ListingFilterStrategy {

    private final String propertyType;

    public PropertyTypeFilterStrategy(String propertyType) {
        this.propertyType = propertyType;
    }

    @Override
    public List<Listing> filter(List<Listing> listings) {
        return listings.stream()
                .filter(l -> l.getPropertyType().equalsIgnoreCase(propertyType))
                .collect(Collectors.toList());
    }
}
