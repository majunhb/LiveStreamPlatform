<template>
  <view class="video-card" :style="{ background: video.cover }" @tap="handleSingleTap" @longpress="handleLongPress">
    <view class="video-overlay"></view>
    
    <view
      v-for="heart in doubleTapHearts"
      :key="heart.id"
      class="double-tap-heart"
      :style="{
        left: `${heart.x}rpx`,
        top: `${heart.y}rpx`,
        animationDuration: `${heart.duration}s`
      }"
    >
      <text class="heart-emoji">❤️</text>
    </view>
    
    <view class="video-content">
      <view class="bottom-info">
        <view class="author-row">
          <UserAvatar :name="video.author.nickname" size="sm" />
          <text class="author-name">@{{ video.author.nickname }}</text>
          <view class="follow-btn">
            <text class="follow-text">+ 关注</text>
          </view>
        </view>
        <text class="video-title">{{ video.title }}</text>
        <view class="music-row">
          <text class="music-icon">🎵</text>
          <text class="music-name ellipsis-1">{{ video.music }}</text>
        </view>
      </view>
      <view class="right-actions">
        <view class="action-item" @tap.stop="handleLike">
          <text class="action-icon">{{ video.isLiked ? '❤️' : '🤍' }}</text>
          <text class="action-count">{{ formatCount(video.likes) }}</text>
        </view>
        <view class="action-item" @tap.stop="handleComment">
          <text class="action-icon">💬</text>
          <text class="action-count">{{ formatCount(video.comments) }}</text>
        </view>
        <view class="action-item" @tap.stop="handleShare">
          <text class="action-icon">↗️</text>
          <text class="action-count">{{ formatCount(video.shares) }}</text>
        </view>
        <view class="action-avatar vinyl-record" :class="{ paused: !isPlaying }" @tap.stop="handleAuthor">
          <UserAvatar :name="video.author.nickname" size="md" />
          <view class="plus-badge">
            <text class="plus-text">+</text>
          </view>
        </view>
      </view>
    </view>
    
    <view class="progress-bar">
      <view class="progress-fill" :style="{ width: `${progress}%` }"></view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import type { ShortVideo } from '@/mock/data'
import UserAvatar from './UserAvatar.vue'
import { formatCount } from '@/utils/format'

const props = defineProps<{
  video: ShortVideo
}>()

const emit = defineEmits(['like', 'comment', 'share', 'author'])

const isPlaying = ref(true)
const progress = ref(0)
const doubleTapHearts = ref<{ id: number; x: number; y: number; duration: number }[]>([])
let heartIdCounter = 0
let lastTapTime = 0
let progressTimer: ReturnType<typeof setInterval> | null = null

onMounted(() => {
  startProgress()
})

onUnmounted(() => {
  if (progressTimer) clearInterval(progressTimer)
})

function startProgress() {
  progressTimer = setInterval(() => {
    if (isPlaying.value) {
      progress.value += 0.5
      if (progress.value >= 100) {
        progress.value = 0
      }
    }
  }, 200)
}

function handleSingleTap(e: any) {
  const now = Date.now()
  const timeDiff = now - lastTapTime
  
  if (timeDiff < 300) {
    handleDoubleTap(e)
    lastTapTime = 0
  } else {
    lastTapTime = now
    setTimeout(() => {
      if (lastTapTime !== 0 && Date.now() - lastTapTime >= 300) {
        isPlaying.value = !isPlaying.value
        lastTapTime = 0
      }
    }, 300)
  }
}

function handleDoubleTap(e: any) {
  const x = (e?.detail?.x || e?.touches?.[0]?.clientX || 200) * 2
  const y = (e?.detail?.y || e?.touches?.[0]?.clientY || 300) * 2
  
  const id = heartIdCounter++
  doubleTapHearts.value.push({
    id,
    x: x - 50,
    y: y - 50,
    duration: 0.8 + Math.random() * 0.4
  })
  
  setTimeout(() => {
    const index = doubleTapHearts.value.findIndex(h => h.id === id)
    if (index > -1) {
      doubleTapHearts.value.splice(index, 1)
    }
  }, 1200)
  
  if (!props.video.isLiked) {
    emit('like', props.video.id)
  }
}

function handleLongPress() {
  isPlaying.value = !isPlaying.value
}

function handleLike() {
  emit('like', props.video.id)
}

function handleComment() {
  emit('comment', props.video.id)
}

function handleShare() {
  emit('share', props.video.id)
}

function handleAuthor() {
  emit('author', props.video.author.id)
}
</script>

<style lang="scss" scoped>
.video-card {
  position: relative;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.video-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(transparent 40%, rgba(0, 0, 0, 0.7) 100%);
  z-index: 1;
}

.double-tap-heart {
  position: absolute;
  z-index: 10;
  pointer-events: none;
  animation: heartPop 1s ease forwards;
}

@keyframes heartPop {
  0% {
    transform: scale(0) rotate(-15deg);
    opacity: 1;
  }
  20% {
    transform: scale(1.5) rotate(0deg);
    opacity: 1;
  }
  40% {
    transform: scale(1.2) rotate(0deg);
    opacity: 1;
  }
  100% {
    transform: scale(0.8) translateY(-100rpx) rotate(15deg);
    opacity: 0;
  }
}

.heart-emoji {
  font-size: 120rpx;
  filter: drop-shadow(0 4rpx 20rpx rgba(255, 45, 85, 0.6));
}

.video-content {
  position: absolute;
  inset: 0;
  z-index: 2;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  padding: 40rpx 32rpx;
  padding-bottom: 200rpx;
}

.bottom-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  max-width: 500rpx;
}

.author-row {
  display: flex;
  align-items: center;
  gap: 16rpx;
  margin-bottom: 20rpx;
}

.author-name {
  font-size: 28rpx;
  color: #fff;
  font-weight: 500;
}

.follow-btn {
  border: 2rpx solid #FF2D55;
  border-radius: 8rpx;
  padding: 4rpx 16rpx;
  background: rgba(255, 45, 85, 0.2);
  min-width: 88rpx;
  min-height: 44rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.follow-text {
  font-size: 22rpx;
  color: #FF2D55;
}

.video-title {
  font-size: 28rpx;
  color: #fff;
  line-height: 1.5;
  margin-bottom: 16rpx;
}

.music-row {
  display: flex;
  align-items: center;
  gap: 10rpx;
}

.music-icon {
  font-size: 24rpx;
}

.music-name {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.8);
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.right-actions {
  position: absolute;
  right: 24rpx;
  bottom: 200rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 32rpx;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
  min-width: 88rpx;
  min-height: 88rpx;
  justify-content: center;
}

.action-icon {
  font-size: 48rpx;
}

.action-count {
  font-size: 22rpx;
  color: #fff;
}

.action-avatar {
  position: relative;
  margin-top: 16rpx;
}

.vinyl-record {
  animation: spin 8s linear infinite;
}

.vinyl-record.paused {
  animation-play-state: paused;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.plus-badge {
  position: absolute;
  bottom: -8rpx;
  left: 50%;
  transform: translateX(-50%);
  width: 32rpx;
  height: 32rpx;
  background: #FF2D55;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.plus-text {
  font-size: 24rpx;
  color: #fff;
  font-weight: bold;
}

.progress-bar {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 6rpx;
  background: rgba(255, 255, 255, 0.2);
  z-index: 5;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #FF2D55 0%, #AF52ED 100%);
  border-radius: 3rpx;
  transition: width 0.2s linear;
}
</style>
