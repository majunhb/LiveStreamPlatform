package com.livestream.gift.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.livestream.gift.entity.Wallet;
import com.livestream.gift.mapper.WalletMapper;
import com.livestream.gift.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 钱包服务实现类
 * 使用数据库原子操作替代 Redis 分布式锁，避免并发扣款导致的余额不一致问题
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WalletServiceImpl extends ServiceImpl<WalletMapper, Wallet> implements WalletService {

    private final RedisTemplate<String, Object> redisTemplate;

    /** S-02安全修复：Lua脚本用于安全的Redis锁释放（CAS模式：比对value后再删除） */
    private static final DefaultRedisScript<Long> UNLOCK_SCRIPT;
    static {
        UNLOCK_SCRIPT = new DefaultRedisScript<>();
        UNLOCK_SCRIPT.setScriptText(
            "if redis.call('get',KEYS[1])==ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end"
        );
        UNLOCK_SCRIPT.setResultType(Long.class);
    }

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
        // S-02安全修复：使用UUID作为锁值 + Lua脚本比对后释放（CAS模式），防止误删其他线程的锁
        String lockKey = "wallet:lock:" + userId;
        String lockValue = UUID.randomUUID().toString();
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, 10, TimeUnit.SECONDS);
        if (!Boolean.TRUE.equals(locked)) {
            log.warn("获取钱包锁失败: userId={}", userId);
            throw new RuntimeException("系统繁忙，请稍后重试");
        }
        try {
            // S-01安全修复：原子扣款SQL（WHERE coin_balance >= amount），消除TOCTOU竞态条件
            int rows = baseMapper.deductBalance(userId, coin);
            if (rows > 0) {
                log.info("扣款成功: userId={}, coin={}", userId, coin);
            } else {
                log.warn("扣款失败(余额不足): userId={}, coin={}", userId, coin);
            }
            return rows > 0;
        } finally {
            // 使用Lua脚本安全释放锁：仅当lockValue匹配时才删除，避免误删其他线程持有的锁
            redisTemplate.execute(UNLOCK_SCRIPT, Collections.singletonList(lockKey), lockValue);
        }
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
