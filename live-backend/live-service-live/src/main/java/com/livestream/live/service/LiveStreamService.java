package com.livestream.live.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.livestream.live.entity.LiveStream;

/**
 * 直播流服务接口
 */
public interface LiveStreamService extends IService<LiveStream> {
    
    /**
     * 开始录制直播流
     */
    LiveStream startRecording(Long roomId);
    
    /**
     * 结束录制直播流
     */
    LiveStream endRecording(Long recordId);
    
    /**
     * 获取直播统计
     */
    LiveStream getLiveStats(Long roomId);
}
