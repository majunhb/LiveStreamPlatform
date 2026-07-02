package com.livestream.admin.service;

import com.livestream.common.dto.PageRequest;
import com.livestream.common.dto.PageResult;

/**
 * 管理后台服务接口
 */
public interface AdminService {
    
    /**
     * 获取用户统计信息
     */
    Object getUserStatistics();
    
    /**
     * 获取直播统计信息
     */
    Object getLiveStatistics();
    
    /**
     * 获取视频统计信息
     */
    Object getVideoStatistics();
    
    /**
     * 获取财务统计信息
     */
    Object getFinanceStatistics();
    
    /**
     * 禁用用户
     */
    boolean disableUser(Long userId);
    
    /**
     * 启用用户
     */
    boolean enableUser(Long userId);
    
    /**
     * 封禁直播间
     */
    boolean banLiveRoom(Long roomId, String reason);
    
    /**
     * 解封直播间
     */
    boolean unbanLiveRoom(Long roomId);
    
    /**
     * 审核视频通过
     */
    boolean approveVideo(String videoId);
    
    /**
     * 审核视频拒绝
     */
    boolean rejectVideo(String videoId, String reason);
}
