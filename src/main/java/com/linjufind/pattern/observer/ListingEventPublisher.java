package com.linjufind.pattern.observer;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

// DESIGN PATTERN: Observer (Behavioral) — Publisher / Subject
/*
    ListingEventPublisher is the subject. It maintains a list of observers and notifies
    all of them whenever a significant listing event occurs (approved or deleted).

    AdminController holds a reference to this publisher. When the admin clicks
    "Approve" on a pending listing, the controller calls publishApproved(listingId).
    Every registered observer is then automatically notified — the controller does not
    need to know what those observers do.

    This keeps the admin action logic (controller) completely decoupled from the
    side-effects of that action (logging, counter update, future email notification, etc.).
*/

@Component
public class ListingEventPublisher {
    private final List<ListingObserver> observers = new ArrayList<>();
    public void register(ListingObserver observer) {
        observers.add(observer);
    }
    public void publishApproved(int listingId) {
        for (ListingObserver observer : observers) {
            observer.onListingApproved(listingId);
        }
    }
    public void publishDeleted(int listingId) {
        for (ListingObserver observer : observers) {
            observer.onListingDeleted(listingId);
        }
    }
}
