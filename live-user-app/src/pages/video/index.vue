<template>
  <view class="video-page">
    <swiper
      class="video-swiper"
      :vertical="true"
      :circular="true"
      @change="onSwiperChange"
      :duration="300"
    >
      <swiper-item v-for="(video, index) in videoList" :key="video.id">
        <view class="video-wrapper" :class="{ 'scale-in': currentIndex === index && isSwiping }">
          <VideoCard
            :video="video"
            @like="handleLike"
            @comment="openComments"
            @share="handleShare"
            @author="handleAuthor"
          />
        </view>
      </swiper-item>
    </swiper>

    <view v-if="showComments" class="comment-panel-overlay" @tap="closeComments">
      <view class="comment-panel" :class="{ show: showComments }" @tap.stop>
        <view class="comment-header">
          <text class="comment-title">{{ videoComments.length }} 条评论</text>
          <view class="close-btn" @tap="closeComments">
            <text class="close-icon">✕</text>
          </view>
        </view>
        
        <scroll-view class="comment-list" scroll-y>
          <view
            v-for="comment in videoComments"
            :key="comment.id"
            class="comment-item"
          >
            <UserAvatar :name="comment.user.nickname" size="sm" />
            <view class="comment-content">
              <view class="comment-top">
                <text class="comment-name">{{ comment.user.nickname }}</text>
                <text class="comment-time">{{ comment.time }}</text>
              </view>
              <text class="comment-text">{{ comment.content }}</text>
              <view class="comment-actions">
                <view class="comment-action" @tap="toggleCommentLike(comment)">
                  <text class="action-icon">{{ comment.isLiked ? '❤️' : '🤍' }}</text>
                  <text class="action-num">{{ formatCount(comment.likes) }}</text>
                </view>
                <view class="comment-action" @tap="replyComment(comment)">
                  <text class="action-icon">💬</text>
                  <text class="action-num">{{ comment.replyCount }}</text>
                </view>
              </view>
            </view>
          </view>
          <view class="comment-bottom-space"></view>
        </scroll-view>
        
        <view class="comment-input-bar">
          <view class="comment-input">
            <text class="input-placeholder">说点什么...</text>
          </view>
          <view class="send-btn">
            <text class="send-icon">📤</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import VideoCard from '@/components/VideoCard.vue'
import UserAvatar from '@/components/UserAvatar.vue'
import { shortVideos, videoComments as mockVideoComments } from '@/mock/data'
import type { VideoComment } from '@/mock/data'
import { formatCount } from '@/utils/format'

const videoList = ref([...shortVideos])
const currentIndex = ref(0)
const isSwiping = ref(false)
const showComments = ref(false)
const videoComments = ref<VideoComment[]>([...mockVideoComments])

function onSwiperChange(e: any) {
  isSwiping.value = true
  currentIndex.value = e.detail.current
  setTimeout(() => {
    isSwiping.value = false
  }, 300)
}

function handleLike(videoId: string) {
  const video = videoList.value.find(v => v.id === videoId)
  if (video) {
    video.isLiked = !video.isLiked
    video.likes += video.isLiked ? 1 : -1
  }
}

function openComments(videoId: string) {
  showComments.value = true
}

function closeComments() {
  showComments.value = false
}

function handleShare(videoId: string) {
  uni.showToast({ title: '分享功能开发中', icon: 'none' })
}

function handleAuthor(authorId: string) {
  uni.showToast({ title: '用户主页开发中', icon: 'none' })
}

function toggleCommentLike(comment: VideoComment) {
  comment.isLiked = !comment.isLiked
  comment.likes += comment.isLiked ? 1 : -1
}

function replyComment(comment: VideoComment) {
  uni.showToast({ title: '回复功能开发中', icon: 'none' })
}
</script>

<style lang="scss" scoped>
.video-page {
  width: 100%;
  height: 100vh;
  background: #0a0a0f;
  overflow: hidden;
}

.video-swiper {
  width: 100%;
  height: 100%;
}

.video-wrapper {
  width: 100%;
  height: 100%;
  transition: transform 0.3s ease;
}

.video-wrapper.scale-in {
  animation: scaleIn 0.3s ease;
}

@keyframes scaleIn {
  from {
    transform: scale(0.95);
    opacity: 0.8;
  }
  to {
    transform: scale(1);
    opacity: 1;
  }
}

.comment-panel-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  z-index: 1000;
  display: flex;
  align-items: flex-end;
  animation: fadeIn 0.2s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.comment-panel {
  width: 100%;
  height: 60vh;
  background: #1a1a25;
  border-radius: 32rpx 32rpx 0 0;
  display: flex;
  flex-direction: column;
  animation: slideUp 0.3s ease;
  padding-bottom: env(safe-area-inset-bottom);
}

@keyframes slideUp {
  from { transform: translateY(100%); }
  to { transform: translateY(0); }
}

.comment-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 32rpx;
  border-bottom: 1rpx solid rgba(255, 255, 255, 0.08);
}

.comment-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #fff;
}

.close-btn {
  width: 48rpx;
  height: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 64rpx;
  min-height: 64rpx;
}

.close-icon {
  font-size: 28rpx;
  color: #888;
}

.comment-list {
  flex: 1;
  padding: 0 32rpx;
}

.comment-item {
  display: flex;
  gap: 20rpx;
  padding: 24rpx 0;
  border-bottom: 1rpx solid rgba(255, 255, 255, 0.05);
}

.comment-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.comment-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.comment-name {
  font-size: 24rpx;
  color: #a0a0b0;
  font-weight: 500;
}

.comment-time {
  font-size: 20rpx;
  color: #666;
}

.comment-text {
  font-size: 26rpx;
  color: #fff;
  line-height: 1.5;
}

.comment-actions {
  display: flex;
  gap: 32rpx;
  margin-top: 4rpx;
}

.comment-action {
  display: flex;
  align-items: center;
  gap: 6rpx;
  min-width: 64rpx;
  min-height: 44rpx;
}

.action-icon {
  font-size: 24rpx;
}

.action-num {
  font-size: 22rpx;
  color: #888;
}

.comment-bottom-space {
  height: 32rpx;
}

.comment-input-bar {
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 16rpx 32rpx;
  border-top: 1rpx solid rgba(255, 255, 255, 0.08);
  background: #12121a;
}

.comment-input {
  flex: 1;
  height: 72rpx;
  background: rgba(255, 255, 255, 0.08);
  border-radius: 36rpx;
  display: flex;
  align-items: center;
  padding: 0 24rpx;
}

.input-placeholder {
  font-size: 26rpx;
  color: #666;
}

.send-btn {
  width: 72rpx;
  height: 72rpx;
  background: linear-gradient(135deg, #FF2D55 0%, #AF52ED 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  min-width: 88rpx;
  min-height: 88rpx;
}

.send-icon {
  font-size: 32rpx;
}
</style>
