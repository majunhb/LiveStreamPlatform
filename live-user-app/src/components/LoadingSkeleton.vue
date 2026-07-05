<template>
  <view class="loading-skeleton">
    <view v-for="i in count" :key="i" class="skeleton-card">
      <view class="skeleton-cover shimmer"></view>
      <view class="skeleton-info">
        <view class="skeleton-line short shimmer"></view>
        <view class="skeleton-line shimmer"></view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
withDefaults(defineProps<{
  count?: number
}>(), {
  count: 8
})
</script>

<style lang="scss" scoped>
.loading-skeleton {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  padding: 0 16rpx;
}

.skeleton-card {
  width: calc(50% - 8rpx);
  background: rgba(255, 255, 255, 0.05);
  border-radius: 16rpx;
  overflow: hidden;
  margin-bottom: 20rpx;
}

.skeleton-cover {
  width: 100%;
  padding-top: 130%;
  background: rgba(255, 255, 255, 0.08);
}

.skeleton-info {
  padding: 16rpx;
}

.skeleton-line {
  height: 20rpx;
  background: rgba(255, 255, 255, 0.08);
  border-radius: 10rpx;
  margin-bottom: 10rpx;
}

.skeleton-line.short {
  width: 40%;
}

.shimmer {
  position: relative;
  overflow: hidden;
}

.shimmer::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    90deg,
    transparent 0%,
    rgba(255, 255, 255, 0.1) 50%,
    transparent 100%
  );
  animation: shimmer 1.5s infinite;
}

@keyframes shimmer {
  0% {
    left: -100%;
  }
  100% {
    left: 100%;
  }
}
</style>
