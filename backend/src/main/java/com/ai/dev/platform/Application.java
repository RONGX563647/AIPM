package com.ai.dev.platform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@SpringBootApplication
public class Application {
    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner verifyDatabase(DataSource dataSource) {
        return args -> {
            log.info("Verifying database connectivity...");
            try (Connection conn = dataSource.getConnection()) {
                try (Statement st = conn.createStatement()) {
                    ResultSet rs = st.executeQuery("SELECT 1");
                    if (rs.next()) {
                        int v = rs.getInt(1);
                        log.info("Database test query returned: {}", v);
                    } else {
                        log.warn("Database test query returned no rows");
                    }
                }
            } catch (Exception e) {
                log.error("Failed to verify database connectivity", e);
                throw e;
            }
        };
    }
}
