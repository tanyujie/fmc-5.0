package com.paradisecloud.fcm.terminal.fs.db;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseConfig {

    @Bean
    public DatabasePool databasePool() {
        return new DatabasePoolImpl(10);
    }
}