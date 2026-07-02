package com.livestream.message.service;

import com.livestream.message.entity.Danmaku;

import java.util.List;

/**
 * 弹幕服务接口
 */
public interface DanmakuService {
    
    /**
     * 发送弹幕
     */
    Danmaku sendDanmaku(Danmaku danmaku);
    
    /**
     * 获取直播间的弹幕历史
     */
    List<Danmaku> getRoomDanmakus(Long roomId, Integer limit);
    
    /**
     * 禁言用户
     */
    boolean muteUser(Long roomId, Long userId, Long duration);
    
    /**
     * 取消禁言
     */
    boolean unmuteUser(Long roomId, Long userId);
    
    /**
     * 检查用户是否被禁言
     */
    boolean isMuted(Long roomId, Long userId);
    
    /**
     * 删除弹幕
     */
    boolean deleteDanmaku(String danmakuId);
}
