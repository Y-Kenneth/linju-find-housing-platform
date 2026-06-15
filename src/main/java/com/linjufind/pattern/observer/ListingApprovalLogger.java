package com.linjufind.pattern.observer;

import org.springframework.stereotype.Component;

// DESIGN PATTERN: Observer (Behavioral) — Concrete Observer 1
/*
    Logs every admin approval and deletion to the console.
    In a production system this would write to a file or audit database table.
    The key point: this logic lives here, not inside AdminController.
*/

@Component
public class ListingApprovalLogger implements ListingObserver {

    @Override
    public void onListingApproved(int listingId) {
        System.out.println("[ListingApprovalLogger] Listing #" + listingId + " was APPROVED by admin.");
    }

    @Override
    public void onListingDeleted(int listingId) {
        System.out.println("[ListingApprovalLogger] Listing #" + listingId + " was DELETED by admin.");
    }
}
