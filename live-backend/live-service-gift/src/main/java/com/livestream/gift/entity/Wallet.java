package com.livestream.gift.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 钱包实体
 */
@Data
@TableName("t_wallet")
public class Wallet implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 钱包ID */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    /** 用户ID */
    private Long userId;
    
    /** 金币余额 */
    private Long coinBalance;
    
    /** 累计充值金币 */
    private Long totalRechargeCoin;
    
    /** 累计消费金币 */
    private Long totalConsumeCoin;
    
    /** 累计收入金币 */
    private Long totalIncomeCoin;
    
    /** 账户状态：0-正常，1-冻结 */
    private Integer status;
    
    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
