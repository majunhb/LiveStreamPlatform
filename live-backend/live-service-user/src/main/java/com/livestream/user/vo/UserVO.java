package com.livestream.user.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户视图对象（S-10安全修复）
 * 用于API响应，排除password、phone、email、lastLoginIp等敏感字段
 */
@Data
public class UserVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String nickname;
    private String avatar;
    private Integer gender;
    private String bio;
    private Integer status;
    private Integer userType;
    private String role;
    private LocalDateTime createTime;
}
