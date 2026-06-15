package com.linjufind.dao;

import com.linjufind.entity.Neighborhood;
import com.linjufind.entity.NeighborhoodRating;
import com.linjufind.entity.NeighborhoodTip;
import com.linjufind.pattern.singleton.DatabaseManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class NeighborhoodDao {

    private final JdbcTemplate jdbcTemplate;

    public NeighborhoodDao(DatabaseManager databaseManager) {
        this.jdbcTemplate = databaseManager.getConnection();
    }

    public List<Neighborhood> findAll() {
        String sql = "SELECT * FROM neighborhood ORDER BY city, name";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Neighborhood n = new Neighborhood();
            n.setId(rs.getInt("id"));
            n.setName(rs.getString("name"));
            n.setCity(rs.getString("city"));
            n.setDescription(rs.getString("description"));
            return n;
        });
    }

    public Neighborhood findById(int id) {
        String sql = "SELECT * FROM neighborhood WHERE id = ?";
        List<Neighborhood> results = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Neighborhood n = new Neighborhood();
            n.setId(rs.getInt("id"));
            n.setName(rs.getString("name"));
            n.setCity(rs.getString("city"));
            n.setDescription(rs.getString("description"));
            return n;
        }, id);
        return results.isEmpty() ? null : results.get(0);
    }

    public void loadAverageRatings(Neighborhood neighborhood) {
        String sql = "SELECT AVG(safety) as avg_safety, AVG(transport) as avg_transport, " +
                     "AVG(food_access) as avg_food, AVG(foreigner_friendly) as avg_foreigner " +
                     "FROM neighborhood_rating WHERE neighborhood_id = ?";
        jdbcTemplate.query(sql, rs -> {
            neighborhood.setAvgSafety(rs.getObject("avg_safety") != null ? rs.getDouble("avg_safety") : null);
            neighborhood.setAvgTransport(rs.getObject("avg_transport") != null ? rs.getDouble("avg_transport") : null);
            neighborhood.setAvgFoodAccess(rs.getObject("avg_food") != null ? rs.getDouble("avg_food") : null);
            neighborhood.setAvgForeignerFriendly(rs.getObject("avg_foreigner") != null ? rs.getDouble("avg_foreigner") : null);
        }, neighborhood.getId());
    }

    public NeighborhoodRating findRatingByNeighborhoodAndUser(int neighborhoodId, int userId) {
        String sql = "SELECT * FROM neighborhood_rating WHERE neighborhood_id = ? AND user_id = ?";
        List<NeighborhoodRating> results = jdbcTemplate.query(sql, (rs, rowNum) -> {
            NeighborhoodRating r = new NeighborhoodRating();
            r.setId(rs.getInt("id"));
            r.setNeighborhoodId(rs.getInt("neighborhood_id"));
            r.setUserId(rs.getInt("user_id"));
            r.setSafety(rs.getInt("safety"));
            r.setTransport(rs.getInt("transport"));
            r.setFoodAccess(rs.getInt("food_access"));
            r.setForeignerFriendly(rs.getInt("foreigner_friendly"));
            return r;
        }, neighborhoodId, userId);
        return results.isEmpty() ? null : results.get(0);
    }

    public void insertRating(NeighborhoodRating rating) {
        String sql = "INSERT INTO neighborhood_rating (neighborhood_id, user_id, safety, transport, food_access, foreigner_friendly) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                rating.getNeighborhoodId(),
                rating.getUserId(),
                rating.getSafety(),
                rating.getTransport(),
                rating.getFoodAccess(),
                rating.getForeignerFriendly());
    }

    public void updateRating(NeighborhoodRating rating) {
        String sql = "UPDATE neighborhood_rating SET safety = ?, transport = ?, food_access = ?, foreigner_friendly = ? " +
                     "WHERE neighborhood_id = ? AND user_id = ?";
        jdbcTemplate.update(sql,
                rating.getSafety(),
                rating.getTransport(),
                rating.getFoodAccess(),
                rating.getForeignerFriendly(),
                rating.getNeighborhoodId(),
                rating.getUserId());
    }

    public List<NeighborhoodTip> findTipsByNeighborhood(int neighborhoodId) {
        String sql = "SELECT t.*, u.username FROM neighborhood_tip t " +
                     "JOIN user u ON t.user_id = u.id " +
                     "WHERE t.neighborhood_id = ? ORDER BY t.created_at DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            NeighborhoodTip t = new NeighborhoodTip();
            t.setId(rs.getInt("id"));
            t.setNeighborhoodId(rs.getInt("neighborhood_id"));
            t.setUserId(rs.getInt("user_id"));
            t.setTipText(rs.getString("tip_text"));
            t.setCreatedAt(rs.getTimestamp("created_at"));
            t.setUsername(rs.getString("username"));
            return t;
        }, neighborhoodId);
    }

    public void insertTip(NeighborhoodTip tip) {
        String sql = "INSERT INTO neighborhood_tip (neighborhood_id, user_id, tip_text) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, tip.getNeighborhoodId(), tip.getUserId(), tip.getTipText());
    }
}
