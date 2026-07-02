package com.livestream.gift.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.livestream.gift.entity.Wallet;
import com.livestream.gift.mapper.WalletMapper;
import com.livestream.gift.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

/**
 * 钱包服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WalletServiceImpl extends ServiceImpl<WalletMapper, Wallet> implements WalletService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String WALLET_LOCK_KEY = "wallet:lock:";
    private static final long LOCK_TIMEOUT = 5;

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
        Wallet wallet = getUserWallet(userId);
        wallet.setCoinBalance(wallet.getCoinBalance() + coin);
        wallet.setTotalRechargeCoin(wallet.getTotalRechargeCoin() + coin);
        return updateById(wallet);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deduct(Long userId, Long coin) {
        String lockKey = WALLET_LOCK_KEY + userId;
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", LOCK_TIMEOUT, TimeUnit.SECONDS);
        if (!Boolean.TRUE.equals(locked)) {
            throw new RuntimeException("系统繁忙，请稍后重试");
        }
        
        try {
            Wallet wallet = getUserWallet(userId);
            if (wallet.getCoinBalance() < coin) {
                throw new RuntimeException("金币不足");
            }
            
            wallet.setCoinBalance(wallet.getCoinBalance() - coin);
            wallet.setTotalConsumeCoin(wallet.getTotalConsumeCoin() + coin);
            return updateById(wallet);
        } finally {
            redisTemplate.delete(lockKey);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addIncome(Long userId, Long coin) {
        Wallet wallet = getUserWallet(userId);
        wallet.setCoinBalance(wallet.getCoinBalance() + coin);
        wallet.setTotalIncomeCoin(wallet.getTotalIncomeCoin() + coin);
        return updateById(wallet);
    }

    @Override
    public boolean checkBalance(Long userId, Long coin) {
        Wallet wallet = getUserWallet(userId);
        return wallet != null && wallet.getCoinBalance() >= coin;
    }
}
