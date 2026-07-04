package com.livestream.message.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.livestream.common.result.R;
import com.livestream.message.entity.Danmaku;
import com.livestream.message.service.DanmakuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 弹幕WebSocket处理器
 * 包含连接数限制、消息频率限制、XSS过滤、内容长度限制等防护措施
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DanmakuWebSocket extends TextWebSocketHandler {

    private final DanmakuService danmakuService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /** 直播间ID -> WebSocket会话集合 */
    private final Map<Long, CopyOnWriteArraySet<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();
    
    /** 会话ID -> 直播间ID */
    private final Map<String, Long> sessionRooms = new ConcurrentHashMap<>();

    /** 全局连接数计数器 */
    private static final AtomicInteger connectionCount = new AtomicInteger(0);

    /** 最大连接数 */
    private static final int MAX_CONNECTIONS = 1000;

    /** 每个会话的最后一次消息时间戳（用于频率限制） */
    private final ConcurrentHashMap<String, Long> lastMessageTimeMap = new ConcurrentHashMap<>();

    /** 消息最小间隔（毫秒） */
    private static final long MIN_MESSAGE_INTERVAL_MS = 1000L;

    /** 弹幕最大长度 */
    private static final int MAX_DANMAKU_LENGTH = 200;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 连接数上限检查
        int current = connectionCount.incrementAndGet();
        if (current > MAX_CONNECTIONS) {
            connectionCount.decrementAndGet();
            log.warn("WebSocket连接数已达上限({}), 拒绝新连接: sessionId={}", MAX_CONNECTIONS, session.getId());
            session.close(CloseStatus.SERVICE_RESTARTED);
            return;
        }

        // 从URL参数获取直播间ID
        String query = session.getUri().getQuery();
        if (query != null && query.contains("roomId=")) {
            String roomIdStr = query.split("roomId=")[1].split("&")[0];
            Long roomId = Long.parseLong(roomIdStr);
            
            // 加入直播间会话集合
            roomSessions.computeIfAbsent(roomId, k -> new CopyOnWriteArraySet<>()).add(session);
            sessionRooms.put(session.getId(), roomId);
            
            log.info("WebSocket连接建立: sessionId={}, roomId={}, 当前连接数={}", session.getId(), roomId, current);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String sessionId = session.getId();

        // ---- 消息频率限制 ----
        long now = System.currentTimeMillis();
        Long lastTime = lastMessageTimeMap.get(sessionId);
        if (lastTime != null && (now - lastTime) < MIN_MESSAGE_INTERVAL_MS) {
            log.debug("消息频率过快，丢弃: sessionId={}, interval={}ms", sessionId, now - lastTime);
            return;
        }
        lastMessageTimeMap.put(sessionId, now);

        String payload = message.getPayload();
        
        try {
            // 解析弹幕消息
            Danmaku danmaku = objectMapper.readValue(payload, Danmaku.class);

            // ---- 内容长度限制 ----
            if (danmaku.getContent() != null && danmaku.getContent().length() > MAX_DANMAKU_LENGTH) {
                danmaku.setContent(danmaku.getContent().substring(0, MAX_DANMAKU_LENGTH));
                log.info("弹幕内容超长，已截断至{}字符: sessionId={}", MAX_DANMAKU_LENGTH, sessionId);
            }

            // ---- XSS过滤 ----
            if (danmaku.getContent() != null) {
                danmaku.setContent(sanitizeHtml(danmaku.getContent()));
            }
            if (danmaku.getUsername() != null) {
                danmaku.setUsername(sanitizeHtml(danmaku.getUsername()));
            }
            
            // 保存弹幕
            Danmaku savedDanmaku = danmakuService.sendDanmaku(danmaku);
            
            // 广播弹幕到同直播间的所有用户
            broadcastToRoom(savedDanmaku.getRoomId(), savedDanmaku);
            
            log.debug("弹幕发送成功: danmakuId={}", savedDanmaku.getId());
        } catch (Exception e) {
            log.error("处理弹幕消息失败", e);
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(
                    R.fail(500, "发送失败: " + e.getMessage()))));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 更新连接计数
        int remaining = connectionCount.decrementAndGet();

        // 清理频率限制记录
        lastMessageTimeMap.remove(session.getId());

        Long roomId = sessionRooms.remove(session.getId());
        if (roomId != null) {
            CopyOnWriteArraySet<WebSocketSession> sessions = roomSessions.get(roomId);
            if (sessions != null) {
                sessions.remove(session);
                if (sessions.isEmpty()) {
                    roomSessions.remove(roomId);
                }
            }
            log.info("WebSocket连接关闭: sessionId={}, roomId={}, 剩余连接数={}", session.getId(), roomId, remaining);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("WebSocket传输错误: sessionId={}", session.getId(), exception);
        afterConnectionClosed(session, CloseStatus.SERVER_ERROR);
    }

    /**
     * 广播消息到指定直播间的所有用户
     */
    public void broadcastToRoom(Long roomId, Danmaku danmaku) {
        CopyOnWriteArraySet<WebSocketSession> sessions = roomSessions.get(roomId);
        if (sessions != null) {
            String message;
            try {
                message = objectMapper.writeValueAsString(danmaku);
            } catch (Exception e) {
                log.error("序列化弹幕消息失败", e);
                return;
            }
            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    try {
                        session.sendMessage(new TextMessage(message));
                    } catch (IOException e) {
                        log.error("发送消息失败: sessionId={}", session.getId(), e);
                    }
                }
            }
        }
    }

    /**
     * 获取指定直播间的在线人数
     */
    public int getRoomOnlineCount(Long roomId) {
        CopyOnWriteArraySet<WebSocketSession> sessions = roomSessions.get(roomId);
        return sessions == null ? 0 : sessions.size();
    }

    /**
     * HTML特殊字符转义，防止XSS攻击
     */
    private String sanitizeHtml(String input) {
        if (input == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(input.length());
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            switch (c) {
                case '&':
                    sb.append("&amp;");
                    break;
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '"':
                    sb.append("&quot;");
                    break;
                case '\'':
                    sb.append("&#39;");
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }
}
