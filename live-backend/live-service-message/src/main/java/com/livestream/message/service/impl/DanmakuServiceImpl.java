package com.livestream.message.service.impl;

import com.livestream.common.exception.BusinessException;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import com.livestream.common.result.ResultCode;
import com.livestream.common.util.IdUtil;
import com.livestream.message.entity.Danmaku;
import com.livestream.message.service.DanmakuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 弹幕服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DanmakuServiceImpl implements DanmakuService {

    private final MongoTemplate mongoTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    
    private static final String MUTE_KEY = "mute:room:";
    private static final String DANMAKU_FREQUENCY_KEY = "danmaku:frequency:";

    @Override
    public Danmaku sendDanmaku(Danmaku danmaku) {
        // 检查用户是否被禁言
        if (isMuted(danmaku.getRoomId(), danmaku.getUserId())) {
            throw new BusinessException(ResultCode.DANMAKU_BANNED);
        }
        
        // 检查发送频率（10秒内最多发5条）
        String frequencyKey = DANMAKU_FREQUENCY_KEY + danmaku.getUserId();
        Long count = redisTemplate.opsForValue().increment(frequencyKey);
        if (count != null && count == 1) {
            redisTemplate.expire(frequencyKey, 10, TimeUnit.SECONDS);
        }
        if (count != null && count > 5) {
            throw new BusinessException(ResultCode.DANMAKU_FREQUENT);
        }
        
        // C-07安全修复：XSS过滤，使用Jsoup清除所有HTML标签
        String cleanContent = Jsoup.clean(danmaku.getContent(), Safelist.none());
        if (cleanContent == null || cleanContent.isBlank()) {
            throw new BusinessException("弹幕内容不能为空");
        }
        if (cleanContent.length() > 200) {
            throw new BusinessException("弹幕内容过长，最多200字");
        }
        danmaku.setContent(cleanContent);
        
        // 保存弹幕
        danmaku.setId(IdUtil.uuid());
        danmaku.setSendTime(LocalDateTime.now());
        danmaku.setVisible(1);
        mongoTemplate.save(danmaku);
        
        log.debug("发送弹幕: danmakuId={}, roomId={}, userId={}", 
                danmaku.getId(), danmaku.getRoomId(), danmaku.getUserId());
        return danmaku;
    }

    @Override
    public List<Danmaku> getRoomDanmakus(Long roomId, Integer limit) {
        limit = limit == null ? 100 : limit;
        Query query = new Query(Criteria.where("room_id").is(roomId).and("visible").is(1));
        query.with(Sort.by(Sort.Direction.DESC, "send_time"));
        query.limit(limit);
        return mongoTemplate.find(query, Danmaku.class);
    }

    @Override
    public boolean muteUser(Long roomId, Long userId, Long duration) {
        String key = MUTE_KEY + roomId + ":" + userId;
        redisTemplate.opsForValue().set(key, "1", duration, TimeUnit.SECONDS);
        log.info("禁言用户: roomId={}, userId={}, duration={}s", roomId, userId, duration);
        return true;
    }

    @Override
    public boolean unmuteUser(Long roomId, Long userId) {
        String key = MUTE_KEY + roomId + ":" + userId;
        redisTemplate.delete(key);
        log.info("取消禁言: roomId={}, userId={}", roomId, userId);
        return true;
    }

    @Override
    public boolean isMuted(Long roomId, Long userId) {
        String key = MUTE_KEY + roomId + ":" + userId;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    @Override
    public boolean deleteDanmaku(String danmakuId) {
        Query query = new Query(Criteria.where("_id").is(danmakuId));
        Update update = new Update().set("visible", 0);
        return mongoTemplate.updateFirst(query, update, Danmaku.class).getModifiedCount() > 0;
    }
}
