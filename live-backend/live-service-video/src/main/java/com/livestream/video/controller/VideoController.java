package com.livestream.video.controller;

import com.livestream.common.dto.PageRequest;
import com.livestream.common.result.R;
import com.livestream.video.entity.ShortVideo;
import com.livestream.video.service.VideoService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 视频控制器
 */
@Slf4j
@RestController
@RequestMapping("/video")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    /**
     * 上传视频
     */
    @PostMapping("/upload")
    public R<ShortVideo> uploadVideo(@RequestBody ShortVideo video, HttpServletRequest request) {
        Long userId = Long.valueOf(request.getHeader("X-User-Id"));
        video.setUserId(userId);
        return R.success("上传成功", videoService.uploadVideo(video));
    }

    /**
     * 更新视频信息
     */
    @PutMapping("/update")
    public R<Void> updateVideo(@RequestBody ShortVideo video) {
        videoService.updateVideo(video);
        return R.success();
    }

    /**
     * 删除视频
     */
    @DeleteMapping("/{videoId}")
    public R<Void> deleteVideo(@PathVariable String videoId, HttpServletRequest request) {
        Long userId = Long.valueOf(request.getHeader("X-User-Id"));
        videoService.deleteVideo(videoId, userId);
        return R.success();
    }

    /**
     * 获取视频详情
     */
    @GetMapping("/{videoId}")
    public R<ShortVideo> getVideoDetail(@PathVariable String videoId) {
        ShortVideo video = videoService.getVideoDetail(videoId);
        if (video != null) {
            videoService.incrementPlayCount(videoId);
        }
        return R.success(video);
    }

    /**
     * 分页查询用户视频
     */
    @GetMapping("/user/{userId}")
    public R<?> getUserVideos(@PathVariable Long userId, PageRequest request) {
        return R.success(videoService.getUserVideos(userId, request));
    }

    /**
     * 获取我的视频列表
     */
    @GetMapping("/my")
    public R<?> getMyVideos(PageRequest request, HttpServletRequest httpRequest) {
        Long userId = Long.valueOf(httpRequest.getHeader("X-User-Id"));
        return R.success(videoService.getUserVideos(userId, request));
    }

    /**
     * 发布视频
     */
    @PostMapping("/publish/{videoId}")
    public R<Void> publishVideo(@PathVariable String videoId, HttpServletRequest request) {
        Long userId = Long.valueOf(request.getHeader("X-User-Id"));
        videoService.publishVideo(videoId, userId);
        return R.success();
    }

    /**
     * 下架视频
     */
    @PostMapping("/unpublish/{videoId}")
    public R<Void> unpublishVideo(@PathVariable String videoId, HttpServletRequest request) {
        Long userId = Long.valueOf(request.getHeader("X-User-Id"));
        videoService.unpublishVideo(videoId, userId);
        return R.success();
    }

    /**
     * 点赞视频
     */
    @PostMapping("/like/{videoId}")
    public R<Void> likeVideo(@PathVariable String videoId, HttpServletRequest request) {
        Long userId = Long.valueOf(request.getHeader("X-User-Id"));
        videoService.likeVideo(videoId, userId);
        return R.success();
    }

    /**
     * 取消点赞
     */
    @PostMapping("/unlike/{videoId}")
    public R<Void> unlikeVideo(@PathVariable String videoId, HttpServletRequest request) {
        Long userId = Long.valueOf(request.getHeader("X-User-Id"));
        videoService.unlikeVideo(videoId, userId);
        return R.success();
    }
}
