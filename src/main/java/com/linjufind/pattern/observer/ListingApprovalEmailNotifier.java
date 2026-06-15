package com.linjufind.pattern.observer;

import com.linjufind.dao.ListingDao;
import com.linjufind.dao.UserDao;
import com.linjufind.entity.Listing;
import com.linjufind.entity.User;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

// DESIGN PATTERN: Observer (Behavioral) — Concrete Observer 3
/*
    Sends a real email to the listing owner when their listing is approved by admin.
    This is a third independent reaction to the same "listing approved" event — the
    publisher (ListingEventPublisher) and the admin controller are completely unaware
    that an email is being sent. Adding this observer required zero changes to either.

    When the admin clicks "Approve" on Kenneth's listing:
      1. ListingApprovalLogger logs it to the console       (Observer 1)
      2. PendingListingCounter decrements the dashboard     (Observer 2)
      3. ListingApprovalEmailNotifier emails Kenneth        (Observer 3 — this class)
*/

@Component
public class ListingApprovalEmailNotifier implements ListingObserver {

    private final JavaMailSender mailSender;
    private final ListingDao listingDao;
    private final UserDao userDao;

    public ListingApprovalEmailNotifier(JavaMailSender mailSender,
                                        ListingDao listingDao,
                                        UserDao userDao) {
        this.mailSender = mailSender;
        this.listingDao = listingDao;
        this.userDao = userDao;
    }

    @Override
    public void onListingApproved(int listingId) {
        Listing listing = listingDao.findById(listingId);
        if (listing == null) return;

        User owner = userDao.findById(listing.getUserId());
        if (owner == null || owner.getEmail() == null) return;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("linjufind.noreply@gmail.com");
        message.setTo(owner.getEmail());
        message.setSubject("Your listing has been approved — Linju Find (邻居找房)");
        message.setText(
            "Hi " + owner.getUsername() + ",\n\n" +
            "Great news! Your listing \"" + listing.getTitle() + "\" in " + listing.getCity() +
            " has been reviewed and approved by our admin team.\n\n" +
            "It is now live on Linju Find and visible to all users browsing housing in your area.\n\n" +
            "Listing details:\n" +
            "  Title    : " + listing.getTitle() + "\n" +
            "  City     : " + listing.getCity() + "\n" +
            "  Address  : " + listing.getAddress() + "\n" +
            "  Price    : ¥" + listing.getPrice() + " / month\n" +
            "  Type     : " + listing.getPropertyType() + "\n\n" +
            "Thank you for contributing to the Linju Find community!\n\n" +
            "— The Linju Find Team (邻居找房)\n" +
            "  This is an automated message. Please do not reply."
        );

        try {
            mailSender.send(message);
            System.out.println("[ListingApprovalEmailNotifier] Email sent to " + owner.getEmail()
                    + " for listing #" + listingId);
        } catch (Exception e) {
            System.err.println("[ListingApprovalEmailNotifier] Failed to send email for listing #"
                    + listingId + ": " + e.getMessage());
        }
    }

    @Override
    public void onListingDeleted(int listingId) {
        // No email sent on deletion — admin deletions are internal moderation actions
    }
}
