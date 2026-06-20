package com.linjufind.pattern.singleton;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

// DESIGN PATTERN: Singleton (Creational)
/*
    Ensures only one JdbcTemplate instance exists for the entire application.
    JdbcTemplate wraps the database connection pool — creating multiple instances
    would waste resources and create inconsistent connection management.

    Singleton contract:
      - private constructor  → nobody can call `new DatabaseManager()`
      - private static instance → only one copy exists in memory
      - synchronized getInstance() → thread-safe, instance is created only once
*/

public class DatabaseManager {
    private static DatabaseManager instance;
    private JdbcTemplate connection;

    private DatabaseManager(DataSource dataSource) {
        setupDatabaseConnection(dataSource);
    }

    // Called once at startup by DatabaseConfig to create the single instance
    public static synchronized DatabaseManager initialize(DataSource dataSource) {
        if (instance == null) {
            instance = new DatabaseManager(dataSource);
        }
        return instance;
    }

    // Used by any class that needs the database connection — always returns the same instance
    public static synchronized DatabaseManager getInstance() {
        return instance;
    }

    private void setupDatabaseConnection(DataSource dataSource) {
        this.connection = new JdbcTemplate(dataSource);
    }

    public JdbcTemplate getConnection() {
        return connection;
    }
}
