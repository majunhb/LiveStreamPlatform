<template>
  <view v-if="showSplash" class="app-splash">
    <view class="splash-content">
      <text class="splash-logo">LIVE</text>
      <text class="splash-subtitle">星球</text>
      <view class="splash-loading">
        <view class="loading-dot"></view>
        <view class="loading-dot"></view>
        <view class="loading-dot"></view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onLaunch, onShow, onHide } from '@dcloudio/uni-app'
import { useUserStore } from '@/store/user'

const showSplash = ref(true)

onLaunch(() => {
  const userStore = useUserStore()
  userStore.initFromStorage()
  
  setTimeout(() => {
    showSplash.value = false
  }, 1500)
})

onShow(() => {})

onHide(() => {})
</script>

<style lang="scss">
page {
  background-color: #0a0a0f;
  color: #ffffff;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  min-height: 100vh;
}

view, text {
  box-sizing: border-box;
}

.gradient-text {
  background: linear-gradient(135deg, #FF2D55 0%, #AF52ED 100%);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

.glass-card {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  border: 1rpx solid rgba(255, 255, 255, 0.1);
  border-radius: 16rpx;
}

.ellipsis-1 {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.ellipsis-2 {
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
}

.app-splash {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 9999;
  background: linear-gradient(135deg, #0a0a0f 0%, #1a1a25 50%, #0a0a0f 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  animation: splashFadeOut 0.5s ease 1.2s forwards;
}

@keyframes splashFadeOut {
  from { opacity: 1; }
  to { opacity: 0; }
}

.splash-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20rpx;
  animation: splashZoomIn 0.8s ease;
}

@keyframes splashZoomIn {
  from {
    opacity: 0;
    transform: scale(0.8);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.splash-logo {
  font-size: 120rpx;
  font-weight: bold;
  background: linear-gradient(135deg, #FF2D55 0%, #AF52ED 100%);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  letter-spacing: 8rpx;
}

.splash-subtitle {
  font-size: 48rpx;
  color: #fff;
  font-weight: 300;
  letter-spacing: 16rpx;
  margin-bottom: 60rpx;
}

.splash-loading {
  display: flex;
  gap: 16rpx;
}

.loading-dot {
  width: 16rpx;
  height: 16rpx;
  background: linear-gradient(135deg, #FF2D55 0%, #AF52ED 100%);
  border-radius: 50%;
  animation: dotBounce 1.4s ease-in-out infinite both;
}

.loading-dot:nth-child(1) {
  animation-delay: -0.32s;
}

.loading-dot:nth-child(2) {
  animation-delay: -0.16s;
}

.loading-dot:nth-child(3) {
  animation-delay: 0s;
}

@keyframes dotBounce {
  0%, 80%, 100% {
    transform: scale(0);
    opacity: 0.5;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}
</style>
