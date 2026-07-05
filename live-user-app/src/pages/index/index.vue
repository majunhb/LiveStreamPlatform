<template>
  <view class="home-page">
    <view class="status-bar"></view>
    <view class="header">
      <view class="header-left">
        <text class="logo-text">LIVE星球</text>
      </view>
      <view class="header-right">
        <view class="header-btn" @tap="goSearch">
          <text class="btn-icon">🔍</text>
        </view>
        <view class="header-btn">
          <text class="btn-icon">📷</text>
        </view>
      </view>
    </view>

    <scroll-view
      class="category-scroll"
      scroll-x
      :show-scrollbar="false"
    >
      <view class="category-list">
        <view
          v-for="cat in categories"
          :key="cat"
          class="category-item"
          :class="{ active: currentCategory === cat }"
          @tap="switchCategory(cat)"
        >
          <text class="cat-text">{{ cat }}</text>
          <view v-if="currentCategory === cat" class="cat-indicator"></view>
        </view>
      </view>
    </scroll-view>

    <scroll-view
      class="content-scroll"
      scroll-y
      :refresher-enabled="true"
      :refresher-triggered="refreshing"
      @refresherrefresh="onRefresh"
    >
      <view class="hot-live-section" v-if="!loading">
        <view class="section-header">
            <text class="section-icon">🔥</text>
            <text class="section-title">热门直播</text>
            <text class="section-more">更多 ›</text>
          </view>
        <scroll-view class="hot-live-scroll" scroll-x :show-scrollbar="false" @scroll="onHotLiveScroll">
          <view class="hot-live-track">
            <view
              v-for="(room, index) in hotLives"
              :key="room.id"
              class="hot-live-card"
              @tap="goLiveRoom(room.id)"
            >
              <view class="hot-live-cover" :style="{ background: room.cover }">
                <view class="hot-live-overlay"></view>
                <view class="hot-live-top">
                  <LiveBadge />
                  <view class="hot-viewer-count">
                    <text class="eye-icon">👁️</text>
                    <text class="count-text">{{ formatCount(room.viewers) }}</text>
                  </view>
                </view>
                <view class="hot-live-bottom">
                  <text class="hot-live-title">{{ room.title }}</text>
                  <view class="hot-live-anchor">
                    <UserAvatar :name="room.anchor.nickname" size="xs" />
                    <text class="hot-anchor-name">{{ room.anchor.nickname }}</text>
                  </view>
                </view>
              </view>
            </view>
          </view>
        </scroll-view>
        <view class="hot-live-dots">
          <view
            v-for="(_, i) in hotLives"
            :key="i"
            class="dot"
            :class="{ active: hotLiveCurrent === i }"
          ></view>
        </view>
      </view>

      <view class="live-grid" :class="{ 'fade-in': !loading, 'stagger-container': !loading }">
        <template v-if="loading">
          <LoadingSkeleton :count="8" />
        </template>
        <template v-else>
          <view
            class="grid-item stagger-item"
            v-for="(room, index) in liveList"
            :key="room.id"
            :style="{ animationDelay: `${index * 50}ms` }"
          >
            <LiveCard :room="room" />
          </view>
        </template>
      </view>

      <view class="hot-banner" v-if="!loading">
        <view class="banner-left">
          <text class="banner-icon">🔥</text>
          <view class="banner-text">
            <text class="banner-title">热门活动</text>
            <text class="banner-desc">新人主播福利，快来参与！</text>
          </view>
        </view>
        <view class="banner-btn">
          <text class="btn-text">去看看</text>
        </view>
      </view>

      <view class="bottom-space"></view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import LiveCard from '@/components/LiveCard.vue'
import LoadingSkeleton from '@/components/LoadingSkeleton.vue'
import LiveBadge from '@/components/LiveBadge.vue'
import UserAvatar from '@/components/UserAvatar.vue'
import { categories, liveRooms, hotLives } from '@/mock/data'
import type { LiveRoom } from '@/mock/data'
import { formatCount } from '@/utils/format'

const currentCategory = ref('推荐')
const liveList = ref<LiveRoom[]>([])
const refreshing = ref(false)
const loading = ref(true)
const hotLiveCurrent = ref(0)

onMounted(() => {
  setTimeout(() => {
    loadLiveList()
    loading.value = false
  }, 1200)
})

function loadLiveList() {
  if (currentCategory.value === '推荐' || currentCategory.value === '关注') {
    liveList.value = [...liveRooms]
  } else {
    liveList.value = liveRooms.filter(r => r.category === currentCategory.value)
  }
}

function switchCategory(cat: string) {
  if (cat === currentCategory.value) return
  loading.value = true
  currentCategory.value = cat
  setTimeout(() => {
    loadLiveList()
    loading.value = false
  }, 300)
}

function onRefresh() {
  refreshing.value = true
  setTimeout(() => {
    loadLiveList()
    refreshing.value = false
    uni.showToast({ title: '刷新成功', icon: 'none' })
  }, 800)
}

function goSearch() {
  uni.navigateTo({ url: '/pages/search/index' })
}

function goLiveRoom(roomId: string) {
  uni.navigateTo({ url: `/pages/live/room?roomId=${roomId}` })
}

function onHotLiveScroll(e: any) {
  const scrollLeft = e.detail.scrollLeft
  const cardWidth = 280 + 24
  const index = Math.round(scrollLeft / cardWidth)
  hotLiveCurrent.value = Math.min(Math.max(0, index), hotLives.length - 1)
}
</script>

<style lang="scss" scoped>
.home-page {
  min-height: 100vh;
  background: #0a0a0f;
  display: flex;
  flex-direction: column;
}

.status-bar {
  height: var(--status-bar-height, 44px);
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16rpx 32rpx;
  background: linear-gradient(180deg, rgba(10, 10, 15, 1) 0%, rgba(10, 10, 15, 0.95) 100%);
}

.header-left {
  display: flex;
  align-items: center;
}

.logo-text {
  font-size: 40rpx;
  font-weight: bold;
  background: linear-gradient(135deg, #FF2D55 0%, #AF52ED 100%);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 24rpx;
}

.header-btn {
  width: 64rpx;
  height: 64rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.08);
  border-radius: 50%;
  min-width: 88rpx;
  min-height: 88rpx;
}

.btn-icon {
  font-size: 32rpx;
}

.category-scroll {
  flex-shrink: 0;
  background: #0a0a0f;
  padding: 0 16rpx;
}

.category-list {
  display: flex;
  gap: 8rpx;
  padding: 16rpx 0;
  white-space: nowrap;
}

.category-item {
  position: relative;
  padding: 12rpx 28rpx;
  flex-shrink: 0;
  min-width: 88rpx;
  min-height: 44rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.category-item.active .cat-text {
  color: #fff;
  font-weight: bold;
  font-size: 32rpx;
}

.cat-text {
  font-size: 28rpx;
  color: #888;
  transition: all 0.2s ease;
}

.cat-indicator {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 32rpx;
  height: 6rpx;
  background: linear-gradient(135deg, #FF2D55 0%, #AF52ED 100%);
  border-radius: 3rpx;
}

.content-scroll {
  flex: 1;
  padding: 0 16rpx;
}

.hot-live-section {
  margin-bottom: 24rpx;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 0 8rpx 16rpx;
}

.section-icon {
  font-size: 28rpx;
}

.section-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #fff;
  flex: 1;
}

.section-more {
  font-size: 24rpx;
  color: #888;
}

.hot-live-scroll {
  width: 100%;
  white-space: nowrap;
}

.hot-live-track {
  display: inline-flex;
  gap: 24rpx;
  padding: 0 8rpx;
}

.hot-live-card {
  width: 560rpx;
  flex-shrink: 0;
}

.hot-live-cover {
  position: relative;
  width: 100%;
  height: 320rpx;
  border-radius: 20rpx;
  overflow: hidden;
}

.hot-live-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(transparent 40%, rgba(0, 0, 0, 0.7) 100%);
}

.hot-live-top {
  position: absolute;
  top: 16rpx;
  left: 16rpx;
  right: 16rpx;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  z-index: 2;
}

.hot-viewer-count {
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

.hot-live-bottom {
  position: absolute;
  bottom: 16rpx;
  left: 16rpx;
  right: 16rpx;
  z-index: 2;
}

.hot-live-title {
  display: block;
  font-size: 28rpx;
  font-weight: bold;
  color: #fff;
  margin-bottom: 8rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.hot-live-anchor {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.hot-anchor-name {
  font-size: 22rpx;
  color: rgba(255, 255, 255, 0.8);
}

.hot-live-dots {
  display: flex;
  justify-content: center;
  gap: 12rpx;
  margin-top: 16rpx;
}

.dot {
  width: 12rpx;
  height: 12rpx;
  border-radius: 6rpx;
  background: rgba(255, 255, 255, 0.2);
  transition: all 0.3s ease;
}

.dot.active {
  width: 24rpx;
  background: linear-gradient(135deg, #FF2D55 0%, #AF52ED 100%);
}

.live-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.live-grid.fade-in {
  animation: fadeInUp 0.4s ease;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.grid-item {
  width: calc(50% - 8rpx);
}

.stagger-container .stagger-item {
  opacity: 0;
  animation: staggerFadeIn 0.5s ease forwards;
}

@keyframes staggerFadeIn {
  from {
    opacity: 0;
    transform: translateY(20rpx);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.hot-banner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 24rpx 0;
  padding: 24rpx;
  background: linear-gradient(135deg, rgba(255, 45, 85, 0.2) 0%, rgba(175, 82, 237, 0.2) 100%);
  border-radius: 16rpx;
  border: 1rpx solid rgba(255, 45, 85, 0.3);
}

.banner-left {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.banner-icon {
  font-size: 48rpx;
}

.banner-text {
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}

.banner-title {
  font-size: 28rpx;
  font-weight: bold;
  color: #fff;
}

.banner-desc {
  font-size: 22rpx;
  color: #a0a0b0;
}

.banner-btn {
  padding: 12rpx 28rpx;
  background: linear-gradient(135deg, #FF2D55 0%, #AF52ED 100%);
  border-radius: 24rpx;
  min-width: 88rpx;
  min-height: 44rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-text {
  font-size: 24rpx;
  color: #fff;
  font-weight: 500;
}

.bottom-space {
  height: 120rpx;
}
</style>
