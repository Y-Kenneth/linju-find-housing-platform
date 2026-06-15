package com.linjufind.pattern.facade;

import com.linjufind.dao.ListingDao;
import com.linjufind.dao.ReviewDao;
import com.linjufind.entity.Listing;
import com.linjufind.entity.Review;
import com.linjufind.pattern.decorator.ListingDecorator;
import org.springframework.stereotype.Component;

import java.util.List;

// DESIGN PATTERN: Facade (Structural)
/*
    Facade simplifies complex interactions between multiple classes (ListingDao and ReviewDao) into a single method call (getListingDetail).

    When a user opens a listing detail page, they see the listing info, all reviews, and the average star rating 
    together from one page request. Without this, the controller would have to manually call the listing database, 
    then the review database, then calculate the average itself — mixing database logic into the wrong layer.
*/

@Component
public class ListingFacade {

    private final ListingDao listingDao;
    private final ReviewDao reviewDao;

    public ListingFacade(ListingDao listingDao, ReviewDao reviewDao) {
        this.listingDao = listingDao;
        this.reviewDao = reviewDao;
    }

    public ListingDecorator getListingDetail(int listingId) {
        Listing listing = listingDao.findById(listingId);
        if (listing == null) return null;

        List<Review> reviews = reviewDao.findByListingId(listingId);
        Double avgRating = reviewDao.getAverageRating(listingId);

        return new ListingDecorator(listing)
                .withAverageRating(avgRating)
                .withReviewCount(reviews.size());
    }
}
