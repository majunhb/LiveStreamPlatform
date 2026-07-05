<template>
  <view class="wallet-page">
    <view class="header">
      <view class="back-btn" @tap="goBack">
        <text class="back-icon">←</text>
      </view>
      <text class="header-title">我的钱包</text>
      <view class="header-right"></view>
    </view>

    <scroll-view class="content-scroll" scroll-y>
      <view class="balance-card">
        <text class="balance-label">我的金币</text>
        <view class="balance-row">
          <text class="balance-num">{{ userStore.coins }}</text>
          <text class="balance-icon">💰</text>
        </view>
        <view class="recharge-btn" @tap="scrollToRecharge">
          <text class="recharge-text">充值</text>
        </view>
      </view>

      <view class="section" id="recharge-section">
        <text class="section-title">充值套餐</text>
        <view class="package-grid">
          <view
            v-for="pkg in packages"
            :key="pkg.coins"
            class="package-item"
            :class="{ active: selectedPackage === pkg.coins }"
            @tap="selectedPackage = pkg.coins"
          >
            <view class="package-top">
              <text class="package-coins">{{ pkg.coins }}</text>
              <text class="package-unit">金币</text>
            </view>
            <view v-if="pkg.bonus > 0" class="package-bonus">
              <text class="bonus-text">+{{ pkg.bonus }}赠送</text>
            </view>
            <view class="package-price">
              <text class="price-symbol">¥</text>
              <text class="price-num">{{ pkg.price }}</text>
            </view>
          </view>
        </view>
      </view>

      <view class="section">
        <text class="section-title">支付方式</text>
        <view class="pay-methods">
          <view
            class="pay-method"
            :class="{ active: selectedPayMethod === 'wechat' }"
            @tap="selectedPayMethod = 'wechat'"
          >
            <text class="pay-icon">💚</text>
            <text class="pay-name">微信支付</text>
            <view class="pay-check">
              <text v-if="selectedPayMethod === 'wechat'" class="check-icon">✓</text>
            </view>
          </view>
          <view
            class="pay-method"
            :class="{ active: selectedPayMethod === 'alipay' }"
            @tap="selectedPayMethod = 'alipay'"
          >
            <text class="pay-icon">💙</text>
            <text class="pay-name">支付宝</text>
            <view class="pay-check">
              <text v-if="selectedPayMethod === 'alipay'" class="check-icon">✓</text>
            </view>
          </view>
        </view>
      </view>

      <view class="confirm-section">
        <view class="confirm-info">
          <text class="confirm-label">支付金额：</text>
          <text class="confirm-amount">¥{{ selectedPrice }}</text>
        </view>
        <view class="confirm-btn" @tap="handleRecharge">
          <text class="confirm-text">立即充值</text>
        </view>
      </view>

      <view class="section">
        <text class="section-title">交易记录</text>
        <view class="record-list">
          <view
            v-for="record in records"
            :key="record.id"
            class="record-item"
          >
            <view class="record-left">
              <text class="record-icon">{{ record.type === 'recharge' ? '➕' : '➖' }}</text>
              <view class="record-info">
                <text class="record-desc">{{ record.description }}</text>
                <text class="record-time">{{ record.time }}</text>
              </view>
            </view>
            <view class="record-right">
              <text class="record-coins" :class="record.type">
                {{ record.type === 'recharge' ? '+' : '-' }}{{ record.coins }}
              </text>
            </view>
          </view>
        </view>
      </view>

      <view class="bottom-space"></view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useUserStore } from '@/store/user'
import { rechargePackages, transactionRecords } from '@/mock/data'
import type { TransactionRecord } from '@/mock/data'

const userStore = useUserStore()
const packages = ref(rechargePackages)
const records = ref<TransactionRecord[]>(transactionRecords)
const selectedPackage = ref(100)
const selectedPayMethod = ref('wechat')

const selectedPrice = computed(() => {
  const pkg = packages.value.find(p => p.coins === selectedPackage.value)
  return pkg?.price || 0
})

function goBack() {
  uni.navigateBack()
}

function scrollToRecharge() {
  uni.pageScrollTo({ selector: '#recharge-section', duration: 300 })
}

async function handleRecharge() {
  const pkg = packages.value.find(p => p.coins === selectedPackage.value)
  if (!pkg) return

  uni.showModal({
    title: '确认充值',
    content: `确认充值 ${pkg.coins} 金币（含赠送 ${pkg.bonus}），支付 ¥${pkg.price}？`,
    success: async (res) => {
      if (res.confirm) {
        uni.showLoading({ title: '充值中...' })
        await new Promise(r => setTimeout(r, 800))
        uni.hideLoading()

        const totalCoins = pkg.coins + pkg.bonus
        userStore.addCoins(totalCoins)

        uni.showToast({
          title: `充值成功，获得 ${totalCoins} 金币`,
          icon: 'success'
        })
      }
    }
  })
}
</script>

<style lang="scss" scoped>
.wallet-page {
  min-height: 100vh;
  background: #0a0a0f;
  display: flex;
  flex-direction: column;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 80rpx 32rpx 24rpx;
  background: linear-gradient(180deg, rgba(255, 45, 85, 0.2) 0%, transparent 100%);
}

.back-btn {
  width: 64rpx;
  height: 64rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.back-icon {
  font-size: 36rpx;
  color: #fff;
}

.header-title {
  font-size: 34rpx;
  font-weight: bold;
  color: #fff;
}

.header-right {
  width: 64rpx;
}

.content-scroll {
  flex: 1;
}

.balance-card {
  margin: 24rpx 32rpx;
  padding: 40rpx 32rpx;
  background: linear-gradient(135deg, #FF2D55 0%, #AF52ED 100%);
  border-radius: 24rpx;
  position: relative;
  overflow: hidden;
}

.balance-label {
  font-size: 28rpx;
  color: rgba(255, 255, 255, 0.8);
}

.balance-row {
  display: flex;
  align-items: baseline;
  gap: 12rpx;
  margin: 16rpx 0;
}

.balance-num {
  font-size: 64rpx;
  font-weight: bold;
  color: #fff;
}

.balance-icon {
  font-size: 40rpx;
}

.recharge-btn {
  position: absolute;
  right: 32rpx;
  top: 50%;
  transform: translateY(-50%);
  padding: 16rpx 32rpx;
  background: rgba(255, 255, 255, 0.2);
  border: 1rpx solid rgba(255, 255, 255, 0.3);
  border-radius: 32rpx;
}

.recharge-text {
  font-size: 28rpx;
  color: #fff;
  font-weight: 500;
}

.section {
  margin: 32rpx;
}

.section-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #fff;
  margin-bottom: 20rpx;
  display: block;
}

.package-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.package-item {
  width: calc(33.33% - 12rpx);
  background: rgba(255, 255, 255, 0.05);
  border: 2rpx solid rgba(255, 255, 255, 0.08);
  border-radius: 16rpx;
  padding: 24rpx 16rpx;
  text-align: center;
  position: relative;
  transition: all 0.2s ease;
}

.package-item.active {
  border-color: #FF2D55;
  background: rgba(255, 45, 85, 0.1);
}

.package-top {
  display: flex;
  align-items: baseline;
  justify-content: center;
  gap: 4rpx;
}

.package-coins {
  font-size: 36rpx;
  font-weight: bold;
  color: #fff;
}

.package-unit {
  font-size: 22rpx;
  color: #888;
}

.package-bonus {
  margin-top: 8rpx;
}

.bonus-text {
  font-size: 20rpx;
  color: #FFD700;
}

.package-price {
  margin-top: 12rpx;
  display: flex;
  align-items: baseline;
  justify-content: center;
  gap: 2rpx;
}

.price-symbol {
  font-size: 22rpx;
  color: #FFD700;
}

.price-num {
  font-size: 28rpx;
  font-weight: bold;
  color: #FFD700;
}

.pay-methods {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.pay-method {
  display: flex;
  align-items: center;
  gap: 20rpx;
  padding: 24rpx;
  background: rgba(255, 255, 255, 0.05);
  border: 2rpx solid rgba(255, 255, 255, 0.08);
  border-radius: 16rpx;
}

.pay-method.active {
  border-color: #FF2D55;
}

.pay-icon {
  font-size: 40rpx;
}

.pay-name {
  flex: 1;
  font-size: 28rpx;
  color: #fff;
}

.pay-check {
  width: 36rpx;
  height: 36rpx;
  border-radius: 50%;
  border: 2rpx solid rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
}

.pay-method.active .pay-check {
  background: #FF2D55;
  border-color: #FF2D55;
}

.check-icon {
  font-size: 22rpx;
  color: #fff;
}

.confirm-section {
  display: flex;
  align-items: center;
  margin: 32rpx;
  padding: 24rpx;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 16rpx;
}

.confirm-info {
  flex: 1;
  display: flex;
  align-items: baseline;
}

.confirm-label {
  font-size: 28rpx;
  color: #888;
}

.confirm-amount {
  font-size: 40rpx;
  font-weight: bold;
  color: #FFD700;
}

.confirm-btn {
  padding: 20rpx 48rpx;
  background: linear-gradient(135deg, #FF2D55 0%, #AF52ED 100%);
  border-radius: 40rpx;
  box-shadow: 0 8rpx 24rpx rgba(255, 45, 85, 0.4);
}

.confirm-text {
  font-size: 28rpx;
  color: #fff;
  font-weight: bold;
}

.record-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.record-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12rpx;
}

.record-left {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.record-icon {
  font-size: 32rpx;
}

.record-info {
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}

.record-desc {
  font-size: 26rpx;
  color: #fff;
}

.record-time {
  font-size: 22rpx;
  color: #666;
}

.record-coins {
  font-size: 28rpx;
  font-weight: bold;
}

.record-coins.recharge {
  color: #4ECDC4;
}

.record-coins.spend {
  color: #FF6B6B;
}

.bottom-space {
  height: 60rpx;
}
</style>
