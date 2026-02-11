package com.ai.dev.platform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate配置类
 * 
 * <p>负责配置RestTemplate Bean，用于HTTP客户端请求：
 * <ul>
 *   <li>配置默认的RestTemplate实例</li>
 *   <li>设置HTTP连接超时参数</li>
 *   <li>配置请求拦截器和错误处理器</li>
 *   <li>支持微服务间通信</li>
 * </ul>
 * 
 * <p>该配置类通过@Configuration注解标记为Spring配置类，
 * 提供全局可用的RestTemplate Bean实例。
 * 
 * @author AI开发平台团队
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
public class RestTemplateConfig {

    /**
     * RestTemplate Bean配置
     * 
     * <p>创建并配置RestTemplate实例，用于：
     * <ul>
     *   <li>发送HTTP请求到外部服务</li>
     *   <li>处理RESTful API调用</li>
     *   <li>支持GET、POST、PUT、DELETE等HTTP方法</li>
     *   <li>自动处理JSON/XML数据转换</li>
     * </ul>
     * 
     * <p>可根据需要进一步配置：
     * <ul>
     *   <li>连接超时和读取超时设置</li>
     *   <li>SSL证书验证配置</li>
     *   <li>请求头和拦截器添加</li>
     *   <li>错误处理策略配置</li>
     * </ul>
     * 
     * @return RestTemplate 配置好的RestTemplate实例
     * @since 1.0.0
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
