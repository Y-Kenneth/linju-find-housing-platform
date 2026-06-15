package com.linjufind.pattern.strategy;

import com.linjufind.entity.Listing;

import java.util.List;
import java.util.stream.Collectors;

public class CityFilterStrategy implements ListingFilterStrategy {
    private final String city;

    public CityFilterStrategy(String city) {
        this.city = city;
    }

    @Override
    public List<Listing> filter(List<Listing> listings) {
        return listings.stream()
                .filter(l -> l.getCity().equalsIgnoreCase(city))
                .collect(Collectors.toList());
    }
}
