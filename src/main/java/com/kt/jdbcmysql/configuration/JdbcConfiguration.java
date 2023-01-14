package com.kt.jdbcmysql.configuration;

import java.sql.Connection;
import java.sql.DriverManager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JdbcConfiguration {

    @Value("${jdbc.config.connectionstring}")
    private String connectionString;

    @Value("${jdbc.config.username}")
    private String username;

    @Value("${jdbc.config.password}")
    private String password;

    @Bean
    Connection getConnection() throws Exception {
        try {
            return DriverManager.getConnection(connectionString, username, password);
        } catch (Exception ex) {
            throw new Exception("MySQL Connection failed.");
        }
    }

}
