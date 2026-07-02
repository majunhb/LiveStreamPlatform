package com.livestream.video.controller;

import com.livestream.common.dto.PageRequest;
import com.livestream.common.dto.PageResult;
import com.livestream.common.result.R;
import com.livestream.video.entity.ShortVideo;
import com.livestream.video.service.FeedService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 推荐Feed控制器
 */
@Slf4j
@RestController
@RequestMapping("/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    /**
     * 获取推荐视频列表
     */
    @GetMapping("/recommend")
    public R<PageResult<ShortVideo>> getRecommendFeed(PageRequest request) {
        return R.success(feedService.getRecommendFeed(request));
    }

    /**
     * 获取关注用户的最新视频
     */
    @GetMapping("/follow")
    public R<PageResult<ShortVideo>> getFollowFeed(PageRequest request, HttpServletRequest httpRequest) {
        Long userId = Long.valueOf(httpRequest.getHeader("X-User-Id"));
        return R.success(feedService.getFollowFeed(userId, request));
    }

    /**
     * 获取热门视频列表
     */
    @GetMapping("/hot")
    public R<PageResult<ShortVideo>> getHotVideos(PageRequest request) {
        return R.success(feedService.getHotVideos(request));
    }

    /**
     * 搜索视频
     */
    @GetMapping("/search")
    public R<PageResult<ShortVideo>> searchVideos(@RequestParam String keyword, PageRequest request) {
        return R.success(feedService.searchVideos(keyword, request));
    }

    /**
     * 获取分类视频
     */
    @GetMapping("/category/{categoryId}")
    public R<List<ShortVideo>> getVideosByCategory(@PathVariable Long categoryId, PageRequest request) {
        return R.success(feedService.getVideosByCategory(categoryId, request));
    }
}
