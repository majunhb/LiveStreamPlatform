<template>
  <view class="search-page">
    <view class="header">
      <view class="back-btn" @tap="goBack">
        <text class="back-icon">←</text>
      </view>
      <view class="search-bar">
        <text class="search-icon">🔍</text>
        <input
          class="search-input"
          v-model="keyword"
          placeholder="搜索主播、直播间、视频"
          placeholder-class="input-placeholder"
          confirm-type="search"
          focus
          @confirm="handleSearch"
        />
        <view v-if="keyword" class="clear-btn" @tap="clearKeyword">
          <text class="clear-icon">✕</text>
        </view>
      </view>
      <view class="search-btn" @tap="handleSearch">
        <text class="btn-text">搜索</text>
      </view>
    </view>

    <scroll-view class="content-scroll" scroll-y>
      <view v-if="!hasSearched" class="search-initial">
        <view class="hot-section">
          <view class="section-header">
            <text class="section-title">🔥 热门搜索</text>
          </view>
          <view class="hot-tags">
            <view
              v-for="(tag, index) in hotSearches"
              :key="tag"
              class="hot-tag"
              @tap="searchTag(tag)"
            >
              <text class="tag-index" :class="{ top: index < 3 }">{{ index + 1 }}</text>
              <text class="tag-text">{{ tag }}</text>
            </view>
          </view>
        </view>

        <view v-if="searchHistory.length > 0" class="history-section">
          <view class="section-header">
            <text class="section-title">🕐 搜索历史</text>
            <view class="clear-history" @tap="clearHistory">
              <text class="clear-text">清空</text>
            </view>
          </view>
          <view class="history-tags">
            <view
              v-for="tag in searchHistory"
              :key="tag"
              class="history-tag"
              @tap="searchTag(tag)"
            >
              <text class="tag-text">{{ tag }}</text>
            </view>
          </view>
        </view>
      </view>

      <view v-else class="search-results">
        <view class="result-tabs">
          <view
            class="result-tab"
            :class="{ active: resultTab === 'live' }"
            @tap="resultTab = 'live'"
          >
            <text class="tab-text">直播</text>
          </view>
          <view
            class="result-tab"
            :class="{ active: resultTab === 'video' }"
            @tap="resultTab = 'video'"
          >
            <text class="tab-text">视频</text>
          </view>
        </view>

        <view v-if="resultTab === 'live'" class="result-list">
          <view
            v-for="room in liveResults"
            :key="room.id"
            class="result-live-item"
            @tap="goLiveRoom(room.id)"
          >
            <view class="live-cover" :style="{ background: room.cover }">
              <LiveBadge />
            </view>
            <view class="live-info">
              <text class="live-title ellipsis-1">{{ room.title }}</text>
              <view class="live-meta">
                <UserAvatar :name="room.anchor.nickname" size="sm" />
                <text class="anchor-name">{{ room.anchor.nickname }}</text>
                <text class="viewer-count">👁️ {{ formatCount(room.viewers) }}</text>
              </view>
            </view>
          </view>
          <view v-if="liveResults.length === 0" class="empty-state">
            <text class="empty-icon">🔍</text>
            <text class="empty-text">未找到相关直播间</text>
          </view>
        </view>

        <view v-else class="result-list">
          <view class="video-grid">
            <view
              v-for="video in videoResults"
              :key="video.id"
              class="video-item"
            >
              <view class="video-cover" :style="{ background: video.cover }">
                <view class="video-stats">
                  <text class="stat-icon">❤️</text>
                  <text class="stat-text">{{ formatCount(video.likes) }}</text>
                </view>
              </view>
              <text class="video-title ellipsis-1">{{ video.title }}</text>
            </view>
          </view>
          <view v-if="videoResults.length === 0" class="empty-state">
            <text class="empty-icon">🔍</text>
            <text class="empty-text">未找到相关视频</text>
          </view>
        </view>
      </view>

      <view class="bottom-space"></view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import LiveBadge from '@/components/LiveBadge.vue'
import UserAvatar from '@/components/UserAvatar.vue'
import { liveRooms, shortVideos, hotSearches } from '@/mock/data'
import { formatCount } from '@/utils/format'

const keyword = ref('')
const hasSearched = ref(false)
const resultTab = ref('live')
const searchHistory = ref<string[]>(['王者荣耀', '热门歌曲'])

const liveResults = computed(() => {
  if (!keyword.value) return []
  return liveRooms.filter(r =>
    r.title.includes(keyword.value) ||
    r.anchor.nickname.includes(keyword.value) ||
    r.category.includes(keyword.value)
  )
})

const videoResults = computed(() => {
  if (!keyword.value) return []
  return shortVideos.filter(v =>
    v.title.includes(keyword.value) ||
    v.author.nickname.includes(keyword.value)
  )
})

function goBack() {
  uni.navigateBack()
}

function clearKeyword() {
  keyword.value = ''
}

function handleSearch() {
  if (!keyword.value.trim()) {
    uni.showToast({ title: '请输入搜索内容', icon: 'none' })
    return
  }
  hasSearched.value = true
  if (!searchHistory.value.includes(keyword.value)) {
    searchHistory.value.unshift(keyword.value)
    if (searchHistory.value.length > 10) {
      searchHistory.value.pop()
    }
    uni.setStorageSync('searchHistory', JSON.stringify(searchHistory.value))
  }
}

function searchTag(tag: string) {
  keyword.value = tag
  handleSearch()
}

function clearHistory() {
  searchHistory.value = []
  uni.removeStorageSync('searchHistory')
}

function goLiveRoom(roomId: string) {
  uni.navigateTo({ url: `/pages/live/room?roomId=${roomId}` })
}
</script>

<style lang="scss" scoped>
.search-page {
  min-height: 100vh;
  background: #0a0a0f;
  display: flex;
  flex-direction: column;
}

.header {
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 80rpx 24rpx 20rpx;
  background: #0a0a0f;
}

.back-btn {
  width: 56rpx;
  height: 56rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.back-icon {
  font-size: 36rpx;
  color: #fff;
}

.search-bar {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 12rpx;
  background: rgba(255, 255, 255, 0.08);
  border-radius: 32rpx;
  padding: 0 20rpx;
  height: 72rpx;
}

.search-icon {
  font-size: 28rpx;
  flex-shrink: 0;
}

.search-input {
  flex: 1;
  height: 72rpx;
  font-size: 28rpx;
  color: #fff;
}

.input-placeholder {
  color: #666;
}

.clear-btn {
  width: 36rpx;
  height: 36rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  flex-shrink: 0;
}

.clear-icon {
  font-size: 20rpx;
  color: #888;
}

.search-btn {
  flex-shrink: 0;
}

.btn-text {
  font-size: 28rpx;
  color: #FF2D55;
  font-weight: 500;
}

.content-scroll {
  flex: 1;
}

.search-initial {
  padding: 24rpx 32rpx;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20rpx;
}

.section-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #fff;
}

.hot-section {
  margin-bottom: 40rpx;
}

.hot-tags {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.hot-tag {
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 16rpx 0;
}

.tag-index {
  width: 40rpx;
  height: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 8rpx;
  font-size: 24rpx;
  color: #888;
  font-weight: bold;
}

.tag-index.top {
  background: linear-gradient(135deg, #FF2D55 0%, #AF52ED 100%);
  color: #fff;
}

.tag-text {
  font-size: 28rpx;
  color: #fff;
}

.clear-history {
  padding: 8rpx 16rpx;
}

.clear-text {
  font-size: 24rpx;
  color: #666;
}

.history-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.history-tag {
  padding: 12rpx 24rpx;
  background: rgba(255, 255, 255, 0.08);
  border-radius: 24rpx;
}

.history-tag .tag-text {
  font-size: 26rpx;
  color: #a0a0b0;
}

.search-results {
  padding: 0 32rpx;
}

.result-tabs {
  display: flex;
  gap: 40rpx;
  padding: 20rpx 0;
  border-bottom: 1rpx solid rgba(255, 255, 255, 0.08);
  margin-bottom: 20rpx;
}

.result-tab {
  position: relative;
  padding-bottom: 12rpx;
}

.result-tab.active .tab-text {
  color: #fff;
  font-weight: bold;
  font-size: 32rpx;
}

.tab-text {
  font-size: 28rpx;
  color: #888;
}

.result-tab.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 40rpx;
  height: 4rpx;
  background: linear-gradient(135deg, #FF2D55 0%, #AF52ED 100%);
  border-radius: 2rpx;
}

.result-list {
  min-height: 400rpx;
}

.result-live-item {
  display: flex;
  gap: 20rpx;
  padding: 16rpx 0;
  border-bottom: 1rpx solid rgba(255, 255, 255, 0.05);
}

.live-cover {
  width: 200rpx;
  height: 260rpx;
  border-radius: 12rpx;
  position: relative;
  flex-shrink: 0;
  display: flex;
  padding: 12rpx;
  box-sizing: border-box;
}

.live-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 8rpx 0;
}

.live-title {
  font-size: 28rpx;
  color: #fff;
  font-weight: 500;
}

.live-meta {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.anchor-name {
  font-size: 24rpx;
  color: #888;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.viewer-count {
  font-size: 22rpx;
  color: #888;
  flex-shrink: 0;
}

.video-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.video-item {
  width: calc(50% - 8rpx);
}

.video-cover {
  position: relative;
  width: 100%;
  padding-top: 130%;
  border-radius: 12rpx;
  overflow: hidden;
  margin-bottom: 8rpx;
}

.video-stats {
  position: absolute;
  bottom: 8rpx;
  right: 12rpx;
  display: flex;
  align-items: center;
  gap: 4rpx;
}

.stat-icon {
  font-size: 20rpx;
}

.stat-text {
  font-size: 20rpx;
  color: #fff;
  text-shadow: 0 1rpx 2rpx rgba(0, 0, 0, 0.8);
}

.video-title {
  font-size: 24rpx;
  color: #fff;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 0;
  gap: 16rpx;
}

.empty-icon {
  font-size: 80rpx;
  opacity: 0.5;
}

.empty-text {
  font-size: 28rpx;
  color: #666;
}

.bottom-space {
  height: 60rpx;
}
</style>
