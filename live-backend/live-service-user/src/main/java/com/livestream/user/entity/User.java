package com.livestream.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
@TableName("t_user")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 用户ID */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    /** 用户名 */
    private String username;
    
    /** 密码（加密存储） */
    @JsonIgnore
    @TableField(select = false)
    private String password;
    
    /** 昵称 */
    private String nickname;
    
    /** 手机号 */
    @JsonIgnore
    private String phone;
    
    /** 邮箱 */
    @JsonIgnore
    private String email;
    
    /** 头像URL */
    private String avatar;
    
    /** 性别：0-未知，1-男，2-女 */
    private Integer gender;
    
    /** 个人简介 */
    private String bio;
    
    /** 用户状态：0-正常，1-禁用 */
    private Integer status;
    
    /** 用户类型：1-普通用户，2-主播，3-管理员 */
    private Integer userType;
    
    /** 用户角色：USER-普通用户，ADMIN-管理员 */
    private String role;
    
    /** 金币余额 */
    private Long coinBalance;
    
    /** 最后登录时间 */
    private LocalDateTime lastLoginTime;
    
    /** 最后登录IP */
    @JsonIgnore
    private String lastLoginIp;
    
    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    /** 是否删除：0-未删除，1-已删除 */
    @TableLogic
    private Integer deleted;
}
