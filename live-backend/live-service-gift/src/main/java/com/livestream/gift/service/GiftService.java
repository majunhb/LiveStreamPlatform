package com.livestream.gift.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.livestream.common.dto.PageRequest;
import com.livestream.common.dto.PageResult;
import com.livestream.gift.entity.Gift;
import com.livestream.gift.entity.GiftRecord;

import java.util.List;

/**
 * 礼物服务接口
 */
public interface GiftService extends IService<Gift> {

    /**
     * 获取上架礼物列表
     */
    List<Gift> getActiveGifts();

    /**
     * 发送礼物
     */
    GiftRecord sendGift(Long roomId, Long senderId, Long receiverId, Long giftId, Integer quantity);

    /**
     * 获取用户礼物记录
     */
    PageResult<GiftRecord> getGiftRecords(Long userId, PageRequest request);

    /**
     * 获取直播间礼物记录
     */
    List<GiftRecord> getRoomGiftRecords(Long roomId);

    /**
     * 获取用户收到的礼物总数（金币）
     */
    Long getUserTotalGifts(Long userId);
}
