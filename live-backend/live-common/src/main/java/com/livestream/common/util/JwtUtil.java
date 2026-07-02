package com.livestream.common.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 */
@Slf4j
public class JwtUtil {

    /** 秘钥（实际使用时从配置读取） */
    private static final String SECRET = "livestream-platform-secret-key-must-be-at-least-256-bits-long";
    
    /** 过期时间（毫秒）- 默认7天 */
    private static final long EXPIRATION = 7 * 24 * 60 * 60 * 1000L;
    
    /** Token前缀 */
    private static final String TOKEN_PREFIX = "Bearer ";
    
    /** Header键 */
    private static final String HEADER_KEY = "Authorization";

    /**
     * 生成Token
     */
    public static String generateToken(Long userId, String username) {
        return generateToken(userId, username, EXPIRATION);
    }

    /**
     * 生成Token（自定义过期时间）
     */
    public static String generateToken(Long userId, String username, long expiration) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expiration);
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        
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
