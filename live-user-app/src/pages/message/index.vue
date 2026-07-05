<template>
  <view class="message-page">
    <view class="header">
      <text class="header-title">消息</text>
    </view>

    <view class="tab-row">
      <view
        v-for="tab in tabs"
        :key="tab.key"
        class="tab-item"
        :class="{ active: activeTab === tab.key }"
        @tap="activeTab = tab.key"
      >
        <text class="tab-text">{{ tab.name }}</text>
        <view v-if="getUnreadCount(tab.key) > 0" class="unread-badge">
          <text class="unread-text">{{ getUnreadCount(tab.key) > 99 ? '99+' : getUnreadCount(tab.key) }}</text>
        </view>
      </view>
    </view>

    <scroll-view class="message-list" scroll-y>
      <view
        v-for="msg in filteredMessages"
        :key="msg.id"
        class="message-item"
        @tap="handleMessageTap(msg)"
      >
        <view class="avatar-wrapper">
          <UserAvatar :name="msg.from.nickname" size="lg" />
          <view v-if="msg.unread > 0" class="unread-dot"></view>
        </view>
        <view class="message-content">
          <view class="message-top">
            <text class="sender-name">{{ msg.from.nickname }}</text>
            <text class="message-time">{{ msg.time }}</text>
          </view>
          <view class="message-bottom">
            <text class="message-text ellipsis-1">{{ msg.content }}</text>
            <view v-if="msg.unread > 0" class="unread-count">
              <text class="count-text">{{ msg.unread }}</text>
            </view>
          </view>
        </view>
      </view>

      <view v-if="filteredMessages.length === 0" class="empty-state">
        <text class="empty-icon">💬</text>
        <text class="empty-text">暂无消息</text>
      </view>

      <view class="bottom-space"></view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import UserAvatar from '@/components/UserAvatar.vue'
import { messages } from '@/mock/data'
import type { MessageItem } from '@/mock/data'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const messageList = ref<MessageItem[]>([...messages])
const activeTab = ref('private')

const tabs = [
  { key: 'private', name: '私信' },
  { key: 'system', name: '系统通知' },
  { key: 'gift', name: '礼物通知' }
]

const filteredMessages = computed(() => {
  return messageList.value.filter(m => m.type === activeTab.value)
})

function getUnreadCount(type: string): number {
  return messageList.value
    .filter(m => m.type === type)
    .reduce((sum, m) => sum + m.unread, 0)
}

onShow(() => {
  if (!userStore.isLoggedIn) {
    uni.navigateTo({ url: '/pages/auth/login' })
  }
})

function handleMessageTap(msg: MessageItem) {
  msg.unread = 0
  uni.showToast({ title: '消息详情开发中', icon: 'none' })
}
</script>

<style lang="scss" scoped>
.message-page {
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

.tab-row {
  display: flex;
  padding: 0 32rpx;
  border-bottom: 1rpx solid rgba(255, 255, 255, 0.08);
}

.tab-item {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
  padding: 24rpx 0;
  position: relative;
}

.tab-item.active .tab-text {
  color: #fff;
  font-weight: bold;
  font-size: 32rpx;
}

.tab-text {
  font-size: 28rpx;
  color: #888;
  transition: all 0.2s ease;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 48rpx;
  height: 4rpx;
  background: linear-gradient(135deg, #FF2D55 0%, #AF52ED 100%);
  border-radius: 2rpx;
}

.unread-badge {
  min-width: 32rpx;
  height: 32rpx;
  padding: 0 8rpx;
  background: #FF2D55;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.unread-text {
  font-size: 20rpx;
  color: #fff;
  font-weight: bold;
}

.message-list {
  flex: 1;
}

.message-item {
  display: flex;
  gap: 20rpx;
  padding: 24rpx 32rpx;
  border-bottom: 1rpx solid rgba(255, 255, 255, 0.05);
}

.avatar-wrapper {
  position: relative;
  flex-shrink: 0;
}

.unread-dot {
  position: absolute;
  top: -4rpx;
  right: -4rpx;
  width: 16rpx;
  height: 16rpx;
  background: #FF2D55;
  border-radius: 50%;
  border: 2rpx solid #0a0a0f;
}

.message-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 8rpx;
  min-width: 0;
}

.message-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
}

.sender-name {
  font-size: 30rpx;
  font-weight: 500;
  color: #fff;
  flex-shrink: 0;
}

.message-time {
  font-size: 22rpx;
  color: #666;
  flex-shrink: 0;
}

.message-bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
}

.message-text {
  font-size: 26rpx;
  color: #888;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.unread-count {
  min-width: 36rpx;
  height: 36rpx;
  padding: 0 10rpx;
  background: #FF2D55;
  border-radius: 18rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.count-text {
  font-size: 22rpx;
  color: #fff;
  font-weight: bold;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 150rpx 0;
  gap: 20rpx;
}

.empty-icon {
  font-size: 100rpx;
  opacity: 0.3;
}

.empty-text {
  font-size: 28rpx;
  color: #555;
}

.bottom-space {
  height: 120rpx;
}
</style>
