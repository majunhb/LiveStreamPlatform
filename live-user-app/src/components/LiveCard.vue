<template>
  <view class="live-card" @tap="handleTap">
    <view class="card-cover" :style="{ background: room.cover }">
      <view class="cover-top">
        <LiveBadge />
        <view class="viewer-count">
          <text class="eye-icon">👁️</text>
          <text class="count-text">{{ formatCount(room.viewers) }}</text>
        </view>
      </view>
      <view class="cover-bottom">
        <view class="category-tag">
          <text class="tag-text">{{ room.category }}</text>
        </view>
      </view>
    </view>
    <view class="card-info">
      <view class="anchor-row">
        <UserAvatar :name="room.anchor.nickname" size="sm" />
        <text class="anchor-name ellipsis-1">{{ room.anchor.nickname }}</text>
      </view>
      <text class="room-title ellipsis-2">{{ room.title }}</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import type { LiveRoom } from '@/mock/data'
import UserAvatar from './UserAvatar.vue'
import LiveBadge from './LiveBadge.vue'
import { formatCount } from '@/utils/format'

const props = defineProps<{
  room: LiveRoom
}>()

function handleTap() {
  uni.navigateTo({
    url: `/pages/live/room?roomId=${props.room.id}`
  })
}
</script>

<style lang="scss" scoped>
.live-card {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 16rpx;
  overflow: hidden;
  margin-bottom: 20rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.08);
  transition: transform 0.2s ease;

  &:active {
    transform: scale(0.98);
  }
}

.card-cover {
  position: relative;
  width: 100%;
  padding-top: 130%;
  border-radius: 16rpx 16rpx 0 0;
  overflow: hidden;
}

.cover-top {
  position: absolute;
  top: 16rpx;
  left: 16rpx;
  right: 16rpx;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  z-index: 2;
}

.viewer-count {
  display: flex;
  align-items: center;
  gap: 6rpx;
  background: rgba(0, 0, 0, 0.5);
  padding: 4rpx 12rpx;
  border-radius: 20rpx;
  backdrop-filter: blur(10px);
}

.eye-icon {
  font-size: 20rpx;
}

.count-text {
  font-size: 20rpx;
  color: #fff;
}

.cover-bottom {
  position: absolute;
  bottom: 16rpx;
  left: 16rpx;
  right: 16rpx;
  z-index: 2;
}

.category-tag {
  display: inline-flex;
  background: rgba(0, 0, 0, 0.5);
  padding: 4rpx 12rpx;
  border-radius: 8rpx;
  backdrop-filter: blur(10px);
}

.tag-text {
  font-size: 20rpx;
  color: #fff;
}

.card-info {
  padding: 16rpx;
}

.anchor-row {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-bottom: 8rpx;
}

.anchor-name {
  flex: 1;
  font-size: 24rpx;
  color: #a0a0b0;
}

.room-title {
  font-size: 26rpx;
  color: #fff;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
}
</style>
