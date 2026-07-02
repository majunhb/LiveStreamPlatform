package com.livestream.admin.controller;

import com.livestream.common.result.R;
import com.livestream.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 管理后台-用户管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/admin/user")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminService adminService;

    /**
     * 获取用户统计
     */
    @GetMapping("/statistics")
    public R<Object> getUserStatistics() {
        return R.success(adminService.getUserStatistics());
    }

    /**
     * 禁用用户
     */
    @PostMapping("/disable/{userId}")
    public R<Void> disableUser(@PathVariable Long userId) {
        adminService.disableUser(userId);
        return R.success();
    }

    /**
     * 启用用户
     */
    @PostMapping("/enable/{userId}")
    public R<Void> enableUser(@PathVariable Long userId) {
        adminService.enableUser(userId);
        return R.success();
    }
}
