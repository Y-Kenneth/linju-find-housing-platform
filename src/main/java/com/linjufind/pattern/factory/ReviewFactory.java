package com.linjufind.pattern.factory;

import com.linjufind.entity.Review;

// DESIGN PATTERN: Factory (Creational)
/*
    When a user submits a review for a listing (e.g. "4 stars — great location near Nanjing University"),
    the system needs to create a Review object. Without a Factory, every place that creates a Review —
    ReviewService, and potentially future batch import tools or admin tools — would scatter the same
    object-assembly code across different classes.

    ReviewFactory centralises that creation in one place. If the Review entity ever gains a new
    mandatory field (e.g. a "language" tag for the review), there is exactly ONE place to update.

    The factory also enforces business rules at construction time:
      - rating must be between 1 and 5
      - reviewText must not be blank
    so those checks never need to be duplicated in every caller.
*/

public class ReviewFactory {
    private ReviewFactory() {}

    public static Review createReview(int listingId, int userId, int rating, String reviewText) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }
        if (reviewText == null || reviewText.isBlank()) {
            throw new IllegalArgumentException("Review text must not be empty.");
        }

        Review review = new Review();
        review.setListingId(listingId);
        review.setUserId(userId);
        review.setRating(rating);
        review.setReviewText(reviewText.trim());
        return review;
    }
}
