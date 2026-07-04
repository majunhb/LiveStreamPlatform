package com.livestream.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JwtUtil 单元测试
 */
class JwtUtilTest {

    @Test
    @DisplayName("generateToken 应生成有效 token，且可解析出正确的 userId")
    void testGenerateToken_validToken() {
        Long userId = 100L;
        String username = "testuser";
        String role = "USER";

        String token = JwtUtil.generateToken(userId, username, role);

        assertNotNull(token, "生成的 token 不应为 null");
        assertFalse(token.isEmpty(), "token 不应为空字符串");

        // 解析验证
        Long parsedUserId = JwtUtil.getUserId(token);
        assertEquals(userId, parsedUserId, "解析出的 userId 应一致");

        String parsedUsername = JwtUtil.getUsername(token);
        assertEquals(username, parsedUsername, "解析出的 username 应一致");

        String parsedRole = JwtUtil.getRole(token);
        assertEquals(role, parsedRole, "解析出的 role 应一致");
    }

    @Test
    @DisplayName("getUserId 应正确解析 token 中的 userId")
    void testGetUserIdFromToken() {
        Long userId = 42L;
        String token = JwtUtil.generateToken(userId, "alice", "ADMIN");
        Long result = JwtUtil.getUserId(token);
        assertEquals(userId, result);
    }

    @Test
    @DisplayName("过期 token 应抛出异常或 validateToken 返回 false")
    void testExpiredToken() {
        // 生成一个立即过期的 token（过期时间设为 0 毫秒）
        String token = JwtUtil.generateToken(1L, "expired_user", "USER", 0L);

        // validateToken 应返回 false
        assertFalse(JwtUtil.validateToken(token), "过期 token 的 validateToken 应返回 false");

        // parseToken 应抛出 RuntimeException（包装了 ExpiredJwtException）
        assertThrows(RuntimeException.class, () -> JwtUtil.parseToken(token),
                "过期 token 解析应抛出 RuntimeException");
    }

    @Test
    @DisplayName("无效 token 字符串应导致 validateToken 返回 false")
    void testInvalidToken() {
        assertFalse(JwtUtil.validateToken("not.a.valid.token"));
        assertThrows(RuntimeException.class, () -> JwtUtil.getUserId("garbage"));
    }

    @Test
    @DisplayName("兼容旧接口 generateToken(userId, username) 应使用默认角色 USER")
    void testLegacyGenerateToken() {
        String token = JwtUtil.generateToken(99L, "legacy_user");
        assertNotNull(token);
        assertEquals("USER", JwtUtil.getRole(token));
        assertEquals(99L, JwtUtil.getUserId(token));
    }
}
