package com.livestream.gift.service.impl;

import com.livestream.gift.mapper.WalletMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * WalletServiceImpl 单元测试
 * 使用 Mockito 模拟 WalletMapper 的原子操作方法
 */
@ExtendWith(MockitoExtension.class)
class WalletServiceImplTest {

    @Mock
    private WalletMapper walletMapper;

    @InjectMocks
    private WalletServiceImpl walletService;

    @Test
    @DisplayName("deduct - 余额充足时应返回 true（原子扣款成功）")
    void testDeduct_success() {
        Long userId = 1L;
        Long coin = 100L;

        // 模拟原子扣款返回 1（受影响行数 > 0 表示成功）
        when(walletMapper.deductBalance(userId, coin)).thenReturn(1);

        boolean result = walletService.deduct(userId, coin);

        assertTrue(result, "余额充足时 deduct 应返回 true");
        verify(walletMapper, times(1)).deductBalance(userId, coin);
    }

    @Test
    @DisplayName("deduct - 余额不足时应返回 false（原子扣款返回 0 行）")
    void testDeduct_insufficientBalance() {
        Long userId = 1L;
        Long coin = 99999L;

        // 模拟原子扣款返回 0（余额不足，SQL WHERE 条件不满足）
        when(walletMapper.deductBalance(userId, coin)).thenReturn(0);

        boolean result = walletService.deduct(userId, coin);

        assertFalse(result, "余额不足时 deduct 应返回 false");
        verify(walletMapper, times(1)).deductBalance(userId, coin);
    }

    @Test
    @DisplayName("recharge - 充值成功时应返回 true")
    void testRecharge_success() {
        Long userId = 1L;
        Long coin = 500L;

        when(walletMapper.rechargeBalance(userId, coin)).thenReturn(1);

        boolean result = walletService.recharge(userId, coin);

        assertTrue(result, "充值成功时 recharge 应返回 true");
        verify(walletMapper, times(1)).rechargeBalance(userId, coin);
    }

    @Test
    @DisplayName("addIncome - 收入入账成功时应返回 true")
    void testAddIncome_success() {
        Long userId = 1L;
        Long coin = 200L;

        when(walletMapper.addIncomeBalance(userId, coin)).thenReturn(1);

        boolean result = walletService.addIncome(userId, coin);

        assertTrue(result, "收入入账成功时 addIncome 应返回 true");
        verify(walletMapper, times(1)).addIncomeBalance(userId, coin);
    }
}
