package com.ai.dev.platform;

import org.mybatis.spring.annotation.MapperScan;
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

/**
 * AI开发平台主应用程序启动类
 * 
 * <p>这是整个AI开发平台后端服务的入口点，负责：
 * <ul>
 *   <li>启动Spring Boot应用程序</li>
 *   <li>配置应用级的初始化逻辑</li>
 *   <li>验证数据库连接状态</li>
 *   <li>预加载关键的日志组件</li>
 * </ul>
 * 
 * @author AI开发平台团队
 * @version 1.0.0
 * @since 1.0.0
 */
@SpringBootApplication
@EnableScheduling
@MapperScan("com.ai.dev.platform.modules.*.mapper")
public class Application {
    private static final Logger log = LoggerFactory.getLogger(Application.class);

    /**
     * 应用程序主入口方法
     * 
     * <p>启动Spring Boot应用程序并初始化所有相关组件
     * 
     * @param args 命令行参数
     * @since 1.0.0
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * 数据库连接验证器
     * 
     * <p>在应用程序启动时验证数据库连接状态，确保：
     * <ul>
     *   <li>数据库服务可访问</li>
     *   <li>连接池配置正确</li>
     *   <li>基础查询能够正常执行</li>
     * </ul>
     * 
     * @param dataSource 数据源对象
     * @param failOnDbCheck 是否在数据库检查失败时终止应用启动
     * @return CommandLineRunner 数据库验证执行器
     * @since 1.0.0
     */
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

    /**
     * Logback类预加载器
     * 
     * <p>预加载关键的Logback类，避免在应用关闭时出现NoClassDefFoundError异常
     * 这是为了解决日志框架在应用生命周期结束时的潜在问题
     * 
     * @return CommandLineRunner Logback类预加载执行器
     * @since 1.0.0
     */
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
