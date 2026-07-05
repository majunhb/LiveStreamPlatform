<template>
  <view class="chat-input-bar">
    <view class="input-wrapper">
      <input
        class="chat-input"
        v-model="inputText"
        placeholder="说点什么..."
        placeholder-class="input-placeholder"
        confirm-type="send"
        @confirm="handleSend"
      />
    </view>
    <view class="action-btn emoji-btn" @tap="$emit('emoji')">
      <text class="btn-icon">😊</text>
    </view>
    <view class="action-btn gift-btn" @tap="$emit('gift')">
      <text class="btn-icon">🎁</text>
    </view>
    <view class="action-btn like-btn" @tap="handleLike">
      <text class="btn-icon heart-icon" :class="{ liked: isLiked }">{{ isLiked ? '❤️' : '🤍' }}</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const inputText = ref('')
const isLiked = ref(false)

const emit = defineEmits(['send', 'emoji', 'gift', 'like'])

function handleSend() {
  const text = inputText.value.trim()
  if (text) {
    emit('send', text)
    inputText.value = ''
  }
}

function handleLike() {
  isLiked.value = !isLiked.value
  emit('like', isLiked.value)
}
</script>

<style lang="scss" scoped>
.chat-input-bar {
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 16rpx 24rpx;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(20px);
  border-top: 1rpx solid rgba(255, 255, 255, 0.1);
}

.input-wrapper {
  flex: 1;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 32rpx;
  padding: 0 24rpx;
  height: 64rpx;
  display: flex;
  align-items: center;
}

.chat-input {
  flex: 1;
  height: 64rpx;
  font-size: 28rpx;
  color: #fff;
}

.input-placeholder {
  color: #666;
}

.action-btn {
  width: 64rpx;
  height: 64rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.btn-icon {
  font-size: 40rpx;
}

.heart-icon {
  transition: transform 0.2s ease;
}

.heart-icon.liked {
  animation: heartBeat 0.3s ease;
}

@keyframes heartBeat {
  0% { transform: scale(1); }
  50% { transform: scale(1.3); }
  100% { transform: scale(1); }
}
</style>
