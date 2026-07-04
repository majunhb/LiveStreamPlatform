package com.livestream.gift.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.livestream.gift.entity.Wallet;
import com.livestream.gift.mapper.WalletMapper;
import com.livestream.gift.service.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 钱包服务实现类
 * 使用数据库原子操作替代 Redis 分布式锁，避免并发扣款导致的余额不一致问题
 */
@Slf4j
@Service
public class WalletServiceImpl extends ServiceImpl<WalletMapper, Wallet> implements WalletService {

    @Override
    public Wallet getUserWallet(Long userId) {
        LambdaQueryWrapper<Wallet> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Wallet::getUserId, userId);
        Wallet wallet = getOne(queryWrapper);
        
        if (wallet == null) {
            wallet = createWallet(userId);
        }
        return wallet;
    }

    @Override
    public Wallet createWallet(Long userId) {
        Wallet wallet = new Wallet();
        wallet.setUserId(userId);
        wallet.setCoinBalance(0L);
        wallet.setTotalRechargeCoin(0L);
        wallet.setTotalConsumeCoin(0L);
        wallet.setTotalIncomeCoin(0L);
        wallet.setStatus(0);
        save(wallet);
        
        log.info("创建钱包: userId={}", userId);
        return wallet;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean recharge(Long userId, Long coin) {
        // 确保钱包存在（首次充值时自动创建）
        getUserWallet(userId);
        int rows = baseMapper.rechargeBalance(userId, coin);
        if (rows > 0) {
            log.info("充值成功: userId={}, coin={}", userId, coin);
        }
        return rows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deduct(Long userId, Long coin) {
        // 确保钱包存在
        getUserWallet(userId);
        // 原子扣款：SQL层面保证 balance >= amount 才扣减，无需 Redis 锁
        int rows = baseMapper.deductBalance(userId, coin);
        if (rows > 0) {
            log.info("扣款成功: userId={}, coin={}", userId, coin);
        } else {
            log.warn("扣款失败(余额不足): userId={}, coin={}", userId, coin);
        }
        return rows > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addIncome(Long userId, Long coin) {
        // 确保钱包存在
        getUserWallet(userId);
        int rows = baseMapper.addIncomeBalance(userId, coin);
        if (rows > 0) {
            log.info("收入入账成功: userId={}, coin={}", userId, coin);
        }
        return rows > 0;
    }

    @Override
    public boolean checkBalance(Long userId, Long coin) {
        Wallet wallet = getUserWallet(userId);
        return wallet != null && wallet.getCoinBalance() >= coin;
    }
}
