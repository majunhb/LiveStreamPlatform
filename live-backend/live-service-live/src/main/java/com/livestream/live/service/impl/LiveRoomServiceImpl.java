package com.livestream.live.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.livestream.common.dto.PageRequest;
import com.livestream.common.dto.PageResult;
import com.livestream.common.exception.BusinessException;
import com.livestream.common.result.ResultCode;
import com.livestream.common.util.IdUtil;
import com.livestream.live.entity.LiveRoom;
import com.livestream.live.mapper.LiveRoomMapper;
import com.livestream.live.service.LiveRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 直播间服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LiveRoomServiceImpl extends ServiceImpl<LiveRoomMapper, LiveRoom> implements LiveRoomService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String VIEWER_COUNT_KEY = "live:viewer:";
    private static final String ROOM_STATUS_KEY = "live:status:";

    @Override
    public LiveRoom createRoom(Long userId, String title, String coverUrl, String description) {
        // 检查是否已有直播间
        LiveRoom existRoom = getUserRoom(userId);
        if (existRoom != null) {
            throw new BusinessException("用户已存在直播间");
        }
        
        LiveRoom room = new LiveRoom();
        room.setUserId(userId);
        room.setTitle(title);
        room.setCoverUrl(coverUrl);
        room.setDescription(description);
        room.setStatus(0);
        room.setViewerCount(0);
        room.setTotalViewerCount(0L);
        room.setLikeCount(0L);
        
        // 生成流地址
        String streamId = IdUtil.uuid();
        room.setStreamKey(streamId);
        room.setStreamUrl("rtmp://live.example.com/live/" + streamId);
        room.setPlayUrl("http://live.example.com/live/" + streamId + "/playlist.m3u8");
        
        save(room);
        log.info("创建直播间成功: roomId={}, userId={}", room.getId(), userId);
        return room;
    }

    @Override
    public boolean updateRoom(LiveRoom room) {
        LiveRoom existRoom = getById(room.getId());
        if (existRoom == null) {
            throw new BusinessException(ResultCode.LIVE_ROOM_NOT_FOUND);
        }
        
        // 只允许更新部分字段
        if (room.getTitle() != null) existRoom.setTitle(room.getTitle());
        if (room.getCoverUrl() != null) existRoom.setCoverUrl(room.getCoverUrl());
        if (room.getDescription() != null) existRoom.setDescription(room.getDescription());
        if (room.getCategoryId() != null) existRoom.setCategoryId(room.getCategoryId());
        
        return updateById(existRoom);
    }

    @Override
    public LiveRoom startLive(Long roomId, Long userId) {
        LiveRoom room = getById(roomId);
        if (room == null) {
            throw new BusinessException(ResultCode.LIVE_ROOM_NOT_FOUND);
        }
        
        if (!room.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.NOT_LIVE_OWNER);
        }
        
        if (room.getStatus() == 1) {
            throw new BusinessException("直播间已在直播中");
        }
        
        room.setStatus(1);
        room.setStartTime(LocalDateTime.now());
        room.setViewerCount(0);
        updateById(room);
        
        // 缓存直播间状态
        redisTemplate.opsForValue().set(ROOM_STATUS_KEY + roomId, "1");
        
        log.info("开始直播: roomId={}, userId={}", roomId, userId);
        return room;
    }

    @Override
    public boolean endLive(Long roomId, Long userId) {
        LiveRoom room = getById(roomId);
        if (room == null) {
            throw new BusinessException(ResultCode.LIVE_ROOM_NOT_FOUND);
        }
        
        if (!room.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.NOT_LIVE_OWNER);
        }
        
        room.setStatus(2);
        room.setEndTime(LocalDateTime.now());
        room.setViewerCount(0);
        updateById(room);
        
        // 清除缓存
        redisTemplate.delete(ROOM_STATUS_KEY + roomId);
        redisTemplate.delete(VIEWER_COUNT_KEY + roomId);
        
        log.info("结束直播: roomId={}, userId={}", roomId, userId);
        return true;
    }

    @Override
    public LiveRoom getRoomDetail(Long roomId) {
        return getById(roomId);
    }

    @Override
    public LiveRoom getUserRoom(Long userId) {
        return getOne(new LambdaQueryWrapper<LiveRoom>()
                .eq(LiveRoom::getUserId, userId)
                .orderByDesc(LiveRoom::getCreateTime)
                .last("LIMIT 1"));
    }

    @Override
    public PageResult<LiveRoom> pageQuery(PageRequest request) {
        Page<LiveRoom> page = new Page<>(request.getPageNum(), request.getPageSize());
        
        LambdaQueryWrapper<LiveRoom> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(LiveRoom::getCreateTime);
        
        Page<LiveRoom> result = page(page, queryWrapper);
        return PageResult.of(result.getRecords(), result.getTotal(),
                (int) result.getCurrent(), (int) result.getSize());
    }

    @Override
    public PageResult<LiveRoom> getLivingRooms(PageRequest request) {
        Page<LiveRoom> page = new Page<>(request.getPageNum(), request.getPageSize());
        
        LambdaQueryWrapper<LiveRoom> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LiveRoom::getStatus, 1)
                .orderByDesc(LiveRoom::getIsRecommend)
                .orderByDesc(LiveRoom::getViewerCount)
                .orderByDesc(LiveRoom::getCreateTime);
        
        Page<LiveRoom> result = page(page, queryWrapper);
        return PageResult.of(result.getRecords(), result.getTotal(),
                (int) result.getCurrent(), (int) result.getSize());
    }

    @Override
    public void updateViewerCount(Long roomId, Integer count) {
        LiveRoom room = getById(roomId);
        if (room != null) {
            room.setViewerCount(count);
            updateById(room);
        }
    }

    @Override
    public void incrementViewerCount(Long roomId) {
        redisTemplate.opsForValue().increment(VIEWER_COUNT_KEY + roomId);
    }

    @Override
    public void decrementViewerCount(Long roomId) {
        redisTemplate.opsForValue().decrement(VIEWER_COUNT_KEY + roomId);
    }
}
