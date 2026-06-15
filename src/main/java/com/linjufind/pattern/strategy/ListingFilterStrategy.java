package com.linjufind.pattern.strategy;

import com.linjufind.entity.Listing;

import java.util.List;

// DESIGN PATTERN: Strategy (Behavioral)
/*
    Strategy allows us to define multiple ways to filter listings (by city, by price, by property type) 
    and switch between them at runtime without changing the code that uses the filters.

    When a user searches by city, the app uses the city filter. When they search by price range, 
    it switches to the price filter. Without this, all three filter types would be tangled together 
    in one block of if/else code that gets harder to change every time a new filter is added.
*/

public interface ListingFilterStrategy {
    List<Listing> filter(List<Listing> listings);
}
