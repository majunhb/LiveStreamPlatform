package com.livestream.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * 跨域配置
 * 安全加固：限制允许的域名列表，禁止 allowCredentials + wildcard origin
 */
@Configuration
public class CorsConfig {

    /**
     * 允许的来源域名列表，通过环境变量 CORS_ALLOWED_ORIGINS 配置
     * 多个域名用逗号分隔，例如: http://localhost:3000,https://admin.example.com
     * 开发环境默认允许 localhost 相关域名
     */
    @Value("${cors.allowed-origins:http://localhost:3000,http://localhost:5173,http://localhost:8080}")
    private String allowedOrigins;

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        
        // 从配置中读取允许的域名列表，不再使用通配符
        List<String> origins = Arrays.asList(allowedOrigins.split(","));
        origins.forEach(origin -> config.addAllowedOrigin(origin.trim()));
        
        // 允许的请求头
        config.addAllowedHeader("*");
        
        // 允许的请求方法
        config.addAllowedMethod("*");
        
        // 允许携带凭证（仅在指定了具体域名时安全）
        config.setAllowCredentials(true);
        
        // 暴露的响应头
        config.addExposedHeader("Authorization");
        
        // 预检请求缓存时间
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        
        return new CorsWebFilter(source);
    }
}
