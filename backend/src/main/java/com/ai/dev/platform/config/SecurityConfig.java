package com.ai.dev.platform.config;

import com.ai.dev.platform.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security安全配置类
 * 
 * <p>负责配置整个应用程序的安全策略，包括：
 * <ul>
 *   <li>JWT认证过滤器配置</li>
 *   <li>密码加密器配置</li>
 *   <li>CSRF保护配置</li>
 *   <li>会话管理策略</li>
 *   <li>请求授权规则</li>
 * </ul>
 * 
 * @author AI开发平台团队
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
public class SecurityConfig {
    
    /**
     * 密码编码器Bean配置
     * 
     * <p>配置BCryptPasswordEncoder用于用户密码的加密和验证
     * BCrypt是一种安全的密码哈希算法，具有：
     * <ul>
     *   <li>内置salt防止彩虹表攻击</li>
     *   <li>可配置的计算成本</li>
     *   <li>抵御字典攻击和暴力破解</li>
     * </ul>
     * 
     * @return BCryptPasswordEncoder BCrypt密码编码器实例
     * @since 1.0.0
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Spring Security过滤器链配置
     * 
     * <p>配置HTTP安全策略，包括：
     * <ul>
     *   <li>禁用CSRF保护（REST API场景下通常禁用）</li>
     *   <li>配置无状态会话管理</li>
     *   <li>设置请求授权规则</li>
     *   <li>集成JWT认证过滤器</li>
     * </ul>
     * 
     * 注意：当前为了开发测试便利，所有请求都放行，并临时注释了JWT过滤器集成
     * 
     * @param http HttpSecurity对象
     * @param jwtAuthFilter JWT认证过滤器
     * @return SecurityFilterChain 安全过滤器链配置
     * @throws Exception 配置过程中的异常
     * @since 1.0.0
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
        // 配置HTTP安全策略
            http
                // 禁用CSRF保护 - REST API通常不需要CSRF保护
                .csrf(csrf -> csrf.disable())
                // 配置无状态会话管理 - 适用于JWT认证场景
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 配置请求授权规则
                .authorizeHttpRequests((authz) -> authz
                        // 临时放行所有接口，方便开发测试
                        .anyRequest().permitAll() 
                );
        
        // 暂时移除JWT过滤器，方便开发测试
        // http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
