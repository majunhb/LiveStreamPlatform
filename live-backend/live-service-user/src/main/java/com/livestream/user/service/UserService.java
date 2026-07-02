package com.livestream.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.livestream.common.dto.PageRequest;
import com.livestream.common.dto.PageResult;
import com.livestream.user.entity.User;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {
    
    /**
     * 用户注册
     */
    String register(String username, String password, String phone);
    
    /**
     * 用户登录
     */
    String login(String username, String password);
    
    /**
     * 根据用户名查询用户
     */
    User getByUsername(String username);
    
    /**
     * 根据手机号查询用户
     */
    User getByPhone(String phone);
    
    /**
     * 更新用户信息
     */
    boolean updateUserInfo(User user);
    
    /**
     * 分页查询用户列表
     */
    PageResult<User> pageQuery(PageRequest request);
    
    /**
     * 检查用户名是否可用
     */
    boolean checkUsername(String username);
    
    /**
     * 检查手机号是否可用
     */
    boolean checkPhone(String phone);
}
