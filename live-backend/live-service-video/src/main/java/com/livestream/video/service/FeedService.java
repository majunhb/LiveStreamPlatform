package com.livestream.video.service;

import com.livestream.common.dto.PageRequest;
import com.livestream.common.dto.PageResult;
import com.livestream.video.entity.ShortVideo;

import java.util.List;

/**
 * 推荐Feed服务接口
 */
public interface FeedService {
    
    /**
     * 获取推荐视频列表
     */
    PageResult<ShortVideo> getRecommendFeed(PageRequest request);
    
    /**
     * 获取关注用户的最新视频
     */
    PageResult<ShortVideo> getFollowFeed(Long userId, PageRequest request);
    
    /**
     * 获取热门视频列表
     */
    PageResult<ShortVideo> getHotVideos(PageRequest request);
    
    /**
     * 搜索视频
     */
    PageResult<ShortVideo> searchVideos(String keyword, PageRequest request);
    
    /**
     * 获取视频分类列表
     */
    List<ShortVideo> getVideosByCategory(Long categoryId, PageRequest request);
}
