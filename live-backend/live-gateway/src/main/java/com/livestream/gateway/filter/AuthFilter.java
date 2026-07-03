package com.livestream.gateway.filter;

import com.livestream.common.result.R;
import com.livestream.common.result.ResultCode;
import com.livestream.common.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * JWT鉴权过滤器
 * 对需要认证的请求进行Token校验，并对管理后台路径进行角色权限校验
 */
@Slf4j
@Component
public class AuthFilter implements GlobalFilter, Ordered {

    /** 过滤器优先级，越小越先执行 */
    private static final int ORDER = -100;
    
    /** 无需认证的路径 */
    private static final List<String> WHITE_LIST = Arrays.asList(
            "/auth/login",
            "/auth/register",
            "/auth/captcha",
            "/user/check-username",
            "/user/check-phone",
            "/doc.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/webjars/**",
            "/favicon.ico",
            "/static/**",
            "/actuator/**"
    );

    /** 管理后台路径模式（需要ADMIN角色） */
    private static final List<String> ADMIN_PATH_PATTERNS = Arrays.asList(
            "/admin/**"
    );

    /** 路径匹配器 */
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        
        log.debug("请求路径: {}", path);
        
        // 白名单路径直接放行
        if (isWhitePath(path)) {
            return chain.filter(exchange);
        }
        
        // 获取Token
        String token = extractToken(request);
        
        // Token为空
        if (!StringUtils.hasText(token)) {
            log.warn("路径 {} 需要认证，但未提供Token", path);
            return unauthorized(exchange.getResponse(), ResultCode.UNAUTHORIZED.getCode(), "请先登录");
        }
        
        // 验证Token
        try {
            Claims claims = JwtUtil.parseToken(token);
            Long userId = claims.get("userId", Long.class);
            String username = claims.getSubject();
            String role = claims.get("role", String.class);
            
            // 管理后台路径需要ADMIN角色校验
            if (isAdminPath(path)) {
                if (!"ADMIN".equals(role)) {
                    log.warn("用户 {} 尝试访问管理后台路径 {}，但角色为 {}，拒绝访问", username, path, role);
                    return forbidden(exchange.getResponse(), ResultCode.FORBIDDEN.getCode(), "无权限访问管理后台");
                }
                log.debug("管理员 {} 访问管理后台路径: {}", username, path);
            }
            
            // 将用户信息传递给下游服务
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Id", String.valueOf(userId))
                    .header("X-Username", username)
                    .header("X-User-Role", role != null ? role : "USER")
                    .build();
            
            log.debug("用户 {} 认证通过，userId: {}, role: {}", username, userId, role);
            return chain.filter(exchange.mutate().request(modifiedRequest).build());
            
        } catch (Exception e) {
            log.warn("Token验证失败: {}", e.getMessage());
            
            if (e.getMessage().contains("过期")) {
                return unauthorized(exchange.getResponse(), ResultCode.TOKEN_EXPIRED.getCode(), "Token已过期，请重新登录");
            }
            return unauthorized(exchange.getResponse(), ResultCode.TOKEN_INVALID.getCode(), "Token无效");
        }
    }

    /**
     * 判断路径是否在白名单中
     */
    private boolean isWhitePath(String path) {
        return WHITE_LIST.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    /**
     * 判断路径是否为管理后台路径
     */
    private boolean isAdminPath(String path) {
        return ADMIN_PATH_PATTERNS.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    /**
     * 从请求头提取Token
     */
    private String extractToken(ServerHttpRequest request) {
        // 优先从Authorization头获取
        String authHeader = request.getHeaders().getFirst(JwtUtil.getHeaderKey());
        if (StringUtils.hasText(authHeader)) {
            if (authHeader.startsWith(JwtUtil.getTokenPrefix())) {
                return authHeader.substring(JwtUtil.getTokenPrefix().length());
            }
            return authHeader;
        }
        
        // 其次从请求参数获取
        String token = request.getQueryParams().getFirst("token");
        if (StringUtils.hasText(token)) {
            return token;
        }
        
        return null;
    }

    /**
     * 返回未授权响应（401）
     */
    private Mono<Void> unauthorized(ServerHttpResponse response, int code, String message) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        
        R<Void> result = R.fail(code, message);
        byte[] bytes = result.toString().getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        
        return response.writeWith(Mono.just(buffer));
    }

    /**
     * 返回禁止访问响应（403）
     */
    private Mono<Void> forbidden(ServerHttpResponse response, int code, String message) {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        
        R<Void> result = R.fail(code, message);
        byte[] bytes = result.toString().getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}
