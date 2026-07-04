package com.livestream.gift.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.livestream.gift.entity.Wallet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 钱包Mapper接口
 */
@Mapper
public interface WalletMapper extends BaseMapper<Wallet> {

    /**
     * 原子扣款：余额 = 余额 - amount，仅当余额充足时生效
     * @return 受影响行数（0表示余额不足）
     */
    @Update("UPDATE t_wallet SET coin_balance = coin_balance - #{amount}, " +
            "total_consume_coin = total_consume_coin + #{amount}, " +
            "update_time = NOW() " +
            "WHERE user_id = #{userId} AND coin_balance >= #{amount} AND status = 0")
    int deductBalance(@Param("userId") Long userId, @Param("amount") Long amount);

    /**
     * 原子充值/增加余额
     * @return 受影响行数
     */
    @Update("UPDATE t_wallet SET coin_balance = coin_balance + #{amount}, " +
            "total_recharge_coin = total_recharge_coin + #{amount}, " +
            "update_time = NOW() " +
            "WHERE user_id = #{userId} AND status = 0")
    int rechargeBalance(@Param("userId") Long userId, @Param("amount") Long amount);

    /**
     * 原子增加收入
     * @return 受影响行数
     */
    @Update("UPDATE t_wallet SET coin_balance = coin_balance + #{amount}, " +
            "total_income_coin = total_income_coin + #{amount}, " +
            "update_time = NOW() " +
            "WHERE user_id = #{userId} AND status = 0")
    int addIncomeBalance(@Param("userId") Long userId, @Param("amount") Long amount);
}
