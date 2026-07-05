<template>
  <view class="gift-toast-container">
    <view
      v-for="toast in toasts"
      :key="toast.id"
      class="gift-toast"
      :class="{ 'toast-enter': toast.entering, 'toast-leave': toast.leaving }"
    >
      <view class="toast-inner">
        <view class="sender-info">
          <text class="sender-name">{{ toast.senderName }}</text>
          <text class="to-divider">送出</text>
        </view>
        <view class="gift-info">
          <text class="gift-icon">{{ toast.giftIcon }}</text>
          <text class="gift-name">{{ toast.giftName }}</text>
          <text v-if="toast.count > 1" class="gift-count">x{{ toast.count }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'

interface GiftToastItem {
  id: string
  senderName: string
  giftName: string
  giftIcon: string
  count: number
  entering: boolean
  leaving: boolean
}

const toasts = ref<GiftToastItem[]>([])
let idCounter = 0

function showToast(senderName: string, giftName: string, giftIcon: string, count: number) {
  const id = `toast_${idCounter++}`
  const toast: GiftToastItem = {
    id,
    senderName,
    giftName,
    giftIcon,
    count,
    entering: true,
    leaving: false
  }

  toasts.value.push(toast)

  setTimeout(() => {
    const item = toasts.value.find(t => t.id === id)
    if (item) item.entering = false
  }, 50)

  setTimeout(() => {
    const item = toasts.value.find(t => t.id === id)
    if (item) item.leaving = true
  }, 2500)

  setTimeout(() => {
    const index = toasts.value.findIndex(t => t.id === id)
    if (index > -1) {
      toasts.value.splice(index, 1)
    }
  }, 3000)
}

defineExpose({ showToast })
</script>

<style lang="scss" scoped>
.gift-toast-container {
  position: fixed;
  top: 200rpx;
  left: 24rpx;
  z-index: 500;
  display: flex;
  flex-direction: column;
  gap: 16rpx;
  pointer-events: none;
}

.gift-toast {
  transform: translateX(-100%);
  opacity: 0;
  transition: all 0.3s ease;
}

.gift-toast.toast-enter {
  transform: translateX(0);
  opacity: 1;
}

.gift-toast.toast-leave {
  transform: translateX(-100%);
  opacity: 0;
}

.toast-inner {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(10px);
  padding: 12rpx 24rpx;
  border-radius: 32rpx;
  border: 1rpx solid rgba(255, 215, 0, 0.3);
}

.sender-info {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.sender-name {
  font-size: 24rpx;
  color: #FFD700;
  font-weight: 500;
}

.to-divider {
  font-size: 22rpx;
  color: rgba(255, 255, 255, 0.6);
}

.gift-info {
  display: flex;
  align-items: center;
  gap: 10rpx;
}

.gift-icon {
  font-size: 40rpx;
}

.gift-name {
  font-size: 26rpx;
  color: #fff;
  font-weight: 500;
}

.gift-count {
  font-size: 26rpx;
  color: #FFD700;
  font-weight: bold;
}
</style>
