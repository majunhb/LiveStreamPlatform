package com.livestream.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.livestream.video.entity.Video;
import org.apache.ibatis.annotations.Mapper;

/**
 * 短视频Mapper接口
 */
@Mapper
public interface VideoMapper extends BaseMapper<Video> {
    
}
