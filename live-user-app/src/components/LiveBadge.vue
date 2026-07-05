<template>
  <view class="live-badge">
    <text class="live-text">LIVE</text>
    <view class="pulse-ring"></view>
    <view class="pulse-ring delay"></view>
  </view>
</template>

<script setup lang="ts">
</script>

<style lang="scss" scoped>
.live-badge {
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #FF2D55 0%, #FF6B6B 100%);
  padding: 4rpx 16rpx;
  border-radius: 8rpx;
  font-size: 20rpx;
  font-weight: bold;
  color: #fff;
  box-shadow: 0 4rpx 12rpx rgba(255, 45, 85, 0.4);
}

.live-text {
  position: relative;
  z-index: 2;
  color: #fff;
  font-weight: bold;
  letter-spacing: 1rpx;
}

.pulse-ring {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 100%;
  height: 100%;
  transform: translate(-50%, -50%);
  border-radius: 8rpx;
  background: rgba(255, 45, 85, 0.5);
  animation: pulse 1.5s ease-out infinite;
  z-index: 1;

  &.delay {
    animation-delay: 0.75s;
  }
}

@keyframes pulse {
  0% {
    transform: translate(-50%, -50%) scale(1);
    opacity: 0.7;
  }
  100% {
    transform: translate(-50%, -50%) scale(1.6);
    opacity: 0;
  }
}
</style>
