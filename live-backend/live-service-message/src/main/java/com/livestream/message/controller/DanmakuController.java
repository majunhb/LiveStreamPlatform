package com.livestream.message.controller;

import com.livestream.common.result.R;
import com.livestream.message.entity.Danmaku;
import com.livestream.message.service.DanmakuService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 弹幕控制器
 */
@Slf4j
@RestController
@RequestMapping("/danmaku")
@RequiredArgsConstructor
public class DanmakuController {

    private final DanmakuService danmakuService;

    /**
     * 发送弹幕
     */
    @PostMapping("/send")
    public R<Danmaku> sendDanmaku(@RequestBody Danmaku danmaku, HttpServletRequest request) {
        Long userId = Long.valueOf(request.getHeader("X-User-Id"));
        String username = request.getHeader("X-Username");
        danmaku.setUserId(userId);
        danmaku.setUsername(username);
        return R.success(danmakuService.sendDanmaku(danmaku));
    }

    /**
     * 获取直播间弹幕历史
     */
    @GetMapping("/room/{roomId}")
    public R<List<Danmaku>> getRoomDanmakus(@PathVariable Long roomId,
                                             @RequestParam(defaultValue = "100") Integer limit) {
        return R.success(danmakuService.getRoomDanmakus(roomId, limit));
    }

    /**
     * 禁言用户
     */
    @PostMapping("/mute")
    public R<Void> muteUser(@RequestParam Long roomId,
                            @RequestParam Long userId,
                            @RequestParam(defaultValue = "300") Long duration) {
        danmakuService.muteUser(roomId, userId, duration);
        return R.success();
    }

    /**
     * 取消禁言
     */
    @PostMapping("/unmute")
    public R<Void> unmuteUser(@RequestParam Long roomId, @RequestParam Long userId) {
        danmakuService.unmuteUser(roomId, userId);
        return R.success();
    }

    /**
     * 删除弹幕
     */
    @DeleteMapping("/{danmakuId}")
    public R<Void> deleteDanmaku(@PathVariable String danmakuId) {
        danmakuService.deleteDanmaku(danmakuId);
        return R.success();
    }
}
