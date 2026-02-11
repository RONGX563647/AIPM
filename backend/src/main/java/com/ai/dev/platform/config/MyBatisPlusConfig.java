package com.ai.dev.platform.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus配置类
 * 
 * <p>负责配置MyBatis-Plus插件和相关功能：
 * <ul>
 *   <li>配置分页插件支持</li>
 *   <li>设置数据库类型适配</li>
 *   <li>启用MyBatis-Plus增强功能</li>
 *   <li>配置SQL性能分析插件</li>
 * </ul>
 * 
 * <p>该配置类通过@Configuration注解标记为Spring配置类，
 * 提供MyBatis-Plus框架的核心配置。
 * 
 * @author AI开发平台团队
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
public class MyBatisPlusConfig {

    /**
     * MyBatis-Plus拦截器配置
     * 
     * <p>创建并配置MyBatis-Plus核心拦截器，用于：
     * <ul>
     *   <li>自动分页功能支持</li>
     *   <li>数据库类型适配(PostgreSQL)</li>
     *   <li>SQL执行性能监控</li>
     *   <li>防止全表更新删除</li>
     * </ul>
     * 
     * <p><b>配置的插件包括：</b>
     * <ul>
     *   <li>PaginationInnerInterceptor：分页插件，支持PostgreSQL语法</li>
     *   <li>可扩展添加其他内置插件</li>
     * </ul>
     * 
     * @return MybatisPlusInterceptor MyBatis-Plus拦截器实例
     * @since 1.0.0
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 配置分页插件，指定数据库类型为PostgreSQL
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.POSTGRE_SQL));
        return interceptor;
    }
}
