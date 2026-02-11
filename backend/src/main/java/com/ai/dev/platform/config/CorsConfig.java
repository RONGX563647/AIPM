package com.ai.dev.platform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * CORS跨域配置类
 * 
 * <p>负责配置跨域资源共享(CORS)策略，解决前后端分离架构中的跨域问题：
 * <ul>
 *   <li>配置允许的源站地址</li>
 *   <li>设置允许的HTTP方法</li>
 *   <li>配置允许的请求头</li>
 *   <li>处理预检请求(OPTIONS)</li>
 * </ul>
 * 
 * <p>该配置类通过@Configuration注解标记为Spring配置类，
 * 提供全局的CORS过滤器配置。
 * 
 * @author AI开发平台团队
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
public class CorsConfig {

    /**
     * CORS过滤器Bean配置
     * 
     * <p>创建并配置CORS过滤器，用于处理跨域请求：
     * <ul>
     *   <li>允许所有源站的跨域请求(*)</li>
     *   <li>允许所有HTTP方法(GET,POST,PUT,DELETE等)</li>
     *   <li>允许所有请求头的传递</li>
     *   <li>允许携带认证信息(credentials)</li>
     * </ul>
     * 
     * <p><b>安全注意事项：</b>
     * <ul>
     *   <li>生产环境中应限制具体的源站地址</li>
     *   <li>避免使用通配符(*)，应明确指定可信域名</li>
     *   <li>根据实际需求限制允许的HTTP方法</li>
     *   <li>谨慎处理credentials=true的配置</li>
     * </ul>
     * 
     * @return CorsFilter 配置好的CORS过滤器实例
     * @since 1.0.0
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 允许所有源站(*) - 生产环境应替换为具体域名
        config.addAllowedOriginPattern("*");
        // 允许所有HTTP方法
        config.addAllowedMethod("*");
        // 允许所有请求头
        config.addAllowedHeader("*");
        // 允许携带认证信息
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 对所有路径应用CORS配置
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
