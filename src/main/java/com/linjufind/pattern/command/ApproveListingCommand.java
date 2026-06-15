package com.linjufind.pattern.command;

import com.linjufind.dao.ListingDao;

// DESIGN PATTERN: Command (Behavioral) — Concrete Command 1
/*
    Encapsulates the "approve a listing" admin action.
    execute() sets status = "approved".
    undo()    sets status = "pending"  (reverts the approval).

    Real-world scenario: the admin accidentally approves a scam listing
    for a non-existent apartment in Shenzhen. Instead of navigating back
    and manually setting the status again, they click "Undo Last Action"
    on the dashboard — this command reverses itself automatically.
*/

public class ApproveListingCommand implements AdminCommand {

    private final ListingDao listingDao;
    private final int listingId;

    public ApproveListingCommand(ListingDao listingDao, int listingId) {
        this.listingDao = listingDao;
        this.listingId  = listingId;
    }

    @Override
    public void execute() {
        listingDao.updateStatus(listingId, "approved");
    }

    @Override
    public void undo() {
        listingDao.updateStatus(listingId, "pending");
    }

    @Override
    public String getDescription() {
        return "Approve listing #" + listingId;
    }
}
