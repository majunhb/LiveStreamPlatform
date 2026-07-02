package com.livestream.admin.service.impl;

import com.livestream.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 管理后台服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final MongoTemplate mongoTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Object getUserStatistics() {
        // 返回模拟统计数据
        return java.util.Map.of(
                "totalUsers", 10000L,
                "activeUsers", 5000L,
                "newUsersToday", 100L,
                "anchorCount", 500L
        );
    }

    @Override
    public Object getLiveStatistics() {
        return java.util.Map.of(
                "totalRooms", 500L,
                "livingRooms", 50L,
                "totalViewers", 10000L,
                "todayLiveTime", 36000L
        );
    }

    @Override
    public Object getVideoStatistics() {
        // 查询MongoDB中的视频统计
        Query query = new Query();
        long totalVideos = mongoTemplate.count(query, "t_short_video");
        
        Query activeQuery = new Query(Criteria.where("status").is(1));
        long activeVideos = mongoTemplate.count(activeQuery, "t_short_video");
        
        return java.util.Map.of(
                "totalVideos", totalVideos,
                "activeVideos", activeVideos,
                "pendingVideos", 100L,
                "totalPlays", 1000000L
        );
    }

    @Override
    public Object getFinanceStatistics() {
        return java.util.Map.of(
                "totalRecharge", 500000L,
                "todayRecharge", 10000L,
                "totalConsume", 400000L,
                "totalWithdraw", 200000L
        );
    }

    @Override
    public boolean disableUser(Long userId) {
        log.info("禁用用户: userId={}", userId);
        return true;
    }

    @Override
    public boolean enableUser(Long userId) {
        log.info("启用用户: userId={}", userId);
        return true;
    }

    @Override
    public boolean banLiveRoom(Long roomId, String reason) {
        log.info("封禁直播间: roomId={}, reason={}", roomId, reason);
        // 实际需要更新MySQL中的直播间状态
        // 并通知直播服务关闭直播间
        return true;
    }

    @Override
    public boolean unbanLiveRoom(Long roomId) {
        log.info("解封直播间: roomId={}", roomId);
        return true;
    }

    @Override
    public boolean approveVideo(String videoId) {
        log.info("审核通过视频: videoId={}", videoId);
        // 更新MongoDB中的视频审核状态
        Query query = new Query(Criteria.where("_id").is(videoId));
        Update update = new Update().set("audit_status", 1);
        mongoTemplate.updateFirst(query, update, "t_short_video");
        return true;
    }

    @Override
    public boolean rejectVideo(String videoId, String reason) {
        log.info("审核拒绝视频: videoId={}, reason={}", videoId, reason);
        Query query = new Query(Criteria.where("_id").is(videoId));
        Update update = new Update().set("audit_status", 2).set("audit_reason", reason);
        mongoTemplate.updateFirst(query, update, "t_short_video");
        return true;
    }
}
