package com.livestream.gift.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 礼物记录实体
 */
@Data
@TableName("t_gift_record")
public class GiftRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 记录ID */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 直播间ID */
    private Long roomId;

    /** 发送者ID */
    private Long senderId;

    /** 接收者ID */
    private Long receiverId;

    /** 礼物ID */
    private Long giftId;

    /** 礼物名称 */
    private String giftName;

    /** 数量 */
    private Integer quantity;

    /** 总金币 */
    private Long totalCoin;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
