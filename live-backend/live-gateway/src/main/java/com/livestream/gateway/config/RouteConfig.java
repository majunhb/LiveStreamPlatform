package com.livestream.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 路由配置
 * 配置各微服务的路由规则
 */
@Configuration
public class RouteConfig {

    /**
     * 定义路由定位器
     * 所有 /api/** 的请求会被转发到对应的微服务
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // 用户服务
                .route("user-service", r -> r
                        .path("/api/user/**", "/api/auth/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://live-service-user"))
                
                // 直播服务
                .route("live-service", r -> r
                        .path("/api/live/**", "/api/stream/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://live-service-live"))
                
                // 短视频服务
                .route("video-service", r -> r
                        .path("/api/video/**", "/api/feed/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://live-service-video"))
                
                // 消息服务（弹幕）
                .route("message-service", r -> r
                        .path("/api/message/**", "/api/danmaku/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://live-service-message"))
                
                // 礼物服务
                .route("gift-service", r -> r
                        .path("/api/gift/**", "/api/wallet/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://live-service-gift"))
                
                // 管理后台服务
                .route("admin-service", r -> r
                        .path("/api/admin/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://live-service-admin"))
                
                // 静态资源
                .route("static-resources", r -> r
                        .path("/static/**", "/favicon.ico")
                        .uri("forward:/static"))
                
                .build();
    }
}
