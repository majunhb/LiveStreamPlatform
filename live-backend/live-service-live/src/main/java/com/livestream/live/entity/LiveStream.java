package com.livestream.live.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 直播记录实体
 */
@Data
@TableName("t_live_stream")
public class LiveStream implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 记录ID */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    /** 直播间ID */
    private Long roomId;
    
    /** 开始时间 */
    private LocalDateTime startTime;
    
    /** 结束时间 */
    private LocalDateTime endTime;
    
    /** 直播时长（秒） */
    private Long duration;
    
    /** 峰值观众数 */
    private Integer peakViewerCount;
    
    /** 平均观众数 */
    private Integer avgViewerCount;
    
    /** 弹幕数 */
    private Long danmakuCount;
    
    /** 礼物数 */
    private Long giftCount;
    
    /** 礼物收入 */
    private Long giftIncome;
    
    /** 新增粉丝数 */
    private Integer newFansCount;
    
    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
