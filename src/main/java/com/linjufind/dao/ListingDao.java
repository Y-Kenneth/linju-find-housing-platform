package com.linjufind.dao;

import com.linjufind.entity.Listing;
import com.linjufind.pattern.singleton.DatabaseManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ListingDao {

    private final JdbcTemplate jdbcTemplate;

    public ListingDao(DatabaseManager databaseManager) {
        this.jdbcTemplate = databaseManager.getConnection();
    }

    private Listing mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        Listing l = new Listing();
        l.setId(rs.getInt("id"));
        l.setUserId(rs.getInt("user_id"));
        l.setTitle(rs.getString("title"));
        l.setDescription(rs.getString("description"));
        l.setCity(rs.getString("city"));
        l.setAddress(rs.getString("address"));
        l.setPrice(rs.getDouble("price"));
        l.setPropertyType(rs.getString("property_type"));
        l.setStatus(rs.getString("status"));
        l.setContactPhone(rs.getString("contact_phone"));
        l.setContactWechat(rs.getString("contact_wechat"));
        l.setContactEmail(rs.getString("contact_email"));
        l.setCreatedAt(rs.getTimestamp("created_at"));
        return l;
    }

    public List<Listing> findApproved(String city, Double minPrice, Double maxPrice,
                                      String propertyType, int page, int pageSize) {
        StringBuilder sql = new StringBuilder(
                "SELECT * FROM listing WHERE status = 'approved'");
        List<Object> params = new ArrayList<>();

        if (city != null && !city.isBlank()) {
            sql.append(" AND city = ?");
            params.add(city);
        }
        if (minPrice != null) {
            sql.append(" AND price >= ?");
            params.add(minPrice);
        }
        if (maxPrice != null) {
            sql.append(" AND price <= ?");
            params.add(maxPrice);
        }
        if (propertyType != null && !propertyType.isBlank()) {
            sql.append(" AND property_type = ?");
            params.add(propertyType);
        }

        sql.append(" ORDER BY created_at DESC LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add((page - 1) * pageSize);

        return jdbcTemplate.query(sql.toString(), this::mapRow, params.toArray());
    }

    public int countApproved(String city, Double minPrice, Double maxPrice, String propertyType) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) FROM listing WHERE status = 'approved'");
        List<Object> params = new ArrayList<>();

        if (city != null && !city.isBlank()) {
            sql.append(" AND city = ?");
            params.add(city);
        }
        if (minPrice != null) {
            sql.append(" AND price >= ?");
            params.add(minPrice);
        }
        if (maxPrice != null) {
            sql.append(" AND price <= ?");
            params.add(maxPrice);
        }
        if (propertyType != null && !propertyType.isBlank()) {
            sql.append(" AND property_type = ?");
            params.add(propertyType);
        }

        Integer count = jdbcTemplate.queryForObject(sql.toString(), Integer.class, params.toArray());
        return count != null ? count : 0;
    }

    public Listing findById(int id) {
        String sql = "SELECT * FROM listing WHERE id = ?";
        List<Listing> results = jdbcTemplate.query(sql, this::mapRow, id);
        return results.isEmpty() ? null : results.get(0);
    }

    /** Number of distinct cities that have at least one approved listing. */
    public int countApprovedCities() {
        Integer c = jdbcTemplate.queryForObject(
                "SELECT COUNT(DISTINCT city) FROM listing WHERE status = 'approved'", Integer.class);
        return c != null ? c : 0;
    }

    /** Cities with the most approved listings, most-active first (for the login chips). */
    public List<String> findTopApprovedCities(int limit) {
        return jdbcTemplate.queryForList(
                "SELECT city FROM listing WHERE status = 'approved' " +
                "GROUP BY city ORDER BY COUNT(*) DESC, city ASC LIMIT ?",
                String.class, limit);
    }

    public List<Listing> findByUserId(int userId) {
        String sql = "SELECT * FROM listing WHERE user_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, this::mapRow, userId);
    }

    public List<Listing> findAllApproved() {
        String sql = "SELECT * FROM listing WHERE status = 'approved' ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, this::mapRow);
    }

    public List<Listing> findAll() {
        String sql = "SELECT * FROM listing ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, this::mapRow);
    }

    public void insert(Listing listing) {
        String sql = "INSERT INTO listing (user_id, title, description, city, address, price, property_type, " +
                     "contact_phone, contact_wechat, contact_email, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'pending')";
        jdbcTemplate.update(sql,
                listing.getUserId(),
                listing.getTitle(),
                listing.getDescription(),
                listing.getCity(),
                listing.getAddress(),
                listing.getPrice(),
                listing.getPropertyType(),
                listing.getContactPhone(),
                listing.getContactWechat(),
                listing.getContactEmail());
    }

    public void delete(int id) {
        jdbcTemplate.update("DELETE FROM listing WHERE id = ?", id);
    }

    public void updateStatus(int id, String status) {
        jdbcTemplate.update("UPDATE listing SET status = ? WHERE id = ?", status, id);
    }
}
