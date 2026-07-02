package com.livestream.gift.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.livestream.gift.entity.GiftRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 礼物记录Mapper接口
 */
@Mapper
public interface GiftRecordMapper extends BaseMapper<GiftRecord> {
    
}
