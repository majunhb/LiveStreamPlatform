<template>
  <view v-if="visible" class="gift-panel-overlay" @tap="handleClose">
    <view class="gift-panel" @tap.stop>
      <view class="panel-header">
        <text class="panel-title">送礼物</text>
        <view class="balance-info">
          <text class="coin-icon">💰</text>
          <text class="balance-text">{{ coins }}</text>
          <text class="coin-label">金币</text>
          <view class="recharge-btn" @tap="goRecharge">
            <text class="recharge-icon">+</text>
          </view>
        </view>
        <view class="close-btn" @tap="handleClose">
          <text class="close-icon">✕</text>
        </view>
      </view>

      <scroll-view class="gift-grid" scroll-y>
        <view
          v-for="gift in gifts"
          :key="gift.id"
          class="gift-item"
          :class="{ active: selectedGift?.id === gift.id }"
          @tap="selectGift(gift)"
        >
          <view class="gift-item-inner">
            <text class="gift-icon">{{ gift.icon }}</text>
            <text class="gift-name">{{ gift.name }}</text>
            <view class="gift-price">
              <text class="price-icon">💰</text>
              <text class="price-text">{{ gift.price }}</text>
            </view>
          </view>
        </view>
      </scroll-view>

      <view class="quantity-row">
        <text class="quantity-label">数量</text>
        <view class="quantity-options">
          <view
            v-for="qty in quantityOptions"
            :key="qty"
            class="quantity-option"
            :class="{ active: selectedQuantity === qty }"
            @tap="selectedQuantity = qty"
          >
            <text class="qty-text">{{ qty }}</text>
          </view>
        </view>
      </view>

      <view class="panel-footer">
        <view class="total-info">
          <text class="total-label">总计：</text>
          <text class="total-coins">{{ totalPrice }}</text>
          <text class="total-unit">金币</text>
        </view>
        <view class="send-btn" :class="{ disabled: !canSend, shake: isShaking }" @tap="handleSend">
          <text class="send-text">赠送</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import type { Gift } from '@/mock/data'
import { gifts as mockGifts } from '@/mock/data'

const props = withDefaults(defineProps<{
  visible: boolean
  coins: number
}>(), {
  visible: false,
  coins: 0
})

const emit = defineEmits(['close', 'send'])

const gifts = ref<Gift[]>(mockGifts)
const selectedGift = ref<Gift | null>(null)
const selectedQuantity = ref<number>(1)
const quantityOptions = [1, 10, 66, 99, 520, 1314]
const isShaking = ref(false)

const totalPrice = computed(() => {
  if (!selectedGift.value) return 0
  return selectedGift.value.price * selectedQuantity.value
})

const canSend = computed(() => {
  return selectedGift.value && props.coins >= totalPrice.value
})

function selectGift(gift: Gift) {
  selectedGift.value = gift
}

function handleClose() {
  emit('close')
}

function goRecharge() {
  uni.navigateTo({ url: '/pages/profile/wallet' })
}

function triggerShake() {
  isShaking.value = true
  setTimeout(() => {
    isShaking.value = false
  }, 500)
}

function handleSend() {
  if (!canSend.value || !selectedGift.value) {
    if (props.coins < totalPrice.value) {
      triggerShake()
      uni.showToast({ title: '金币不足', icon: 'none' })
    } else {
      uni.showToast({ title: '请选择礼物', icon: 'none' })
    }
    return
  }
  emit('send', {
    gift: selectedGift.value,
    count: selectedQuantity.value,
    totalPrice: totalPrice.value
  })
}

watch(() => props.visible, (val) => {
  if (!val) {
    selectedGift.value = null
    selectedQuantity.value = 1
  }
})
</script>

<style lang="scss" scoped>
.gift-panel-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  z-index: 1000;
  display: flex;
  align-items: flex-end;
  animation: fadeIn 0.2s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.gift-panel {
  width: 100%;
  background: #1a1a25;
  border-radius: 32rpx 32rpx 0 0;
  padding-bottom: env(safe-area-inset-bottom);
  animation: slideUp 0.3s ease;
}

@keyframes slideUp {
  from { transform: translateY(100%); }
  to { transform: translateY(0); }
}

.panel-header {
  display: flex;
  align-items: center;
  padding: 32rpx;
  border-bottom: 1rpx solid rgba(255, 255, 255, 0.08);
  position: relative;
}

.panel-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #fff;
}

.balance-info {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
}

.coin-icon {
  font-size: 28rpx;
}

.balance-text {
  font-size: 30rpx;
  font-weight: bold;
  color: #FFD700;
}

.coin-label {
  font-size: 24rpx;
  color: #a0a0b0;
}

.recharge-btn {
  width: 40rpx;
  height: 40rpx;
  margin-left: 8rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #FF2D55 0%, #AF52ED 100%);
  border-radius: 50%;
}

.recharge-icon {
  font-size: 28rpx;
  color: #fff;
  font-weight: bold;
  line-height: 1;
}

.close-btn {
  width: 48rpx;
  height: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-icon {
  font-size: 32rpx;
  color: #888;
}

.gift-grid {
  display: flex;
  flex-wrap: wrap;
  padding: 24rpx 12rpx;
  max-height: 500rpx;
}

.gift-item {
  width: 25%;
  padding: 12rpx;
  box-sizing: border-box;
}

.gift-item-inner {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 16rpx;
  padding: 20rpx 12rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  border: 2rpx solid transparent;
  transition: all 0.2s ease;
}

.gift-item.active .gift-item-inner {
  border-color: #FF2D55;
  background: rgba(255, 45, 85, 0.1);
  transform: scale(1.05);
  box-shadow: 0 0 20rpx rgba(255, 45, 85, 0.5);
}

.gift-icon {
  font-size: 56rpx;
  margin-bottom: 8rpx;
  transition: transform 0.2s ease;
}

.gift-item.active .gift-icon {
  transform: scale(1.1);
}

.gift-name {
  font-size: 22rpx;
  color: #fff;
  margin-bottom: 6rpx;
}

.gift-price {
  display: flex;
  align-items: center;
  gap: 4rpx;
}

.price-icon {
  font-size: 18rpx;
}

.price-text {
  font-size: 20rpx;
  color: #FFD700;
}

.quantity-row {
  display: flex;
  align-items: center;
  padding: 0 32rpx;
  margin-bottom: 24rpx;
  gap: 20rpx;
}

.quantity-label {
  font-size: 26rpx;
  color: #a0a0b0;
  flex-shrink: 0;
}

.quantity-options {
  flex: 1;
  display: flex;
  gap: 12rpx;
  overflow-x: auto;
}

.quantity-option {
  flex-shrink: 0;
  padding: 8rpx 20rpx;
  background: rgba(255, 255, 255, 0.08);
  border-radius: 24rpx;
  border: 2rpx solid transparent;
}

.quantity-option.active {
  border-color: #FF2D55;
  background: rgba(255, 45, 85, 0.15);
}

.qty-text {
  font-size: 24rpx;
  color: #fff;
}

.quantity-option.active .qty-text {
  color: #FF2D55;
}

.panel-footer {
  display: flex;
  align-items: center;
  padding: 24rpx 32rpx;
  border-top: 1rpx solid rgba(255, 255, 255, 0.08);
}

.total-info {
  flex: 1;
  display: flex;
  align-items: baseline;
}

.total-label {
  font-size: 26rpx;
  color: #a0a0b0;
}

.total-coins {
  font-size: 36rpx;
  font-weight: bold;
  color: #FFD700;
  margin: 0 6rpx;
}

.total-unit {
  font-size: 24rpx;
  color: #a0a0b0;
}

.send-btn {
  padding: 16rpx 64rpx;
  background: linear-gradient(135deg, #FF2D55 0%, #AF52ED 100%);
  border-radius: 40rpx;
  box-shadow: 0 8rpx 24rpx rgba(255, 45, 85, 0.4);
}

.send-btn.disabled {
  opacity: 0.5;
}

.send-btn.shake {
  animation: shake 0.5s ease;
}

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  10%, 30%, 50%, 70%, 90% { transform: translateX(-8rpx); }
  20%, 40%, 60%, 80% { transform: translateX(8rpx); }
}

.send-text {
  font-size: 30rpx;
  color: #fff;
  font-weight: bold;
}
</style>
