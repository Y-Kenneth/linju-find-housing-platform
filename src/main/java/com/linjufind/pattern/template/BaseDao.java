package com.linjufind.pattern.template;

import com.linjufind.pattern.singleton.DatabaseManager;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

// DESIGN PATTERN: Template Method (Behavioral)
/*
     When the admin opens the Users page, or a user opens the Listings page, both follow the exact same steps
     to fetch data. Without this, every DAO would repeat the same fetch-and-map code over and over,
     and a bug fix in one place would need to be applied everywhere else too.
*/

public abstract class BaseDao<T> {
    protected final JdbcTemplate jdbcTemplate;

    public BaseDao(DatabaseManager databaseManager) {
        this.jdbcTemplate = databaseManager.getConnection();
    }

    // Template method — fixed algorithm skeleton, two steps delegated to subclass
    public List<T> findAll() {
        String sql = getQuery();
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapResult(rs));
    }

    protected abstract String getQuery();
    protected abstract T mapResult(ResultSet rs) throws SQLException;
}
