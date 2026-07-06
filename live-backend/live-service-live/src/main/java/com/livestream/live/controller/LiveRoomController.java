package com.livestream.live.controller;

import com.livestream.common.dto.PageRequest;
import com.livestream.common.dto.PageResult;
import com.livestream.common.result.R;
import com.livestream.live.entity.LiveRoom;
import com.livestream.live.service.LiveRoomService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 直播间控制器
 */
@Slf4j
@RestController
@RequestMapping("/live")
@RequiredArgsConstructor
public class LiveRoomController {

    private final LiveRoomService liveRoomService;

    /**
     * 创建直播间
     */
    @PostMapping("/room/create")
    public R<LiveRoom> createRoom(@RequestParam String title,
                                   @RequestParam(required = false) String coverUrl,
                                   @RequestParam(required = false) String description,
                                   HttpServletRequest request) {
        Long userId = Long.valueOf(request.getHeader("X-User-Id"));
        LiveRoom room = liveRoomService.createRoom(userId, title, coverUrl, description);
        return R.success("直播间创建成功", room);
    }

    /**
     * 更新直播间信息
     */
    @PutMapping("/room/update")
    public R<Void> updateRoom(@RequestBody LiveRoom room) {
        liveRoomService.updateRoom(room);
        return R.success();
    }

    /**
     * 开始直播
     */
    @PostMapping("/room/start")
    public R<LiveRoom> startLive(@RequestParam Long roomId, HttpServletRequest request) {
        Long userId = Long.valueOf(request.getHeader("X-User-Id"));
        LiveRoom room = liveRoomService.startLive(roomId, userId);
        return R.success("开始直播", room);
    }

    /**
     * 结束直播
     */
    @PostMapping("/room/end")
    public R<Void> endLive(@RequestParam Long roomId, HttpServletRequest request) {
        Long userId = Long.valueOf(request.getHeader("X-User-Id"));
        liveRoomService.endLive(roomId, userId);
        return R.success();
    }

    /**
     * 获取直播间详情
     */
    @GetMapping("/room/{roomId}")
    public R<LiveRoom> getRoomDetail(@PathVariable Long roomId) {
        return R.success(liveRoomService.getRoomDetail(roomId));
    }

    /**
     * 获取我的直播间
     */
    @GetMapping("/room/my")
    public R<LiveRoom> getMyRoom(HttpServletRequest request) {
        Long userId = Long.valueOf(request.getHeader("X-User-Id"));
        return R.success(liveRoomService.getUserRoom(userId));
    }

    /**
     * 获取正在直播的列表
     */
    @GetMapping("/rooms/living")
    public R<PageResult<LiveRoom>> getLivingRooms(PageRequest request) {
        return R.success(liveRoomService.getLivingRooms(request));
    }

    /**
     * 分页查询直播间列表
     */
    @GetMapping("/rooms")
    public R<PageResult<LiveRoom>> getRooms(PageRequest request) {
        return R.success(liveRoomService.pageQuery(request));
    }
}
