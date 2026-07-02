package com.livestream.gift.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 礼物实体
 */
@Data
@TableName("t_gift")
public class Gift implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 礼物ID */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    /** 礼物名称 */
    private String name;
    
    /** 礼物图标URL */
    private String iconUrl;
    
    /** 礼物动画URL */
    private String animationUrl;
    
    /** 价格（金币） */
    private Long price;
    
    /** 价格（人民币，单位：分） */
    private BigDecimal rmbPrice;
    
    /** 礼物类型：1-普通礼物，2-特效礼物，3-守护礼物 */
    private Integer type;
    
    /** 持续时间（秒） */
    private Integer duration;
    
    /** 排序 */
    private Integer sort;
    
    /** 状态：0-下架，1-上架 */
    private Integer status;
    
    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
