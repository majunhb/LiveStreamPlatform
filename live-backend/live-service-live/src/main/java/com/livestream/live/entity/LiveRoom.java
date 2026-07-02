package com.livestream.live.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 直播间实体
 */
@Data
@TableName("t_live_room")
public class LiveRoom implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 直播间ID */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    /** 主播用户ID */
    private Long userId;
    
    /** 直播间标题 */
    private String title;
    
    /** 直播间封面图 */
    private String coverUrl;
    
    /** 直播间描述 */
    private String description;
    
    /** 流地址 */
    private String streamUrl;
    
    /** 推流密钥 */
    private String streamKey;
    
    /** 播放地址 */
    private String playUrl;
    
    /** 直播间状态：0-未开播，1-直播中，2-已下播 */
    private Integer status;
    
    /** 当前观看人数 */
    private Integer viewerCount;
    
    /** 累计观看人数 */
    private Long totalViewerCount;
    
    /** 点赞数 */
    private Long likeCount;
    
    /** 分类ID */
    private Long categoryId;
    
    /** 是否置顶：0-否，1-是 */
    private Integer isTop;
    
    /** 是否推荐：0-否，1-是 */
    private Integer isRecommend;
    
    /** 封禁原因 */
    private String banReason;
    
    /** 封禁截止时间 */
    private LocalDateTime banEndTime;
    
    /** 开始时间 */
    private LocalDateTime startTime;
    
    /** 结束时间 */
    private LocalDateTime endTime;
    
    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
