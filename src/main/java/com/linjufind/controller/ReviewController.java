package com.linjufind.controller;

import com.linjufind.entity.Review;
import com.linjufind.entity.User;
import com.linjufind.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/add/{listingId}")
    public String showAddForm(@PathVariable int listingId, HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if ("admin".equals(loginUser.getRole())) return "redirect:/listings/" + listingId;
        model.addAttribute("listingId", listingId);
        return "review/form";
    }

    @PostMapping("/add/{listingId}")
    public String processAdd(@PathVariable int listingId,
                             @RequestParam int rating,
                             @RequestParam String reviewText,
                             HttpSession session,
                             Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if ("admin".equals(loginUser.getRole())) return "redirect:/listings/" + listingId;
        String error = reviewService.addReview(listingId, loginUser.getId(), rating, reviewText);
        if (error != null) {
            model.addAttribute("listingId", listingId);
            model.addAttribute("error", error);
            return "review/form";
        }
        return "redirect:/listings/" + listingId;
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable int id, HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        Review review = reviewService.findById(id);
        if (review == null || !review.getUserId().equals(loginUser.getId())) {
            return "redirect:/listings";
        }
        model.addAttribute("review", review);
        return "review/edit";
    }

    @PostMapping("/edit/{id}")
    public String processEdit(@PathVariable int id,
                              @RequestParam int rating,
                              @RequestParam String reviewText,
                              HttpSession session,
                              Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        String error = reviewService.updateReview(id, loginUser.getId(), rating, reviewText);
        if (error != null) {
            Review review = reviewService.findById(id);
            model.addAttribute("review", review);
            model.addAttribute("error", error);
            return "review/edit";
        }
        Review review = reviewService.findById(id);
        return "redirect:/listings/" + review.getListingId();
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable int id, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        Review review = reviewService.findById(id);
        int listingId = (review != null) ? review.getListingId() : 0;
        reviewService.deleteReview(id, loginUser.getId());
        return listingId > 0 ? "redirect:/listings/" + listingId : "redirect:/listings";
    }
}
