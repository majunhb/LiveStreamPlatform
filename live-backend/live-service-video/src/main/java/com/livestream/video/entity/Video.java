package com.livestream.video.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 短视频关联实体（MySQL）
 */
@Data
@TableName("t_video")
public class Video implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 视频ID */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    /** 用户ID */
    private Long userId;
    
    /** 视频标题 */
    private String title;
    
    /** 视频描述 */
    private String description;
    
    /** 视频封面URL */
    private String coverUrl;
    
    /** 视频存储URL */
    private String videoUrl;
    
    /** 视频时长（秒） */
    private Integer duration;
    
    /** 审核状态：0-待审核，1-通过，2-拒绝 */
    private Integer auditStatus;
    
    /** 视频状态：0-草稿，1-已发布，2-已下架 */
    private Integer status;
    
    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
