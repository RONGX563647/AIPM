package com.ai.dev.platform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@SpringBootApplication
@EnableScheduling
public class Application {
    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner verifyDatabase(DataSource dataSource,
                                             @Value("${app.startup.failOnDatabaseCheck:false}") boolean failOnDbCheck) {
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
                log.error("Failed to verify database connectivity. {}",
                        failOnDbCheck ? "Application will stop due to configuration." : "Continuing startup (set app.startup.failOnDatabaseCheck=true to fail).", e);
                if (failOnDbCheck) {
                    throw e;
                }
            }
        };
    }

    // Preload critical logback classes to avoid NoClassDefFoundError during shutdown
    @Bean
    public CommandLineRunner preloadLogbackClasses() {
        return args -> {
            try {
                Class.forName("ch.qos.logback.classic.spi.ThrowableProxy");
                log.debug("Preloaded logback ThrowableProxy");
            } catch (Throwable t) {
                // Don't use logger here to avoid cascading logging errors if binding is broken
                System.err.println("Warning: could not preload logback ThrowableProxy: " + t);
            }
        };
    }
}
