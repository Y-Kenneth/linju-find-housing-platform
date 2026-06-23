package com.linjufind.pattern.observer;

import com.linjufind.dao.ListingDao;
import org.springframework.stereotype.Component;

// DESIGN PATTERN: Observer (Behavioral) — Concrete Observer 2
/*
    Maintains a live in-memory count of pending listings.
    The admin dashboard normally recalculates this by scanning every listing in the DB
    on every single page load. PendingListingCounter caches that number and updates it
    instantly when an approval or deletion event fires — no extra DB query needed.

    On first access (getPendingCount == -1) it loads the real count from the DB once,
    then keeps it current through observer notifications from that point forward.
*/

@Component
public class PendingListingCounter implements ListingObserver {
    private final ListingDao listingDao;
    private int pendingCount = -1; // -1 means not yet initialised

    public PendingListingCounter(ListingDao listingDao) {
        this.listingDao = listingDao;
    }
    public int getPendingCount() {
        if (pendingCount == -1) {
            pendingCount = (int) listingDao.findAll().stream()
                    .filter(l -> "pending".equals(l.getStatus()))
                    .count();
        }
        return pendingCount;
    }
    @Override
    public void onListingApproved(int listingId) {
        if (pendingCount > 0) pendingCount--;
    }
    @Override
    public void onListingDeleted(int listingId) {
        // when a listing is deleted, the class cannot be sure whether that listing was pending 
        // or already approved, so it resets pendingCount back to -1
        pendingCount = -1;
    }
}
