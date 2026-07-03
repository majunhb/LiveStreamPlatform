package com.livestream.common.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 */
@Slf4j
public class JwtUtil {

    /** 默认过期时间（毫秒）- 7天 */
    private static final long EXPIRATION = 7 * 24 * 60 * 60 * 1000L;
    
    /** Token前缀 */
    private static final String TOKEN_PREFIX = "Bearer ";
    
    /** Header键 */
    private static final String HEADER_KEY = "Authorization";

    /** 从环境变量/系统属性读取的JWT密钥 */
    private static final String SECRET;

    static {
        // 优先从环境变量读取，其次从系统属性读取，最后生成随机密钥并告警
        String secret = System.getenv("JWT_SECRET");
        if (secret == null || secret.isBlank()) {
            secret = System.getProperty("jwt.secret");
        }
        if (secret == null || secret.isBlank()) {
            // 开发环境回退：生成一个安全的随机密钥并打印警告
            log.warn("未配置JWT_SECRET环境变量或jwt.secret系统属性，正在生成随机密钥。生产环境请务必配置安全的JWT密钥！");
            byte[] randomKey = new byte[64]; // 512 bits
            new SecureRandom().nextBytes(randomKey);
            secret = Base64.getEncoder().encodeToString(randomKey);
        }
        SECRET = secret;
        log.info("JWT密钥已加载（来源: {}）",
                System.getenv("JWT_SECRET") != null ? "环境变量JWT_SECRET" :
                System.getProperty("jwt.secret") != null ? "系统属性jwt.secret" : "随机生成");
    }

    /**
     * 生成Token（包含角色信息）
     */
    public static String generateToken(Long userId, String username, String role) {
        return generateToken(userId, username, role, EXPIRATION);
    }

    /**
     * 生成Token（自定义过期时间，包含角色信息）
     */
    public static String generateToken(Long userId, String username, String role, long expiration) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expiration);
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role", role != null ? role : "USER");
        
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(key)
                .compact();
    }

    /**
     * 兼容旧接口（无角色参数，默认角色为USER）
     */
    public static String generateToken(Long userId, String username) {
        return generateToken(userId, username, "USER", EXPIRATION);
    }

    /**
     * 兼容旧接口（无角色参数，自定义过期时间）
     */
    public static String generateToken(Long userId, String username, long expiration) {
        return generateToken(userId, username, "USER", expiration);
    }

    /**
     * 解析Token
     */
    public static Claims parseToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.warn("Token已过期: {}", e.getMessage());
            throw new RuntimeException("Token已过期");
        } catch (JwtException e) {
            log.warn("Token解析失败: {}", e.getMessage());
            throw new RuntimeException("Token无效");
        }
    }

    /**
     * 验证Token
     */
    public static boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从Token中获取用户ID
     */
    public static Long getUserId(String token) {
        Claims claims = parseToken(token);
        return claims.get("userId", Long.class);
    }

    /**
     * 从Token中获取用户名
     */
    public static String getUsername(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }

    /**
     * 从Token中获取用户角色
     */
    public static String getRole(String token) {
        Claims claims = parseToken(token);
        return claims.get("role", String.class);
    }

    /**
     * 判断Token是否过期
     */
    public static boolean isTokenExpired(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    /**
     * 获取Token前缀
     */
    public static String getTokenPrefix() {
        return TOKEN_PREFIX;
    }

    /**
     * 获取Header键
     */
    public static String getHeaderKey() {
        return HEADER_KEY;
    }
}
