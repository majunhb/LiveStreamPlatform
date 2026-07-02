package com.livestream.message.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 弹幕实体（MongoDB存储）
 */
@Data
@Document(collection = "t_danmaku")
public class Danmaku implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 弹幕ID */
    @Id
    private String id;
    
    /** 直播间ID */
    @Field("room_id")
    private Long roomId;
    
    /** 用户ID */
    @Field("user_id")
    private Long userId;
    
    /** 用户名 */
    private String username;
    
    /** 用户头像 */
    private String avatar;
    
    /** 弹幕内容 */
    private String content;
    
    /** 弹幕颜色 */
    private String color;
    
    /** 弹幕大小：0-小，1-中，2-大 */
    private Integer size;
    
    /** 弹幕位置：0-滚动，1-顶部，2-底部 */
    private Integer position;
    
    /** 弹幕类型：0-普通，1-礼物，2-系统 */
    private Integer type;
    
    /** 发送时间 */
    @Field("send_time")
    private LocalDateTime sendTime;
    
    /** 是否显示：0-隐藏，1-显示 */
    private Integer visible;
}
