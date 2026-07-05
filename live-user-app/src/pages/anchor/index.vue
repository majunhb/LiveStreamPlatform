<template>
  <view class="anchor-page">
    <view class="header">
      <text class="header-title">主播中心</text>
    </view>

    <scroll-view class="content-scroll" scroll-y>
      <view class="stats-section">
        <view class="stat-card">
          <text class="stat-icon">👁️</text>
          <text class="stat-num">{{ formatCount(stats.totalViewers) }}</text>
          <text class="stat-label">总观看</text>
        </view>
        <view class="stat-card">
          <text class="stat-icon">⏱️</text>
          <text class="stat-num">{{ stats.totalDuration }}</text>
          <text class="stat-label">直播时长(h)</text>
        </view>
        <view class="stat-card">
          <text class="stat-icon">🎁</text>
          <text class="stat-num">{{ formatCount(stats.totalGifts) }}</text>
          <text class="stat-label">收到礼物</text>
        </view>
        <view class="stat-card">
          <text class="stat-icon">❤️</text>
          <text class="stat-num">{{ formatCount(stats.totalLikes) }}</text>
          <text class="stat-label">获赞</text>
        </view>
      </view>

      <view class="go-live-section">
        <view class="live-preview">
          <view class="preview-cover" :style="{ background: coverGradient }">
            <view class="preview-overlay">
              <LiveBadge />
              <text class="preview-title">{{ liveTitle || '点击设置直播间标题' }}</text>
            </view>
          </view>
        </view>

        <view class="live-setup">
          <view class="setup-item">
            <text class="setup-label">标题</text>
            <input
              class="setup-input"
              v-model="liveTitle"
              placeholder="请输入直播间标题"
              placeholder-class="input-placeholder"
              maxlength="30"
            />
          </view>
          <view class="setup-item">
            <text class="setup-label">分类</text>
            <view class="category-selector" @tap="showCategoryPicker = true">
              <text class="category-text">{{ selectedCategory }}</text>
              <text class="category-arrow">›</text>
            </view>
          </view>
        </view>

        <view class="start-btn" @tap="handleStartLive">
          <text class="start-icon">🎬</text>
          <text class="start-text">开始直播</text>
        </view>
      </view>

      <view class="history-section">
        <view class="section-header">
          <text class="section-title">直播记录</text>
          <text class="more-text">查看全部 ›</text>
        </view>
        <view class="history-list">
          <view
            v-for="item in liveHistory"
            :key="item.id"
            class="history-item"
          >
            <view class="history-cover" :style="{ background: coverGradient }">
              <text class="history-duration">{{ item.duration }}</text>
            </view>
            <view class="history-info">
              <text class="history-title ellipsis-1">{{ item.title }}</text>
              <view class="history-meta">
                <text class="meta-item">👁️ {{ formatCount(item.viewers) }}</text>
                <text class="meta-item">🎁 {{ item.gifts }}</text>
              </view>
              <text class="history-date">{{ item.date }}</text>
            </view>
          </view>
        </view>
      </view>

      <view class="bottom-space"></view>
    </scroll-view>

    <view v-if="showCategoryPicker" class="picker-mask" @tap="showCategoryPicker = false">
      <view class="picker-content" @tap.stop>
        <view class="picker-header">
          <text class="picker-title">选择分类</text>
          <view class="picker-close" @tap="showCategoryPicker = false">
            <text class="close-icon">✕</text>
          </view>
        </view>
        <scroll-view class="picker-list" scroll-y>
          <view
            v-for="cat in categories"
            :key="cat"
            class="picker-item"
            :class="{ active: selectedCategory === cat }"
            @tap="selectCategory(cat)"
          >
            <text class="picker-text">{{ cat }}</text>
            <text v-if="selectedCategory === cat" class="picker-check">✓</text>
          </view>
        </scroll-view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import LiveBadge from '@/components/LiveBadge.vue'
import { categories } from '@/mock/data'
import { formatCount, getGradientByName } from '@/utils/format'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const liveTitle = ref('新人主播，多多关照~')
const selectedCategory = ref('聊天')
const showCategoryPicker = ref(false)
const coverGradient = ref(getGradientByName('anchor'))

const stats = reactive({
  totalViewers: 56780,
  totalDuration: 128,
  totalGifts: 2580,
  totalLikes: 98560
})

const liveHistory = ref([
  { id: '1', title: '深夜聊天', duration: '2:30:00', viewers: 6780, gifts: 128, date: '2024-01-15' },
  { id: '2', title: '唱歌直播', duration: '1:45:00', viewers: 12500, gifts: 356, date: '2024-01-14' },
  { id: '3', title: '游戏互动', duration: '3:10:00', viewers: 8900, gifts: 245, date: '2024-01-13' }
])

onShow(() => {
  if (!userStore.isLoggedIn) {
    uni.navigateTo({ url: '/pages/auth/login' })
    return
  }
})

function selectCategory(cat: string) {
  selectedCategory.value = cat
  coverGradient.value = getGradientByName(cat)
  showCategoryPicker.value = false
}

function handleStartLive() {
  if (!liveTitle.value.trim()) {
    uni.showToast({ title: '请输入直播间标题', icon: 'none' })
    return
  }

  uni.showModal({
    title: '开始直播',
    content: `确定开始直播吗？\n标题：${liveTitle.value}\n分类：${selectedCategory.value}`,
    success: (res) => {
      if (res.confirm) {
        uni.showLoading({ title: '正在开播...' })
        setTimeout(() => {
          uni.hideLoading()
          uni.showToast({ title: '开播成功', icon: 'success' })
          setTimeout(() => {
            uni.navigateTo({
              url: `/pages/live/room?roomId=1&anchorMode=true`
            })
          }, 500)
        }, 800)
      }
    }
  })
}
</script>

<style lang="scss" scoped>
.anchor-page {
  min-height: 100vh;
  background: #0a0a0f;
  display: flex;
  flex-direction: column;
}

.header {
  padding: 80rpx 32rpx 24rpx;
  text-align: center;
}

.header-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #fff;
}

.content-scroll {
  flex: 1;
}

.stats-section {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  padding: 0 32rpx;
  margin-bottom: 32rpx;
}

.stat-card {
  width: calc(50% - 8rpx);
  background: rgba(255, 255, 255, 0.05);
  border-radius: 16rpx;
  padding: 24rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.08);
}

.stat-icon {
  font-size: 40rpx;
}

.stat-num {
  font-size: 36rpx;
  font-weight: bold;
  color: #fff;
}

.stat-label {
  font-size: 22rpx;
  color: #888;
}

.go-live-section {
  margin: 0 32rpx 32rpx;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 20rpx;
  padding: 24rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.08);
}

.live-preview {
  margin-bottom: 24rpx;
}

.preview-cover {
  position: relative;
  width: 100%;
  height: 400rpx;
  border-radius: 16rpx;
  overflow: hidden;
}

.preview-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 24rpx;
  background: linear-gradient(transparent, rgba(0,0,0,0.7));
  display: flex;
  flex-direction: column;
  gap: 12rpx;
}

.preview-title {
  font-size: 28rpx;
  color: #fff;
  font-weight: 500;
}

.live-setup {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
  margin-bottom: 24rpx;
}

.setup-item {
  display: flex;
  align-items: center;
  gap: 20rpx;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12rpx;
  padding: 0 20rpx;
  height: 80rpx;
}

.setup-label {
  font-size: 28rpx;
  color: #888;
  flex-shrink: 0;
  width: 80rpx;
}

.setup-input {
  flex: 1;
  height: 80rpx;
  font-size: 28rpx;
  color: #fff;
}

.input-placeholder {
  color: #555;
}

.category-selector {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.category-text {
  font-size: 28rpx;
  color: #fff;
}

.category-arrow {
  font-size: 32rpx;
  color: #666;
}

.start-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  height: 96rpx;
  background: linear-gradient(135deg, #FF2D55 0%, #AF52ED 100%);
  border-radius: 48rpx;
  box-shadow: 0 8rpx 32rpx rgba(255, 45, 85, 0.4);
}

.start-icon {
  font-size: 36rpx;
}

.start-text {
  font-size: 32rpx;
  color: #fff;
  font-weight: bold;
}

.history-section {
  margin: 0 32rpx;
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

.more-text {
  font-size: 24rpx;
  color: #888;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.history-item {
  display: flex;
  gap: 20rpx;
  padding: 16rpx;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12rpx;
}

.history-cover {
  width: 160rpx;
  height: 120rpx;
  border-radius: 8rpx;
  flex-shrink: 0;
  position: relative;
  display: flex;
  align-items: flex-end;
  justify-content: center;
  padding-bottom: 8rpx;
  box-sizing: border-box;
}

.history-duration {
  font-size: 20rpx;
  color: #fff;
  background: rgba(0, 0, 0, 0.5);
  padding: 2rpx 8rpx;
  border-radius: 4rpx;
}

.history-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 4rpx 0;
}

.history-title {
  font-size: 26rpx;
  color: #fff;
  font-weight: 500;
}

.history-meta {
  display: flex;
  gap: 16rpx;
}

.meta-item {
  font-size: 22rpx;
  color: #888;
}

.history-date {
  font-size: 22rpx;
  color: #666;
}

.bottom-space {
  height: 120rpx;
}

.picker-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  z-index: 1000;
  display: flex;
  align-items: flex-end;
}

.picker-content {
  width: 100%;
  max-height: 70vh;
  background: #1a1a25;
  border-radius: 32rpx 32rpx 0 0;
  display: flex;
  flex-direction: column;
}

.picker-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 32rpx;
  border-bottom: 1rpx solid rgba(255, 255, 255, 0.08);
}

.picker-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #fff;
}

.picker-close {
  width: 48rpx;
  height: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-icon {
  font-size: 28rpx;
  color: #888;
}

.picker-list {
  flex: 1;
  padding: 16rpx 0;
}

.picker-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 28rpx 32rpx;
}

.picker-item.active .picker-text {
  color: #FF2D55;
}

.picker-text {
  font-size: 30rpx;
  color: #fff;
}

.picker-check {
  font-size: 28rpx;
  color: #FF2D55;
  font-weight: bold;
}
</style>
