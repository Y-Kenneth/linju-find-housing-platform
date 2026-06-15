package com.linjufind.service;

import com.linjufind.dao.ListingDao;
import com.linjufind.entity.Listing;
import com.linjufind.pattern.decorator.ListingDecorator;
import com.linjufind.pattern.facade.ListingFacade;
import com.linjufind.pattern.strategy.CityFilterStrategy;
import com.linjufind.pattern.strategy.ListingFilterStrategy;
import com.linjufind.pattern.strategy.PriceRangeFilterStrategy;
import com.linjufind.pattern.strategy.PropertyTypeFilterStrategy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListingService {

    private final ListingDao listingDao;
    private final ListingFacade listingFacade;

    public ListingService(ListingDao listingDao, ListingFacade listingFacade) {
        this.listingDao = listingDao;
        this.listingFacade = listingFacade;
    }

    // DESIGN PATTERN: Strategy — selects and applies the correct filter at runtime.
    // Each filter type is an independent, swappable strategy; adding a new filter
    // (e.g. "furnished only") means writing one new class, not editing this method.
    public List<Listing> getApprovedListings(String city, Double minPrice, Double maxPrice,
                                              String propertyType, int page, int pageSize) {
                                                
        // Load all approved listings, then apply whichever strategy the user chose
        List<Listing> all = listingDao.findAllApproved();

        if (city != null && !city.isBlank()) {
            ListingFilterStrategy strategy = new CityFilterStrategy(city);
            all = strategy.filter(all);
        }
        if (minPrice != null || maxPrice != null) {
            ListingFilterStrategy strategy = new PriceRangeFilterStrategy(minPrice, maxPrice);
            all = strategy.filter(all);
        }
        if (propertyType != null && !propertyType.isBlank()) {
            ListingFilterStrategy strategy = new PropertyTypeFilterStrategy(propertyType);
            all = strategy.filter(all);
        }

        // Manual pagination after filtering
        int total = all.size();
        int fromIndex = Math.min((page - 1) * pageSize, total);
        int toIndex   = Math.min(fromIndex + pageSize, total);
        return all.subList(fromIndex, toIndex);
    }

    public int countApprovedListings(String city, Double minPrice, Double maxPrice,
                                     String propertyType) {
        // Count after applying strategies — mirrors the filter logic above
        List<Listing> all = listingDao.findAllApproved();

        if (city != null && !city.isBlank()) {
            all = new CityFilterStrategy(city).filter(all);
        }
        if (minPrice != null || maxPrice != null) {
            all = new PriceRangeFilterStrategy(minPrice, maxPrice).filter(all);
        }
        if (propertyType != null && !propertyType.isBlank()) {
            all = new PropertyTypeFilterStrategy(propertyType).filter(all);
        }

        return all.size();
    }

    // Delegates to ListingFacade — see pattern/facade/ListingFacade.java
    public ListingDecorator getListingDetail(int listingId) {
        return listingFacade.getListingDetail(listingId);
    }

    public List<Listing> getMyListings(int userId) {
        return listingDao.findByUserId(userId);
    }

    public void addListing(Listing listing) {
        listingDao.insert(listing);
    }

    public void deleteListing(int id) {
        listingDao.delete(id);
    }

    public Listing findById(int id) {
        return listingDao.findById(id);
    }

    public int countApprovedCities() {
        return listingDao.countApprovedCities();
    }

    public java.util.List<String> getTopApprovedCities(int limit) {
        return listingDao.findTopApprovedCities(limit);
    }
}
