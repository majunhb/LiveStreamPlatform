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

/**
 * 弹幕WebSocket处理器
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

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 从URL参数获取直播间ID
        String query = session.getUri().getQuery();
        if (query != null && query.contains("roomId=")) {
            String roomIdStr = query.split("roomId=")[1].split("&")[0];
            Long roomId = Long.parseLong(roomIdStr);
            
            // 加入直播间会话集合
            roomSessions.computeIfAbsent(roomId, k -> new CopyOnWriteArraySet<>()).add(session);
            sessionRooms.put(session.getId(), roomId);
            
            log.info("WebSocket连接建立: sessionId={}, roomId={}", session.getId(), roomId);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        
        try {
            // 解析弹幕消息
            Danmaku danmaku = objectMapper.readValue(payload, Danmaku.class);
            
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
        Long roomId = sessionRooms.remove(session.getId());
        if (roomId != null) {
            CopyOnWriteArraySet<WebSocketSession> sessions = roomSessions.get(roomId);
            if (sessions != null) {
                sessions.remove(session);
                if (sessions.isEmpty()) {
                    roomSessions.remove(roomId);
                }
            }
            log.info("WebSocket连接关闭: sessionId={}, roomId={}", session.getId(), roomId);
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
            String message = objectMapper.writeValueAsString(danmaku);
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
}
