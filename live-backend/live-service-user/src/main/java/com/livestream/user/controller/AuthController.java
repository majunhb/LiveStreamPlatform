package com.livestream.user.controller;

import com.livestream.common.result.R;
import com.livestream.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public R<String> login(@RequestParam String username,
                           @RequestParam String password) {
        String token = userService.login(username, password);
        return R.success("登录成功", token);
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public R<String> register(@RequestParam String username,
                               @RequestParam String password,
                               @RequestParam(required = false) String phone,
                               @RequestParam(required = false) String email) {
        String token = userService.register(username, password, phone);
        return R.success("注册成功", token);
    }
}
