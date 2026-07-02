package com.livestream.live.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.livestream.common.dto.PageRequest;
import com.livestream.common.dto.PageResult;
import com.livestream.live.entity.LiveRoom;

/**
 * 直播间服务接口
 */
public interface LiveRoomService extends IService<LiveRoom> {
    
    /**
     * 创建直播间
     */
    LiveRoom createRoom(Long userId, String title, String coverUrl, String description);
    
    /**
     * 更新直播间信息
     */
    boolean updateRoom(LiveRoom room);
    
    /**
     * 开始直播
     */
    LiveRoom startLive(Long roomId, Long userId);
    
    /**
     * 结束直播
     */
    boolean endLive(Long roomId, Long userId);
    
    /**
     * 获取直播间详情
     */
    LiveRoom getRoomDetail(Long roomId);
    
    /**
     * 获取用户的直播间
     */
    LiveRoom getUserRoom(Long userId);
    
    /**
     * 分页查询直播间列表
     */
    PageResult<LiveRoom> pageQuery(PageRequest request);
    
    /**
     * 获取正在直播的列表
     */
    PageResult<LiveRoom> getLivingRooms(PageRequest request);
    
    /**
     * 更新观看人数
     */
    void updateViewerCount(Long roomId, Integer count);
    
    /**
     * 添加观看人数
     */
    void incrementViewerCount(Long roomId);
    
    /**
     * 减少观看人数
     */
    void decrementViewerCount(Long roomId);
}
