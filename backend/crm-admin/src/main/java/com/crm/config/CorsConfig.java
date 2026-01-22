package com.crm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * CORS 跨域配置类
 * 允许前端跨域访问后端 API
 *
 * @author CRM System
 * @since 1.0.0
 */
@Configuration
public class CorsConfig {

    /**
     * 配置 CORS 过滤器
     * 允许所有来源、方法和头部的跨域请求
     *
     * @return CorsFilter
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 允许所有来源
        config.addAllowedOriginPattern("*");
        // 允许所有头部
        config.addAllowedHeader("*");
        // 允许所有方法
        config.addAllowedMethod("*");
        // 允许发送 Cookie
        config.setAllowCredentials(true);
        // 预检请求的有效期，单位为秒
        config.setMaxAge(3600L);
        // 暴露响应头
        config.addExposedHeader("Authorization");
        config.addExposedHeader("Content-Disposition");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
