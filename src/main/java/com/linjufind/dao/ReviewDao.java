package com.linjufind.dao;

import com.linjufind.entity.Review;
import com.linjufind.pattern.singleton.DatabaseManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ReviewDao {

    private final JdbcTemplate jdbcTemplate;

    public ReviewDao(DatabaseManager databaseManager) {
        this.jdbcTemplate = databaseManager.getConnection();
    }

    private Review mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        Review r = new Review();
        r.setId(rs.getInt("id"));
        r.setListingId(rs.getInt("listing_id"));
        r.setUserId(rs.getInt("user_id"));
        r.setRating(rs.getInt("rating"));
        r.setReviewText(rs.getString("review_text"));
        r.setCreatedAt(rs.getTimestamp("created_at"));
        return r;
    }

    public List<Review> findByListingId(int listingId) {
        String sql = "SELECT r.*, u.username FROM review r " +
                     "JOIN user u ON r.user_id = u.id " +
                     "WHERE r.listing_id = ? ORDER BY r.created_at DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Review r = mapRow(rs, rowNum);
            r.setUsername(rs.getString("username"));
            return r;
        }, listingId);
    }

    public Review findByListingAndUser(int listingId, int userId) {
        String sql = "SELECT * FROM review WHERE listing_id = ? AND user_id = ?";
        List<Review> results = jdbcTemplate.query(sql, this::mapRow, listingId, userId);
        return results.isEmpty() ? null : results.get(0);
    }

    public Review findById(int id) {
        String sql = "SELECT * FROM review WHERE id = ?";
        List<Review> results = jdbcTemplate.query(sql, this::mapRow, id);
        return results.isEmpty() ? null : results.get(0);
    }

    public Double getAverageRating(int listingId) {
        String sql = "SELECT AVG(rating) FROM review WHERE listing_id = ?";
        return jdbcTemplate.queryForObject(sql, Double.class, listingId);
    }

    public int count() {
        Integer c = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM review", Integer.class);
        return c != null ? c : 0;
    }

    public List<Review> findAll() {
        String sql = "SELECT r.*, u.username FROM review r " +
                     "JOIN user u ON r.user_id = u.id ORDER BY r.created_at DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Review r = mapRow(rs, rowNum);
            r.setUsername(rs.getString("username"));
            return r;
        });
    }

    public void insert(Review review) {
        String sql = "INSERT INTO review (listing_id, user_id, rating, review_text) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                review.getListingId(),
                review.getUserId(),
                review.getRating(),
                review.getReviewText());
    }

    public void update(Review review) {
        String sql = "UPDATE review SET rating = ?, review_text = ? WHERE id = ?";
        jdbcTemplate.update(sql, review.getRating(), review.getReviewText(), review.getId());
    }

    public void delete(int id) {
        jdbcTemplate.update("DELETE FROM review WHERE id = ?", id);
    }
}
