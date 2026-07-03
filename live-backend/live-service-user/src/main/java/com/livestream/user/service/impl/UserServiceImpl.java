package com.livestream.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.livestream.common.dto.PageRequest;
import com.livestream.common.dto.PageResult;
import com.livestream.common.exception.BusinessException;
import com.livestream.common.result.ResultCode;
import com.livestream.common.util.JwtUtil;
import com.livestream.user.entity.User;
import com.livestream.user.mapper.UserMapper;
import com.livestream.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final RedisTemplate<String, Object> redisTemplate;

    /** BCrypt密码编码器 */
    private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    @Override
    public String register(String username, String password, String phone) {
        // 检查用户名是否已存在
        if (getByUsername(username) != null) {
            throw new BusinessException(ResultCode.USER_ALREADY_EXISTS);
        }
        
        // 创建用户
        User user = new User();
        user.setUsername(username);
        // 使用BCrypt加密存储密码
        user.setPassword(PASSWORD_ENCODER.encode(password));
        user.setPhone(phone);
        user.setNickname(username);
        user.setStatus(0);
        user.setUserType(1);
        // 默认角色为普通用户
        user.setRole("USER");
        user.setCoinBalance(0L);
        
        boolean saved = save(user);
        if (!saved) {
            throw new BusinessException("注册失败");
        }
        
        log.info("用户注册成功: username={}", username);
        return JwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
    }

    @Override
    public String login(String username, String password) {
        User user = getByUsername(username);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        
        if (user.getStatus() == 1) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }
        
        // 使用BCrypt验证密码
        if (!PASSWORD_ENCODER.matches(password, user.getPassword())) {
            throw new BusinessException(ResultCode.USERNAME_OR_PASSWORD_ERROR);
        }
        
        // 更新登录信息
        user.setLastLoginTime(LocalDateTime.now());
        updateById(user);
        
        log.info("用户登录成功: username={}", username);
        return JwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
    }

    @Override
    public User getByUsername(String username) {
        return getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
    }

    @Override
    public User getByPhone(String phone) {
        return getOne(new LambdaQueryWrapper<User>().eq(User::getPhone, phone));
    }

    @Override
    public boolean updateUserInfo(User user) {
        // 防止修改关键字段
        user.setUsername(null);
        user.setPassword(null);
        user.setUserType(null);
        user.setCoinBalance(null);
        return updateById(user);
    }

    @Override
    public PageResult<User> pageQuery(PageRequest request) {
        Page<User> page = new Page<>(request.getPageNum(), request.getPageSize());
        
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(User::getCreateTime);
        
        Page<User> result = page(page, queryWrapper);
        
        return PageResult.of(
                result.getRecords(),
                result.getTotal(),
                (int) result.getCurrent(),
                (int) result.getSize()
        );
    }

    @Override
    public boolean checkUsername(String username) {
        return getByUsername(username) == null;
    }

    @Override
    public boolean checkPhone(String phone) {
        return getByPhone(phone) == null;
    }
}
