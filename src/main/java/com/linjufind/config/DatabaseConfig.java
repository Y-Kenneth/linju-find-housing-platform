package com.linjufind.config;

import com.linjufind.pattern.singleton.DatabaseManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

// Initializes the DatabaseManager Singleton at startup.
@Configuration
public class DatabaseConfig {

    @Bean
    public DatabaseManager databaseManager(DataSource dataSource) {
        return DatabaseManager.initialize(dataSource);
    }
}
