package com.linjufind.dao;

import com.linjufind.entity.User;
import com.linjufind.pattern.singleton.DatabaseManager;
import com.linjufind.pattern.template.BaseDao;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

// Extends BaseDao — Template Method pattern provides findAll() for free.
// UserDao only needs to supply the SQL and the row mapping.
@Repository
public class UserDao extends BaseDao<User> {

    public UserDao(DatabaseManager databaseManager) {
        super(databaseManager);
    }

    // --- Template Method hooks ---

    @Override
    protected String getQuery() {
        return "SELECT * FROM user ORDER BY created_at DESC";
    }

    @Override
    protected User mapResult(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setEmail(rs.getString("email"));
        u.setNationality(rs.getString("nationality"));
        u.setCity(rs.getString("city"));
        u.setRole(rs.getString("role"));
        u.setCreatedAt(rs.getTimestamp("created_at"));
        return u;
    }

    // --- Other UserDao-specific queries ---

    public User findByUsernameAndPassword(String username, String password) {
        String sql = "SELECT * FROM user WHERE username = ? AND password = ?";
        List<User> results = jdbcTemplate.query(sql, (rs, rowNum) -> mapResult(rs), username, password);
        return results.isEmpty() ? null : results.get(0);
    }

    public User findByUsername(String username) {
        String sql = "SELECT * FROM user WHERE username = ?";
        List<User> results = jdbcTemplate.query(sql, (rs, rowNum) -> mapResult(rs), username);
        return results.isEmpty() ? null : results.get(0);
    }

    public User findByEmail(String email) {
        String sql = "SELECT * FROM user WHERE email = ?";
        List<User> results = jdbcTemplate.query(sql, (rs, rowNum) -> {
            User u = new User();
            u.setId(rs.getInt("id"));
            u.setUsername(rs.getString("username"));
            u.setEmail(rs.getString("email"));
            return u;
        }, email);
        return results.isEmpty() ? null : results.get(0);
    }

    public void insert(User user) {
        String sql = "INSERT INTO user (username, email, password, nationality, city) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getNationality(),
                user.getCity());
    }

    public User findById(Integer id) {
        String sql = "SELECT * FROM user WHERE id = ?";
        List<User> results = jdbcTemplate.query(sql, (rs, rowNum) -> mapResult(rs), id);
        return results.isEmpty() ? null : results.get(0);
    }

    public void updateRole(Integer id, String role) {
        jdbcTemplate.update("UPDATE user SET role = ? WHERE id = ?", role, id);
    }
}
