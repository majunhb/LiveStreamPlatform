package com.livestream.gift.controller;

import com.livestream.common.dto.PageRequest;
import com.livestream.common.dto.PageResult;
import com.livestream.common.result.R;
import com.livestream.gift.entity.Gift;
import com.livestream.gift.entity.GiftRecord;
import com.livestream.gift.service.GiftService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 礼物控制器
 */
@Slf4j
@RestController
@RequestMapping("/gift")
@RequiredArgsConstructor
public class GiftController {

    private final GiftService giftService;

    /**
     * 获取礼物列表
     */
    @GetMapping("/list")
    public R<List<Gift>> getGiftList() {
        return R.success(giftService.getActiveGifts());
    }

    /**
     * 发送礼物
     */
    @PostMapping("/send")
    public R<GiftRecord> sendGift(@RequestParam Long roomId,
                                  @RequestParam Long receiverId,
                                  @RequestParam Long giftId,
                                  @RequestParam(defaultValue = "1") Integer quantity,
                                  HttpServletRequest request) {
        Long senderId = Long.valueOf(request.getHeader("X-User-Id"));
        GiftRecord record = giftService.sendGift(roomId, senderId, receiverId, giftId, quantity);
        return R.success("礼物发送成功", record);
    }

    /**
     * 获取我的礼物记录
     */
    @GetMapping("/records/my")
    public R<PageResult<GiftRecord>> getMyGiftRecords(PageRequest request, HttpServletRequest httpRequest) {
        Long userId = Long.valueOf(httpRequest.getHeader("X-User-Id"));
        return R.success(giftService.getGiftRecords(userId, request));
    }

    /**
     * 获取直播间的礼物记录
     */
    @GetMapping("/records/room/{roomId}")
    public R<List<GiftRecord>> getRoomGiftRecords(@PathVariable Long roomId) {
        return R.success(giftService.getRoomGiftRecords(roomId));
    }

    /**
     * 获取用户收到的礼物总数
     */
    @GetMapping("/total/{userId}")
    public R<Long> getUserTotalGifts(@PathVariable Long userId) {
        return R.success(giftService.getUserTotalGifts(userId));
    }
}
