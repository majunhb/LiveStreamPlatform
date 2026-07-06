package com.livestream.gift.controller;

import com.livestream.common.result.R;
import com.livestream.gift.entity.Wallet;
import com.livestream.gift.service.WalletService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 钱包控制器
 */
@Slf4j
@RestController
@RequestMapping("/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    /**
     * 获取我的钱包
     */
    @GetMapping("/my")
    public R<Wallet> getMyWallet(HttpServletRequest request) {
        Long userId = Long.valueOf(request.getHeader("X-User-Id"));
        return R.success(walletService.getUserWallet(userId));
    }

    /**
     * 获取用户钱包
     */
    @GetMapping("/{userId}")
    public R<Wallet> getWallet(@PathVariable Long userId) {
        return R.success(walletService.getUserWallet(userId));
    }

    /**
     * 充值金币（实际需要接入支付系统）
     */
    @PostMapping("/recharge")
    public R<Void> recharge(@RequestParam Long coin, HttpServletRequest request) {
        Long userId = Long.valueOf(request.getHeader("X-User-Id"));
        walletService.recharge(userId, coin);
        return R.success();
    }
}
