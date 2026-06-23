package com.linjufind.service;

import com.linjufind.dao.NeighborhoodDao;
import com.linjufind.entity.Neighborhood;
import com.linjufind.entity.NeighborhoodRating;
import com.linjufind.entity.NeighborhoodTip;
import com.linjufind.pattern.composite.NeighborhoodScoreComposite;
import com.linjufind.pattern.composite.NeighborhoodScoreLeaf;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NeighborhoodService {

    private final NeighborhoodDao neighborhoodDao;

    public NeighborhoodService(NeighborhoodDao neighborhoodDao) {
        this.neighborhoodDao = neighborhoodDao;
    }

    // Composite Pattern Implementation — builds a score tree for one neighborhood
    public NeighborhoodScoreComposite buildScoreTree(Neighborhood n) {
        NeighborhoodScoreComposite root = new NeighborhoodScoreComposite("Overall Liveability");

        if (n.getAvgSafety()            != null) root.add(new NeighborhoodScoreLeaf("Safety",             n.getAvgSafety()));
        if (n.getAvgTransport()         != null) root.add(new NeighborhoodScoreLeaf("Transport",          n.getAvgTransport()));
        if (n.getAvgFoodAccess()        != null) root.add(new NeighborhoodScoreLeaf("Food Access",        n.getAvgFoodAccess()));
        if (n.getAvgForeignerFriendly() != null) root.add(new NeighborhoodScoreLeaf("Foreigner-Friendly", n.getAvgForeignerFriendly()));

        return root;
    }

    public List<Neighborhood> getAllNeighborhoods() {
        List<Neighborhood> list = neighborhoodDao.findAll();
        for (Neighborhood n : list) {
            neighborhoodDao.loadAverageRatings(n);
        }
        return list;
    }

    public Neighborhood getNeighborhoodDetail(int id) {
        Neighborhood n = neighborhoodDao.findById(id);
        if (n == null) return null;
        neighborhoodDao.loadAverageRatings(n);
        return n;
    }

    public List<NeighborhoodTip> getTips(int neighborhoodId) {
        return neighborhoodDao.findTipsByNeighborhood(neighborhoodId);
    }

    public void submitRating(int neighborhoodId, int userId,
                             int safety, int transport, int foodAccess, int foreignerFriendly) {
        NeighborhoodRating existing = neighborhoodDao.findRatingByNeighborhoodAndUser(neighborhoodId, userId);

        NeighborhoodRating rating = new NeighborhoodRating();
        rating.setNeighborhoodId(neighborhoodId);
        rating.setUserId(userId);
        rating.setSafety(safety);
        rating.setTransport(transport);
        rating.setFoodAccess(foodAccess);
        rating.setForeignerFriendly(foreignerFriendly);

        if (existing == null) {
            neighborhoodDao.insertRating(rating);
        } else {
            neighborhoodDao.updateRating(rating);
        }
    }

    public void submitTip(int neighborhoodId, int userId, String tipText) {
        NeighborhoodTip tip = new NeighborhoodTip();
        tip.setNeighborhoodId(neighborhoodId);
        tip.setUserId(userId);
        tip.setTipText(tipText);
        neighborhoodDao.insertTip(tip);
    }

    public NeighborhoodRating getUserRating(int neighborhoodId, int userId) {
        return neighborhoodDao.findRatingByNeighborhoodAndUser(neighborhoodId, userId);
    }
}
