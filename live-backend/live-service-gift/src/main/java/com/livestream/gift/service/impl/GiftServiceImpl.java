package com.livestream.gift.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.livestream.common.dto.PageRequest;
import com.livestream.common.dto.PageResult;
import com.livestream.common.exception.BusinessException;
import com.livestream.common.result.ResultCode;
import com.livestream.gift.entity.Gift;
import com.livestream.gift.entity.GiftRecord;
import com.livestream.gift.mapper.GiftMapper;
import com.livestream.gift.mapper.GiftRecordMapper;
import com.livestream.gift.service.GiftService;
import com.livestream.gift.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 礼物服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GiftServiceImpl extends ServiceImpl<GiftMapper, Gift> implements GiftService {

    private final GiftRecordMapper giftRecordMapper;
    private final WalletService walletService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public List<Gift> getActiveGifts() {
        LambdaQueryWrapper<Gift> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Gift::getStatus, 1).orderByAsc(Gift::getSort);
        return list(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GiftRecord sendGift(Long roomId, Long senderId, Long receiverId, Long giftId, Integer quantity) {
        quantity = quantity == null || quantity < 1 ? 1 : quantity;
        
        // 获取礼物信息
        Gift gift = getById(giftId);
        if (gift == null || gift.getStatus() != 1) {
            throw new BusinessException(ResultCode.GIFT_NOT_FOUND);
        }
        
        // 计算总价
        long totalCoin = gift.getPrice() * quantity;
        
        // 检查发送者余额
        if (!walletService.checkBalance(senderId, totalCoin)) {
            throw new BusinessException(ResultCode.COIN_INSUFFICIENT);
        }
        
        // 扣除发送者金币
        walletService.deduct(senderId, totalCoin);
        
        // 增加接收者（主播）金币收入
        walletService.addIncome(receiverId, totalCoin);
        
        // 创建礼物记录
        GiftRecord record = new GiftRecord();
        record.setRoomId(roomId);
        record.setSenderId(senderId);
        record.setReceiverId(receiverId);
        record.setGiftId(giftId);
        record.setGiftName(gift.getName());
        record.setQuantity(quantity);
        record.setTotalCoin(totalCoin);
        giftRecordMapper.insert(record);
        
        // 发送Kafka消息通知直播间
        Map<String, Object> message = new HashMap<>();
        message.put("type", "gift");
        message.put("roomId", roomId);
        message.put("senderId", senderId);
        message.put("receiverId", receiverId);
        message.put("gift", gift);
        message.put("quantity", quantity);
        kafkaTemplate.send("live-gift-topic", String.valueOf(roomId), message);
        
        log.info("发送礼物成功: roomId={}, senderId={}, giftId={}, quantity={}, totalCoin={}",
                roomId, senderId, giftId, quantity, totalCoin);
        
        return record;
    }

    @Override
    public PageResult<GiftRecord> getGiftRecords(Long userId, PageRequest request) {
        // 查询用户发送或接收的礼物记录
        LambdaQueryWrapper<GiftRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.and(w -> w.eq(GiftRecord::getSenderId, userId).or().eq(GiftRecord::getReceiverId, userId));
        queryWrapper.orderByDesc(GiftRecord::getCreateTime);
        
        Page<GiftRecord> page = new Page<>(request.getPageNum(), request.getPageSize());
        Page<GiftRecord> result = giftRecordMapper.selectPage(page, queryWrapper);
        
        return PageResult.of(result.getRecords(), result.getTotal(),
                (int) result.getCurrent(), (int) result.getSize());
    }

    @Override
    public List<GiftRecord> getRoomGiftRecords(Long roomId) {
        LambdaQueryWrapper<GiftRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GiftRecord::getRoomId, roomId)
                .orderByDesc(GiftRecord::getCreateTime)
                .last("LIMIT 50");
        return giftRecordMapper.selectList(queryWrapper);
    }

    @Override
    public Long getUserTotalGifts(Long userId) {
        LambdaQueryWrapper<GiftRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GiftRecord::getReceiverId, userId);
        List<GiftRecord> records = giftRecordMapper.selectList(queryWrapper);
        return records.stream().mapToLong(GiftRecord::getTotalCoin).sum();
    }
}
