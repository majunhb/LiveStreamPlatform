package com.livestream.video.service;

import com.livestream.common.dto.PageRequest;
import com.livestream.common.dto.PageResult;
import com.livestream.video.entity.ShortVideo;

/**
 * 视频服务接口
 */
public interface VideoService {
    
    /**
     * 上传视频
     */
    ShortVideo uploadVideo(ShortVideo video);
    
    /**
     * 更新视频信息
     */
    boolean updateVideo(ShortVideo video);
    
    /**
     * 删除视频
     */
    boolean deleteVideo(String videoId, Long userId);
    
    /**
     * 获取视频详情
     */
    ShortVideo getVideoDetail(String videoId);
    
    /**
     * 分页查询用户视频
     */
    PageResult<ShortVideo> getUserVideos(Long userId, PageRequest request);
    
    /**
     * 发布视频
     */
    boolean publishVideo(String videoId, Long userId);
    
    /**
     * 下架视频
     */
    boolean unpublishVideo(String videoId, Long userId);
    
    /**
     * 点赞视频
     */
    boolean likeVideo(String videoId, Long userId);
    
    /**
     * 取消点赞
     */
    boolean unlikeVideo(String videoId, Long userId);
    
    /**
     * 增加播放量
     */
    void incrementPlayCount(String videoId);
}
