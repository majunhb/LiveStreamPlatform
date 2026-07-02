package com.livestream.admin.controller;

import com.livestream.common.result.R;
import com.livestream.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 管理后台-直播管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/admin/live")
@RequiredArgsConstructor
public class AdminLiveController {

    private final AdminService adminService;

    /**
     * 获取直播统计
     */
    @GetMapping("/statistics")
    public R<Object> getLiveStatistics() {
        return R.success(adminService.getLiveStatistics());
    }

    /**
     * 封禁直播间
     */
    @PostMapping("/ban/{roomId}")
    public R<Void> banLiveRoom(@PathVariable Long roomId, @RequestParam String reason) {
        adminService.banLiveRoom(roomId, reason);
        return R.success();
    }

    /**
     * 解封直播间
     */
    @PostMapping("/unban/{roomId}")
    public R<Void> unbanLiveRoom(@PathVariable Long roomId) {
        adminService.unbanLiveRoom(roomId);
        return R.success();
    }
}
