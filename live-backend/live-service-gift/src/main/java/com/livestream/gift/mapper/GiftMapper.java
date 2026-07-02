package com.livestream.gift.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.livestream.gift.entity.Gift;
import org.apache.ibatis.annotations.Mapper;

/**
 * 礼物Mapper接口
 */
@Mapper
public interface GiftMapper extends BaseMapper<Gift> {
    
}
