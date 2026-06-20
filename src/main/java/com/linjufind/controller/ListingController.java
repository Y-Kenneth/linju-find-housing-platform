package com.linjufind.controller;

import com.linjufind.dao.ReviewDao;
import com.linjufind.entity.Listing;
import com.linjufind.entity.Review;
import com.linjufind.entity.User;
import com.linjufind.pattern.builder.ListingBuilder;
import com.linjufind.pattern.decorator.ListingDecorator;
import com.linjufind.service.ListingService;
import com.linjufind.util.CityNames;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/listings")
public class ListingController {

    private static final int PAGE_SIZE = 6;

    private final ListingService listingService;
    private final ReviewDao reviewDao;
    private final CityNames cityNames;

    public ListingController(ListingService listingService, ReviewDao reviewDao, CityNames cityNames) {
        this.listingService = listingService;
        this.reviewDao = reviewDao;
        this.cityNames = cityNames;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String city,
                       @RequestParam(required = false) Double minPrice,
                       @RequestParam(required = false) Double maxPrice,
                       @RequestParam(required = false) String propertyType,
                       @RequestParam(defaultValue = "1") int page,
                       Model model) {

        List<Listing> listings = listingService.getApprovedListings(
                city, minPrice, maxPrice, propertyType, page, PAGE_SIZE);
        int total = listingService.countApprovedListings(city, minPrice, maxPrice, propertyType);
        int totalPages = (int) Math.ceil((double) total / PAGE_SIZE);

        model.addAttribute("listings", listings);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("city", city);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("propertyType", propertyType);
        return "listing/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable int id, HttpSession session, Model model) {
        ListingDecorator listing = listingService.getListingDetail(id);
        if (listing == null) return "redirect:/listings";

        List<Review> reviews = reviewDao.findByListingId(id);

        User loginUser = (User) session.getAttribute("loginUser");
        Review existingReview = null;
        if (loginUser != null) {
            existingReview = reviewDao.findByListingAndUser(id, loginUser.getId());
        }

        model.addAttribute("listing", listing);
        model.addAttribute("cityHanzi", cityNames.hanzi(listing.getCity()));
        model.addAttribute("reviews", reviews);
        model.addAttribute("existingReview", existingReview);
        return "listing/detail";
    }

    @GetMapping("/add")
    public String showAddForm(HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        model.addAttribute("loginEmail", loginUser.getEmail());
        return "listing/add";
    }

    @PostMapping("/add")
    public String processAdd(@RequestParam String title,
                             @RequestParam String description,
                             @RequestParam String city,
                             @RequestParam(required = false) String address,
                             @RequestParam Double price,
                             @RequestParam String propertyType,
                             @RequestParam(required = false) String contactPhone,
                             @RequestParam(required = false) String contactWechat,
                             HttpSession session) {

        User loginUser = (User) session.getAttribute("loginUser");
        System.out.println("[processAdd] New listing submitted: \"" + title + "\" by user " + loginUser.getUsername());

        // Builder Pattern implementation
        Listing listing = new ListingBuilder()
                .userId(loginUser.getId())
                .title(title)
                .description(description)
                .city(city)
                .address(address)
                .price(price)
                .propertyType(propertyType)
                .contactPhone(contactPhone)
                .contactWechat(contactWechat)
                .contactEmail(loginUser.getEmail())
                .build();

        listingService.addListing(listing);
        return "redirect:/listings/my";
    }

    @GetMapping("/my")
    public String myListings(HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        model.addAttribute("listings", listingService.getMyListings(loginUser.getId()));
        return "listing/my";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable int id, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        Listing listing = listingService.findById(id);
        if (listing != null && listing.getUserId().equals(loginUser.getId())) {
            listingService.deleteListing(id);
        }
        return "redirect:/listings/my";
    }
}
