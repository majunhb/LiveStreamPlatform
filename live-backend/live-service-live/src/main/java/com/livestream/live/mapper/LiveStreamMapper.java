package com.livestream.live.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.livestream.live.entity.LiveStream;
import org.apache.ibatis.annotations.Mapper;

/**
 * 直播记录Mapper接口
 */
@Mapper
public interface LiveStreamMapper extends BaseMapper<LiveStream> {
    
}
