package com.linjufind.pattern.strategy;

import com.linjufind.entity.Listing;

import java.util.List;
import java.util.stream.Collectors;

public class PriceRangeFilterStrategy implements ListingFilterStrategy {
    private final Double minPrice;
    private final Double maxPrice;

    public PriceRangeFilterStrategy(Double minPrice, Double maxPrice) {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }

    @Override
    public List<Listing> filter(List<Listing> listings) {
        return listings.stream()
                .filter(l -> (minPrice == null || l.getPrice() >= minPrice)
                          && (maxPrice == null || l.getPrice() <= maxPrice))
                .collect(Collectors.toList());
    }
}
