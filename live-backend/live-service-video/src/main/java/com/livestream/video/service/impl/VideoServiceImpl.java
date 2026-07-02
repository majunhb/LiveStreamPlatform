package com.livestream.video.service.impl;

import com.livestream.common.dto.PageRequest;
import com.livestream.common.dto.PageResult;
import com.livestream.common.exception.BusinessException;
import com.livestream.common.result.ResultCode;
import com.livestream.common.util.IdUtil;
import com.livestream.video.entity.ShortVideo;
import com.livestream.video.service.VideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest as SpringPageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 视频服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {

    private final MongoTemplate mongoTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String LIKE_KEY = "video:like:";
    private static final String PLAY_KEY = "video:play:";

    @Override
    public ShortVideo uploadVideo(ShortVideo video) {
        video.setId(IdUtil.uuid());
        video.setLikeCount(0L);
        video.setCommentCount(0L);
        video.setShareCount(0L);
        video.setPlayCount(0L);
        video.setAuditStatus(0);
        video.setStatus(1);
        video.setIsTop(0);
        video.setCreateTime(LocalDateTime.now());
        video.setUpdateTime(LocalDateTime.now());
        
        mongoTemplate.save(video);
        log.info("上传视频成功: videoId={}, userId={}", video.getId(), video.getUserId());
        return video;
    }

    @Override
    public boolean updateVideo(ShortVideo video) {
        video.setUpdateTime(LocalDateTime.now());
        Query query = new Query(Criteria.where("_id").is(video.getId()));
        Update update = new Update()
                .set("title", video.getTitle())
                .set("description", video.getDescription())
                .set("tags", video.getTags())
                .set("category_id", video.getCategoryId())
                .set("update_time", video.getUpdateTime());
        
        return mongoTemplate.updateFirst(query, update, ShortVideo.class).getModifiedCount() > 0;
    }

    @Override
    public boolean deleteVideo(String videoId, Long userId) {
        Query query = new Query(Criteria.where("_id").is(videoId).and("user_id").is(userId));
        ShortVideo video = mongoTemplate.findOne(query, ShortVideo.class);
        if (video == null) {
            throw new BusinessException(ResultCode.VIDEO_NOT_FOUND);
        }
        
        // 软删除
        Update update = new Update().set("status", 2).set("update_time", LocalDateTime.now());
        return mongoTemplate.updateFirst(query, update, ShortVideo.class).getModifiedCount() > 0;
    }

    @Override
    public ShortVideo getVideoDetail(String videoId) {
        Query query = new Query(Criteria.where("_id").is(videoId));
        return mongoTemplate.findOne(query, ShortVideo.class);
    }

    @Override
    public PageResult<ShortVideo> getUserVideos(Long userId, PageRequest request) {
        Query query = new Query(Criteria.where("user_id").is(userId).and("status").is(1));
        query.with(SpringPageRequest.of(request.getPageNum() - 1, request.getPageSize()));
        query.fields().exclude("description");
        
        List<ShortVideo> videos = mongoTemplate.find(query, ShortVideo.class);
        long total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), ShortVideo.class);
        
        return PageResult.of(videos, total, request.getPageNum(), request.getPageSize());
    }

    @Override
    public boolean publishVideo(String videoId, Long userId) {
        Query query = new Query(Criteria.where("_id").is(videoId).and("user_id").is(userId));
        Update update = new Update()
                .set("status", 1)
                .set("audit_status", 1)
                .set("update_time", LocalDateTime.now());
        
        return mongoTemplate.updateFirst(query, update, ShortVideo.class).getModifiedCount() > 0;
    }

    @Override
    public boolean unpublishVideo(String videoId, Long userId) {
        Query query = new Query(Criteria.where("_id").is(videoId).and("user_id").is(userId));
        Update update = new Update().set("status", 2).set("update_time", LocalDateTime.now());
        return mongoTemplate.updateFirst(query, update, ShortVideo.class).getModifiedCount() > 0;
    }

    @Override
    public boolean likeVideo(String videoId, Long userId) {
        String key = LIKE_KEY + videoId + ":" + userId;
        Boolean isLiked = redisTemplate.hasKey(key);
        if (Boolean.TRUE.equals(isLiked)) {
            return false;
        }
        
        redisTemplate.opsForValue().set(key, "1");
        redisTemplate.opsForZSet().incrementScore(LIKE_KEY + videoId, userId.toString(), 1);
        
        Query query = new Query(Criteria.where("_id").is(videoId));
        mongoTemplate.updateFirst(query, new Update().inc("like_count", 1), ShortVideo.class);
        
        return true;
    }

    @Override
    public boolean unlikeVideo(String videoId, Long userId) {
        String key = LIKE_KEY + videoId + ":" + userId;
        Boolean isLiked = redisTemplate.hasKey(key);
        if (!Boolean.TRUE.equals(isLiked)) {
            return false;
        }
        
        redisTemplate.delete(key);
        redisTemplate.opsForZSet().incrementScore(LIKE_KEY + videoId, userId.toString(), -1);
        
        Query query = new Query(Criteria.where("_id").is(videoId));
        mongoTemplate.updateFirst(query, new Update().inc("like_count", -1), ShortVideo.class);
        
        return true;
    }

    @Override
    public void incrementPlayCount(String videoId) {
        redisTemplate.opsForValue().increment(PLAY_KEY + videoId);
    }
}
