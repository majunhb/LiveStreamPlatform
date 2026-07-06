package com.livestream.user.controller;

import com.livestream.common.dto.PageRequest;
import com.livestream.common.dto.PageResult;
import com.livestream.common.result.R;
import com.livestream.common.util.JwtUtil;
import com.livestream.user.entity.User;
import com.livestream.user.service.UserService;
import com.livestream.user.vo.UserVO;
import org.springframework.beans.BeanUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public R<String> register(@RequestParam String username,
                              @RequestParam String password,
                              @RequestParam(required = false) String phone) {
        String token = userService.register(username, password, phone);
        return R.success("注册成功", JwtUtil.getTokenPrefix() + token);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public R<String> login(@RequestParam String username,
                            @RequestParam String password) {
        String token = userService.login(username, password);
        return R.success("登录成功", JwtUtil.getTokenPrefix() + token);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public R<UserVO> getCurrentUserInfo(HttpServletRequest request) {
        Long userId = Long.valueOf(request.getHeader("X-User-Id"));
        User user = userService.getById(userId);
        return R.success(convertToVO(user));
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/update")
    public R<Void> updateUserInfo(@RequestBody User user, HttpServletRequest request) {
        Long userId = Long.valueOf(request.getHeader("X-User-Id"));
        user.setId(userId);
        userService.updateUserInfo(user);
        return R.success();
    }

    /**
     * 根据ID获取用户信息
     */
    @GetMapping("/{id}")
    public R<UserVO> getUserById(@PathVariable Long id) {
        User user = userService.getById(id);
        return R.success(convertToVO(user));
    }

    /**
     * 分页查询用户列表
     */
    @GetMapping("/page")
    public R<PageResult<UserVO>> pageQuery(PageRequest request) {
        PageResult<User> pageResult = userService.pageQuery(request);
        return R.success(PageResult.of(
                pageResult.getRecords().stream().map(this::convertToVO).toList(),
                pageResult.getTotal(),
                pageResult.getPageNum(),
                pageResult.getPageSize()
        ));
    }

    /**
     * User实体转UserVO（S-10安全修复：脱敏敏感字段）
     */
    private UserVO convertToVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }

    /**
     * 检查用户名是否可用
     */
    @GetMapping("/check-username")
    public R<Boolean> checkUsername(@RequestParam String username) {
        return R.success(userService.checkUsername(username));
    }

    /**
     * 检查手机号是否可用
     */
    @GetMapping("/check-phone")
    public R<Boolean> checkPhone(@RequestParam String phone) {
        return R.success(userService.checkPhone(phone));
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public R<Void> logout(HttpServletRequest request) {
        // 可以在这里处理退出登录逻辑，如将Token加入黑名单
        return R.success();
    }
}
