package com.livestream.gift.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.livestream.gift.entity.Wallet;
import org.apache.ibatis.annotations.Mapper;

/**
 * 钱包Mapper接口
 */
@Mapper
public interface WalletMapper extends BaseMapper<Wallet> {
    
}
