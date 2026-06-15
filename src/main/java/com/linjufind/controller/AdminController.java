package com.linjufind.controller;

import com.linjufind.dao.ListingDao;
import com.linjufind.dao.ReviewDao;
import com.linjufind.dao.UserDao;
import com.linjufind.entity.User;
import com.linjufind.pattern.command.ApproveListingCommand;
import com.linjufind.pattern.command.CommandHistory;
import com.linjufind.pattern.command.DeactivateUserCommand;
import com.linjufind.pattern.observer.ListingApprovalEmailNotifier;
import com.linjufind.pattern.observer.ListingApprovalLogger;
import com.linjufind.pattern.observer.ListingEventPublisher;
import com.linjufind.pattern.observer.PendingListingCounter;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ListingDao listingDao;
    private final ReviewDao reviewDao;
    private final UserDao userDao;

    // DESIGN PATTERN: Observer — publisher fires events; observers react independently
    private final ListingEventPublisher listingEventPublisher;
    private final ListingApprovalLogger listingApprovalLogger;
    private final PendingListingCounter pendingListingCounter;
    private final ListingApprovalEmailNotifier listingApprovalEmailNotifier;

    // DESIGN PATTERN: Command — every reversible admin action goes through CommandHistory
    private final CommandHistory commandHistory;

    public AdminController(ListingDao listingDao, ReviewDao reviewDao, UserDao userDao,
                           ListingEventPublisher listingEventPublisher,
                           ListingApprovalLogger listingApprovalLogger,
                           PendingListingCounter pendingListingCounter,
                           ListingApprovalEmailNotifier listingApprovalEmailNotifier,
                           CommandHistory commandHistory) {
        this.listingDao = listingDao;
        this.reviewDao = reviewDao;
        this.userDao = userDao;
        this.listingEventPublisher = listingEventPublisher;
        this.listingApprovalLogger = listingApprovalLogger;
        this.pendingListingCounter = pendingListingCounter;
        this.listingApprovalEmailNotifier = listingApprovalEmailNotifier;
        this.commandHistory = commandHistory;
    }

    // Register all three observers once at startup
    @PostConstruct
    public void registerObservers() {
        listingEventPublisher.register(listingApprovalLogger);
        listingEventPublisher.register(pendingListingCounter);
        listingEventPublisher.register(listingApprovalEmailNotifier);
    }

    private boolean isAdmin(HttpSession session) {
        User u = (User) session.getAttribute("loginUser");
        return u != null && "admin".equals(u.getRole());
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/listings";

        long totalUsers    = userDao.findAll().size();
        long totalListings = listingDao.findAll().size();
        long totalReviews  = reviewDao.findAll().size();

        // DESIGN PATTERN: Observer — pending count from PendingListingCounter cache
        long pendingListings = pendingListingCounter.getPendingCount();

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalListings", totalListings);
        model.addAttribute("totalReviews", totalReviews);
        model.addAttribute("pendingListings", pendingListings);

        // Command Pattern Implementation — expose full action history and undo state to dashboard
        model.addAttribute("lastAction", commandHistory.peekLastDescription());
        model.addAttribute("canUndo", commandHistory.hasHistory());
        model.addAttribute("actionHistory", commandHistory.getHistoryDescriptions());
        return "admin/dashboard";
    }

    @GetMapping("/listings")
    public String listings(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/listings";
        model.addAttribute("listings", listingDao.findAll());
        return "admin/listings";
    }

    @PostMapping("/listings/approve/{id}")
    public String approveListing(@PathVariable int id, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/listings";

        // Command Pattern Implementation — execute via history so it can be undone
        commandHistory.executeCommand(new ApproveListingCommand(listingDao, id));

        // DESIGN PATTERN: Observer — notify all observers that a listing was approved
        listingEventPublisher.publishApproved(id);
        return "redirect:/admin/listings";
    }

    @PostMapping("/listings/delete/{id}")
    public String deleteListing(@PathVariable int id, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/listings";
        listingDao.delete(id);
        // DESIGN PATTERN: Observer — notify all observers that a listing was deleted
        listingEventPublisher.publishDeleted(id);
        return "redirect:/admin/listings";
    }

    @GetMapping("/reviews")
    public String reviews(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/listings";
        model.addAttribute("reviews", reviewDao.findAll());
        return "admin/reviews";
    }

    @PostMapping("/reviews/delete/{id}")
    public String deleteReview(@PathVariable int id, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/listings";
        reviewDao.delete(id);
        return "redirect:/admin/reviews";
    }

    @GetMapping("/users")
    public String users(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/listings";
        model.addAttribute("users", userDao.findAll());
        return "admin/users";
    }

    @PostMapping("/users/deactivate/{id}")
    public String deactivateUser(@PathVariable int id, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/listings";

        // Command Pattern Implementation — execute via history so it can be undone
        commandHistory.executeCommand(new DeactivateUserCommand(userDao, id));

        return "redirect:/admin/users";
    }

    @PostMapping("/users/reactivate/{id}")
    public String reactivateUser(@PathVariable int id, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/listings";
        userDao.updateRole(id, "user");
        return "redirect:/admin/users";
    }

    // DESIGN PATTERN: Command — undo the last reversible admin action
    @PostMapping("/undo")
    public String undo(HttpSession session) {
        if (!isAdmin(session)) return "redirect:/listings";
        commandHistory.undoLast();
        return "redirect:/admin/dashboard";
    }
}
