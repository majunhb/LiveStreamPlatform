package com.livestream.live.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.livestream.live.entity.LiveRoom;
import org.apache.ibatis.annotations.Mapper;

/**
 * 直播间Mapper接口
 */
@Mapper
public interface LiveRoomMapper extends BaseMapper<LiveRoom> {
    
}
