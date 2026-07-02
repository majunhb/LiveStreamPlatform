package com.livestream.video.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 短视频实体（存储在MongoDB）
 */
@Data
@Document(collection = "t_short_video")
public class ShortVideo implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 视频ID */
    @Id
    private String id;
    
    /** 用户ID */
    @Field("user_id")
    private Long userId;
    
    /** 视频标题 */
    private String title;
    
    /** 视频描述 */
    private String description;
    
    /** 视频封面URL */
    @Field("cover_url")
    private String coverUrl;
    
    /** 视频存储URL */
    @Field("video_url")
    private String videoUrl;
    
    /** 视频时长（秒） */
    private Integer duration;
    
    /** 视频宽度 */
    private Integer width;
    
    /** 视频高度 */
    private Integer height;
    
    /** 文件大小（字节） */
    @Field("file_size")
    private Long fileSize;
    
    /** 分类ID */
    @Field("category_id")
    private Long categoryId;
    
    /** 标签（逗号分隔） */
    private String tags;
    
    /** 点赞数 */
    @Field("like_count")
    private Long likeCount;
    
    /** 评论数 */
    @Field("comment_count")
    private Long commentCount;
    
    /** 分享数 */
    @Field("share_count")
    private Long shareCount;
    
    /** 播放数 */
    @Field("play_count")
    private Long playCount;
    
    /** 审核状态：0-待审核，1-通过，2-拒绝 */
    @Field("audit_status")
    private Integer auditStatus;
    
    /** 审核原因 */
    @Field("audit_reason")
    private String auditReason;
    
    /** 视频状态：0-草稿，1-已发布，2-已下架 */
    private Integer status;
    
    /** 是否置顶：0-否，1-是 */
    @Field("is_top")
    private Integer isTop;
    
    /** 创建时间 */
    @Field("create_time")
    private LocalDateTime createTime;
    
    /** 更新时间 */
    @Field("update_time")
    private LocalDateTime updateTime;
}
