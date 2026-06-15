package com.linjufind.service;

import com.linjufind.dao.ReviewDao;
import com.linjufind.entity.Review;
import com.linjufind.pattern.factory.ReviewFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewDao reviewDao;

    public ReviewService(ReviewDao reviewDao) {
        this.reviewDao = reviewDao;
    }

    public String addReview(int listingId, int userId, int rating, String reviewText) {
        if (reviewDao.findByListingAndUser(listingId, userId) != null) {
            return "You have already reviewed this listing.";
        }
        // Factory Pattern implementation
        Review review = ReviewFactory.createReview(listingId, userId, rating, reviewText);
        reviewDao.insert(review);
        return null; // null = success
    }

    public String updateReview(int reviewId, int userId, int rating, String reviewText) {
        Review existing = reviewDao.findById(reviewId);
        if (existing == null) return "Review not found.";
        if (!existing.getUserId().equals(userId)) return "You can only edit your own reviews.";

        existing.setRating(rating);
        existing.setReviewText(reviewText);
        reviewDao.update(existing);
        return null;
    }

    public String deleteReview(int reviewId, int userId) {
        Review existing = reviewDao.findById(reviewId);
        if (existing == null) return null;
        if (!existing.getUserId().equals(userId)) return "You can only delete your own reviews.";
        reviewDao.delete(reviewId);
        return null;
    }

    public Review findById(int id) {
        return reviewDao.findById(id);
    }

    /** All reviews for a listing, newest first (each carries the author username). */
    public List<Review> getByListing(int listingId) {
        return reviewDao.findByListingId(listingId);
    }

    /** Average star rating for a listing, or {@code null} when it has no reviews yet. */
    public Double getAverageRating(int listingId) {
        return reviewDao.getAverageRating(listingId);
    }

    /** Total number of reviews across all listings. */
    public int count() {
        return reviewDao.count();
    }
}
