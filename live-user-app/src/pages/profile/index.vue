<template>
  <view class="profile-page">
    <view class="header-bg"></view>
    <scroll-view class="content-scroll" scroll-y>
      <view class="profile-header">
        <view class="header-top">
          <view class="settings-btn" @tap="handleSettings">
            <text class="settings-icon">⚙️</text>
          </view>
        </view>
        <view class="user-info">
          <view class="avatar-wrapper" @tap="previewAvatar">
            <UserAvatar :name="userInfo?.nickname || '用户'" size="xl" />
            <view class="avatar-ring"></view>
          </view>
          <view class="user-detail">
            <view class="name-row">
              <text class="nickname">{{ userInfo?.nickname || '未登录' }}</text>
              <view class="level-badge">
                <text class="level-text">Lv.{{ userInfo?.level || 1 }}</text>
              </view>
            </view>
            <text class="user-id">ID: {{ userInfo?.id || '---' }}</text>
            <text class="bio">{{ userInfo?.bio || '这个人很懒，什么都没写~' }}</text>
          </view>
          <view class="edit-btn" @tap="goEdit">
            <text class="edit-icon">✏️</text>
            <text class="edit-text">编辑</text>
          </view>
        </view>

        <view class="level-section">
          <view class="level-info">
            <text class="level-label">等级 Lv.{{ userInfo?.level || 1 }}</text>
            <text class="level-exp">{{ currentExp }}/{{ nextLevelExp }} 经验</text>
          </view>
          <view class="level-progress-bar">
            <view class="level-progress-fill" :style="{ width: `${levelProgress}%` }"></view>
          </view>
          <text class="level-tip">距离 Lv.{{ (userInfo?.level || 1) + 1 }} 还差 {{ nextLevelExp - currentExp }} 经验</text>
        </view>

        <view class="stats-row">
          <view class="stat-item">
            <text class="stat-num">{{ formatCount(userInfo?.following || 0) }}</text>
            <text class="stat-label">关注</text>
          </view>
          <view class="stat-divider"></view>
          <view class="stat-item">
            <text class="stat-num">{{ formatCount(userInfo?.followers || 0) }}</text>
            <text class="stat-label">粉丝</text>
          </view>
          <view class="stat-divider"></view>
          <view class="stat-item">
            <text class="stat-num">{{ formatCount(userInfo?.likes || 0) }}</text>
            <text class="stat-label">获赞</text>
          </view>
          <view class="stat-divider"></view>
          <view class="stat-item">
            <text class="stat-num">{{ formatCount(currentExp) }}</text>
            <text class="stat-label">经验</text>
          </view>
        </view>
      </view>

      <view class="menu-section">
        <view class="menu-item" @tap="goWallet">
          <view class="menu-left">
            <text class="menu-icon">💰</text>
            <text class="menu-text">我的钱包</text>
          </view>
          <view class="menu-right">
            <text class="menu-value">{{ userStore.coins }} 金币</text>
            <text class="arrow">›</text>
          </view>
        </view>
        <view class="menu-item" @tap="handleMyVideos">
          <view class="menu-left">
            <text class="menu-icon">🎬</text>
            <text class="menu-text">我的视频</text>
          </view>
          <view class="menu-right">
            <text class="arrow">›</text>
          </view>
        </view>
        <view class="menu-item" @tap="handleFollow">
          <view class="menu-left">
            <text class="menu-icon">❤️</text>
            <text class="menu-text">我的关注</text>
          </view>
          <view class="menu-right">
            <text class="arrow">›</text>
          </view>
        </view>
        <view class="menu-item" @tap="handleFans">
          <view class="menu-left">
            <text class="menu-icon">👥</text>
            <text class="menu-text">我的粉丝</text>
          </view>
          <view class="menu-right">
            <text class="arrow">›</text>
          </view>
        </view>
        <view class="menu-item" @tap="goAnchor">
          <view class="menu-left">
            <text class="menu-icon">🎤</text>
            <text class="menu-text">主播中心</text>
          </view>
          <view class="menu-right">
            <text class="arrow">›</text>
          </view>
        </view>
        <view class="menu-item" @tap="handleSettings">
          <view class="menu-left">
            <text class="menu-icon">⚙️</text>
            <text class="menu-text">设置</text>
          </view>
          <view class="menu-right">
            <text class="arrow">›</text>
          </view>
        </view>
      </view>

      <view class="my-videos-section">
        <view class="section-header">
          <text class="section-title">我的视频</text>
          <text class="more-text">查看全部 ›</text>
        </view>
        <view class="video-grid">
          <view
            class="video-item"
            v-for="video in myVideos"
            :key="video.id"
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
      </view>

      <view class="logout-section">
        <view class="logout-btn" @tap="handleLogout">
          <text class="logout-text">退出登录</text>
        </view>
      </view>

      <view class="bottom-space"></view>
    </scroll-view>

    <view v-if="showAvatarPreview" class="avatar-preview-overlay" @tap="closeAvatarPreview">
      <view class="avatar-preview-content" @tap.stop>
        <view class="preview-avatar" :style="{ background: avatarGradient }">
          <text class="preview-avatar-text">{{ avatarInitial }}</text>
        </view>
        <view class="preview-info">
          <text class="preview-name">{{ userInfo?.nickname || '用户' }}</text>
          <text class="preview-id">ID: {{ userInfo?.id || '---' }}</text>
        </view>
        <view class="preview-close" @tap="closeAvatarPreview">
          <text class="close-icon">✕</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import UserAvatar from '@/components/UserAvatar.vue'
import { useUserStore } from '@/store/user'
import { shortVideos } from '@/mock/data'
import { formatCount, getGradientByName } from '@/utils/format'

const userStore = useUserStore()
const myVideos = ref(shortVideos.slice(0, 4))
const showAvatarPreview = ref(false)

const userInfo = computed(() => userStore.userInfo)

const currentExp = computed(() => {
  const level = userInfo.value?.level || 1
  return Math.floor(level * 150 + Math.random() * 100)
})

const nextLevelExp = computed(() => {
  const level = userInfo.value?.level || 1
  return (level + 1) * 200
})

const levelProgress = computed(() => {
  return Math.min(100, (currentExp.value / nextLevelExp.value) * 100)
})

const avatarGradient = computed(() => {
  return getGradientByName(userInfo.value?.nickname || '用户')
})

const avatarInitial = computed(() => {
  return (userInfo.value?.nickname || '?').charAt(0)
})

onShow(() => {
  if (!userStore.isLoggedIn) {
    uni.navigateTo({ url: '/pages/auth/login' })
  }
})

function previewAvatar() {
  showAvatarPreview.value = true
}

function closeAvatarPreview() {
  showAvatarPreview.value = false
}

function goEdit() {
  uni.navigateTo({ url: '/pages/profile/edit' })
}

function goWallet() {
  uni.navigateTo({ url: '/pages/profile/wallet' })
}

function goAnchor() {
  uni.switchTab({ url: '/pages/anchor/index' })
}

function handleMyVideos() {
  uni.showToast({ title: '开发中', icon: 'none' })
}

function handleFollow() {
  uni.showToast({ title: '开发中', icon: 'none' })
}

function handleFans() {
  uni.showToast({ title: '开发中', icon: 'none' })
}

function handleSettings() {
  uni.showToast({ title: '设置开发中', icon: 'none' })
}

function handleLogout() {
  uni.showModal({
    title: '提示',
    content: '确定要退出登录吗？',
    success: (res) => {
      if (res.confirm) {
        userStore.logout()
        uni.showToast({ title: '已退出', icon: 'success' })
        setTimeout(() => {
          uni.navigateTo({ url: '/pages/auth/login' })
        }, 500)
      }
    }
  })
}
</script>

<style lang="scss" scoped>
.profile-page {
  min-height: 100vh;
  background: #0a0a0f;
  position: relative;
}

.header-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 400rpx;
  background: linear-gradient(135deg, rgba(255, 45, 85, 0.3) 0%, rgba(175, 82, 237, 0.3) 100%);
}

.content-scroll {
  position: relative;
  z-index: 2;
  height: 100vh;
}

.profile-header {
  padding: 80rpx 32rpx 32rpx;
}

.header-top {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 24rpx;
}

.settings-btn {
  width: 64rpx;
  height: 64rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 88rpx;
  min-height: 88rpx;
}

.settings-icon {
  font-size: 40rpx;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 24rpx;
  margin-bottom: 24rpx;
}

.avatar-wrapper {
  position: relative;
  flex-shrink: 0;
}

.avatar-ring {
  position: absolute;
  inset: -6rpx;
  border-radius: 50%;
  border: 4rpx solid transparent;
  background: linear-gradient(135deg, #FF2D55 0%, #AF52ED 100%) border-box;
  -webkit-mask: linear-gradient(#fff 0 0) padding-box, linear-gradient(#fff 0 0);
  mask: linear-gradient(#fff 0 0) padding-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  pointer-events: none;
}

.user-detail {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6rpx;
  min-width: 0;
}

.name-row {
  display: flex;
  align-items: center;
  gap: 12rpx;
  flex-wrap: wrap;
}

.nickname {
  font-size: 36rpx;
  font-weight: bold;
  color: #fff;
}

.level-badge {
  background: linear-gradient(135deg, #FFD700 0%, #FFA500 100%);
  padding: 4rpx 12rpx;
  border-radius: 8rpx;
  flex-shrink: 0;
}

.level-text {
  font-size: 20rpx;
  color: #fff;
  font-weight: bold;
}

.user-id {
  font-size: 24rpx;
  color: #888;
}

.bio {
  font-size: 26rpx;
  color: #a0a0b0;
  margin-top: 4rpx;
  line-height: 1.4;
}

.edit-btn {
  display: flex;
  align-items: center;
  gap: 6rpx;
  padding: 12rpx 20rpx;
  background: rgba(255, 255, 255, 0.1);
  border: 1rpx solid rgba(255, 255, 255, 0.2);
  border-radius: 24rpx;
  flex-shrink: 0;
  min-width: 88rpx;
  min-height: 44rpx;
  justify-content: center;
}

.edit-icon {
  font-size: 22rpx;
}

.edit-text {
  font-size: 24rpx;
  color: #fff;
}

.level-section {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 16rpx;
  padding: 20rpx 24rpx;
  margin-bottom: 24rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.08);
}

.level-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12rpx;
}

.level-label {
  font-size: 26rpx;
  font-weight: bold;
  color: #FFD700;
}

.level-exp {
  font-size: 22rpx;
  color: #888;
}

.level-progress-bar {
  height: 12rpx;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 6rpx;
  overflow: hidden;
  margin-bottom: 8rpx;
}

.level-progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #FFD700 0%, #FFA500 100%);
  border-radius: 6rpx;
  transition: width 0.5s ease;
}

.level-tip {
  font-size: 20rpx;
  color: #666;
}

.stats-row {
  display: flex;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 16rpx;
  padding: 24rpx 0;
  backdrop-filter: blur(10px);
  border: 1rpx solid rgba(255, 255, 255, 0.08);
}

.stat-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
}

.stat-num {
  font-size: 32rpx;
  font-weight: bold;
  color: #fff;
}

.stat-label {
  font-size: 24rpx;
  color: #888;
}

.stat-divider {
  width: 1rpx;
  height: 48rpx;
  background: rgba(255, 255, 255, 0.1);
  align-self: center;
}

.menu-section {
  margin: 24rpx 32rpx;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 16rpx;
  overflow: hidden;
  border: 1rpx solid rgba(255, 255, 255, 0.08);
}

.menu-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 28rpx 24rpx;
  border-bottom: 1rpx solid rgba(255, 255, 255, 0.05);
  min-height: 88rpx;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-left {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.menu-icon {
  font-size: 36rpx;
}

.menu-text {
  font-size: 30rpx;
  color: #fff;
}

.menu-right {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.menu-value {
  font-size: 26rpx;
  color: #FFD700;
}

.arrow {
  font-size: 32rpx;
  color: #666;
}

.my-videos-section {
  margin: 0 32rpx;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #fff;
}

.more-text {
  font-size: 26rpx;
  color: #888;
}

.video-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
}

.video-item {
  width: calc(50% - 6rpx);
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

.logout-section {
  margin: 48rpx 32rpx 0;
}

.logout-btn {
  height: 88rpx;
  background: rgba(255, 45, 85, 0.15);
  border: 1rpx solid rgba(255, 45, 85, 0.3);
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logout-text {
  font-size: 30rpx;
  color: #FF2D55;
}

.bottom-space {
  height: 120rpx;
}

.avatar-preview-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.9);
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.avatar-preview-content {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 32rpx;
  animation: scaleIn 0.3s ease;
}

@keyframes scaleIn {
  from {
    transform: scale(0.8);
    opacity: 0;
  }
  to {
    transform: scale(1);
    opacity: 1;
  }
}

.preview-avatar {
  width: 300rpx;
  height: 300rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 0 60rpx rgba(255, 45, 85, 0.5);
}

.preview-avatar-text {
  font-size: 120rpx;
  font-weight: bold;
  color: #fff;
  text-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.3);
}

.preview-info {
  text-align: center;
}

.preview-name {
  display: block;
  font-size: 36rpx;
  font-weight: bold;
  color: #fff;
  margin-bottom: 8rpx;
}

.preview-id {
  display: block;
  font-size: 26rpx;
  color: #888;
}

.preview-close {
  position: absolute;
  top: -20rpx;
  right: -20rpx;
  width: 64rpx;
  height: 64rpx;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 88rpx;
  min-height: 88rpx;
}

.close-icon {
  font-size: 32rpx;
  color: #fff;
}
</style>
