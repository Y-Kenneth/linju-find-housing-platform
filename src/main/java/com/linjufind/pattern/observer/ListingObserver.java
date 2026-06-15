package com.linjufind.pattern.observer;

// DESIGN PATTERN: Observer (Behavioral) — Subject interface
/*
    Defines the contract that any observer of listing events must implement.
    When the admin approves or deletes a listing, the publisher calls onListingApproved()
    or onListingDeleted() on every registered observer — without knowing who those observers are.
*/

public interface ListingObserver {
    void onListingApproved(int listingId);
    void onListingDeleted(int listingId);
}
