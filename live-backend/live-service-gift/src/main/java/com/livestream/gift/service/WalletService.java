package com.livestream.gift.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.livestream.gift.entity.Wallet;

/**
 * 钱包服务接口
 */
public interface WalletService extends IService<Wallet> {
    
    /**
     * 获取用户钱包
     */
    Wallet getUserWallet(Long userId);
    
    /**
     * 创建钱包
     */
    Wallet createWallet(Long userId);
    
    /**
     * 充值金币
     */
    boolean recharge(Long userId, Long coin);
    
    /**
     * 扣除金币
     */
    boolean deduct(Long userId, Long coin);
    
    /**
     * 增加金币收入
     */
    boolean addIncome(Long userId, Long coin);
    
    /**
     * 检查金币是否足够
     */
    boolean checkBalance(Long userId, Long coin);
}
