<template>
  <view class="live-room">
    <view class="video-area" :style="{ background: room?.cover || 'linear-gradient(135deg, #FF2D55 0%, #AF52ED 100%)' }">
      <view class="top-bar">
        <view class="back-btn" @tap="goBack">
          <text class="back-icon">←</text>
        </view>
        <view class="anchor-info" @tap="goAnchorProfile">
          <UserAvatar :name="room?.anchor?.nickname || ''" size="md" />
          <view class="anchor-detail">
            <view class="anchor-top">
              <text class="anchor-name">{{ room?.anchor?.nickname }}</text>
              <LiveBadge />
            </view>
            <text class="anchor-fans">{{ formatCount(room?.anchor?.followers || 0) }} 粉丝</text>
          </view>
          <view class="follow-btn" :class="{ followed: isFollowed, animating: followAnimating }" @tap.stop="toggleFollow">
            <text class="follow-text">{{ isFollowed ? '✓ 已关注' : '+ 关注' }}</text>
          </view>
        </view>
        <view class="share-btn" @tap="handleShare">
          <text class="share-icon">↗️</text>
        </view>
      </view>

      <view class="entry-notification" v-if="entryNotices.length > 0">
        <view class="entry-track">
          <view
            v-for="notice in entryNotices"
            :key="notice.id"
            class="entry-item"
            :style="{ animationDelay: `${notice.delay}s` }"
          >
            <text class="entry-icon">👋</text>
            <text class="entry-text">欢迎 <text class="entry-name">{{ notice.username }}</text> 进入直播间</text>
          </view>
        </view>
      </view>

      <view class="viewer-info">
        <text class="viewer-icon">👁️</text>
        <text class="viewer-count" :class="{ bump: viewerBump }">{{ formatCount(displayViewers) }}</text>
      </view>

      <DanmakuLayer ref="danmakuRef" />
      <HeartBurst ref="heartBurstRef" />
      <GiftToast ref="giftToastRef" />

      <view class="fullscreen-gift" v-if="fullscreenGift" :style="{ '--gift-icon': fullscreenGift.icon }">
        <view class="gift-glow"></view>
        <text class="fullscreen-gift-icon">{{ fullscreenGift.icon }}</text>
        <view class="fullscreen-gift-info">
          <text class="fullscreen-gift-user">{{ fullscreenGift.username }}</text>
          <text class="fullscreen-gift-name">送出 {{ fullscreenGift.name }}</text>
        </view>
      </view>

      <view class="gift-combo" v-if="giftCombo.show">
        <text class="combo-count" :class="{ shake: comboShake }">x{{ giftCombo.count }}</text>
        <text class="combo-text">combo!</text>
      </view>

      <view class="like-float-btn" @tap="handleLikeTap">
        <text class="like-icon">❤️</text>
        <text class="like-count" :class="{ bump: likeBump }">{{ formatCount(displayLikes) }}</text>
      </view>
    </view>

    <view class="bottom-area">
      <scroll-view class="chat-list" scroll-y :scroll-top="chatScrollTop">
        <view class="chat-item" v-for="msg in chatMessages" :key="msg.id" :class="{ 'is-self': msg.isSelf }">
          <text class="chat-user" :style="{ color: msg.color }">{{ msg.username }}:</text>
          <text class="chat-content">{{ msg.content }}</text>
        </view>
        <view class="gift-item" v-for="gift in giftNotifications" :key="gift.id">
          <text class="gift-user">{{ gift.username }}</text>
          <text class="gift-action">送出</text>
          <text class="gift-icon">{{ gift.giftIcon }}</text>
          <text class="gift-name">{{ gift.giftName }}</text>
          <text v-if="gift.count > 1" class="gift-count">x{{ gift.count }}</text>
        </view>
      </scroll-view>

      <ChatInput
        @send="handleSendDanmaku"
        @gift="showGiftPanel = true"
        @like="handleLikeButton"
        @emoji="handleEmoji"
      />
    </view>

    <GiftPanel
      :visible="showGiftPanel"
      :coins="userStore.coins"
      @close="showGiftPanel = false"
      @send="handleSendGift"
    />
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick, computed, watch } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import UserAvatar from '@/components/UserAvatar.vue'
import LiveBadge from '@/components/LiveBadge.vue'
import DanmakuLayer from '@/components/DanmakuLayer.vue'
import HeartBurst from '@/components/HeartBurst.vue'
import GiftToast from '@/components/GiftToast.vue'
import GiftPanel from '@/components/GiftPanel.vue'
import ChatInput from '@/components/ChatInput.vue'
import { useUserStore } from '@/store/user'
import { liveRooms, getRandomDanmaku, getRandomColor, getRandomUser, gifts, getRandomEntryUser } from '@/mock/data'
import type { LiveRoom, GiftRecord, DanmakuMessage } from '@/mock/data'
import { formatCount } from '@/utils/format'

const userStore = useUserStore()
const room = ref<LiveRoom | null>(null)
const isFollowed = ref(false)
const followAnimating = ref(false)
const showGiftPanel = ref(false)
const danmakuRef = ref()
const heartBurstRef = ref()
const giftToastRef = ref()
const chatScrollTop = ref(0)
const chatMessages = ref<(DanmakuMessage & { isSelf?: boolean })[]>([])
const giftNotifications = ref<GiftRecord[]>([])
let chatTimer: ReturnType<typeof setInterval> | null = null
let entryTimer: ReturnType<typeof setInterval> | null = null
let viewerTimer: ReturnType<typeof setInterval> | null = null
let msgIdCounter = 0

const displayLikes = ref(0)
const likeBump = ref(false)
const displayViewers = ref(0)
const viewerBump = ref(false)

const entryNotices = ref<{ id: number; username: string; delay: number }[]>([])
let entryIdCounter = 0

const fullscreenGift = ref<{ icon: string; name: string; username: string } | null>(null)

const giftCombo = ref({
  show: false,
  count: 0,
  giftId: '',
  userId: '',
  lastTime: 0
})
const comboShake = ref(false)
let comboTimer: ReturnType<typeof setTimeout> | null = null

onLoad((options: any) => {
  const roomId = options?.roomId || '1'
  loadRoom(roomId)
})

onMounted(() => {
  startChatSimulation()
  startEntrySimulation()
  startViewerSimulation()
})

onUnmounted(() => {
  if (chatTimer) clearInterval(chatTimer)
  if (entryTimer) clearInterval(entryTimer)
  if (viewerTimer) clearInterval(viewerTimer)
  if (comboTimer) clearTimeout(comboTimer)
})

function loadRoom(roomId: string) {
  room.value = liveRooms.find(r => r.id === roomId) || liveRooms[0]
  displayLikes.value = room.value?.likes || 0
  displayViewers.value = room.value?.viewers || 0
}

function goBack() {
  uni.navigateBack()
}

function goAnchorProfile() {
  uni.showToast({ title: '主播主页开发中', icon: 'none' })
}

function toggleFollow() {
  followAnimating.value = true
  setTimeout(() => {
    isFollowed.value = !isFollowed.value
    setTimeout(() => {
      followAnimating.value = false
    }, 200)
  }, 150)
  uni.showToast({
    title: isFollowed.value ? '已取消关注' : '关注成功',
    icon: 'none'
  })
}

function handleShare() {
  uni.showToast({ title: '分享功能开发中', icon: 'none' })
}

function handleSendDanmaku(content: string) {
  const user = userStore.userInfo
  const msg: DanmakuMessage & { isSelf?: boolean } = {
    id: String(msgIdCounter++),
    userId: user?.id || '1',
    username: user?.nickname || '我',
    content,
    color: '#FFD700',
    isSelf: true
  }
  chatMessages.value.push(msg)
  danmakuRef.value?.pushDanmaku(`${user?.nickname || '我'}: ${content}`, '#FFD700')
  scrollChatToBottom()
}

function handleLikeTap(e: any) {
  if (room.value) {
    room.value.likes++
    triggerLikeBump()
  }
  
  let x = 600
  let y = 100
  
  if (e?.detail?.x !== undefined) {
    x = e.detail.x * 2
  } else if (e?.touches?.[0]?.clientX !== undefined) {
    x = e.touches[0].clientX * 2
  } else if (e?.changedTouches?.[0]?.clientX !== undefined) {
    x = e.changedTouches[0].clientX * 2
  }
  
  if (e?.detail?.y !== undefined) {
    y = e.detail.y * 2
  } else if (e?.touches?.[0]?.clientY !== undefined) {
    y = e.touches[0].clientY * 2
  } else if (e?.changedTouches?.[0]?.clientY !== undefined) {
    y = e.changedTouches[0].clientY * 2
  }
  
  heartBurstRef.value?.burst(x + Math.random() * 50 - 25, y + Math.random() * 50 - 25, 6 + Math.floor(Math.random() * 4))
}

function triggerLikeBump() {
  likeBump.value = true
  setTimeout(() => {
    likeBump.value = false
  }, 300)
}

watch(() => room.value?.likes, (newVal, oldVal) => {
  if (newVal !== undefined && oldVal !== undefined && newVal !== oldVal) {
    const diff = newVal - oldVal
    const steps = Math.min(Math.abs(diff), 10)
    const stepValue = diff / steps
    let current = oldVal
    let step = 0
    const interval = setInterval(() => {
      step++
      current += stepValue
      displayLikes.value = Math.round(current)
      if (step >= steps) {
        displayLikes.value = newVal
        clearInterval(interval)
      }
    }, 30)
  }
})

function handleLikeButton(liked: boolean) {
  if (room.value) {
    room.value.likes += liked ? 1 : -1
    triggerLikeBump()
  }
  if (liked) {
    const x = 600 + Math.random() * 50 - 25
    const y = 100 + Math.random() * 50 - 25
    heartBurstRef.value?.burst(x, y, 8)
  }
}

function handleEmoji() {
  uni.showToast({ title: '表情功能开发中', icon: 'none' })
}

function handleSendGift(data: { gift: any; count: number; totalPrice: number }) {
  if (!userStore.deductCoins(data.totalPrice)) {
    uni.showToast({ title: '金币不足', icon: 'none' })
    return
  }

  const user = userStore.userInfo
  const record: GiftRecord = {
    id: String(Date.now()),
    userId: user?.id || '1',
    username: user?.nickname || '我',
    giftId: data.gift.id,
    giftName: data.gift.name,
    giftIcon: data.gift.icon,
    count: data.count,
    timestamp: Date.now()
  }

  giftNotifications.value.push(record)
  giftToastRef.value?.showToast(user?.nickname || '我', data.gift.name, data.gift.icon, data.count)
  danmakuRef.value?.pushDanmaku(`${user?.nickname || '我'} 送出了 ${data.gift.name} x${data.count}`, '#FFD700')
  
  updateGiftCombo(data.gift.id, user?.id || '1', data.count, data.gift.name, data.gift.icon, user?.nickname || '我')
  
  if (data.gift.price >= 500) {
    showFullscreenGift(data.gift.icon, data.gift.name, user?.nickname || '我')
  }
  
  showGiftPanel.value = false
  scrollChatToBottom()
}

function updateGiftCombo(giftId: string, userId: string, count: number, giftName: string, giftIcon: string, username: string) {
  const now = Date.now()
  
  if (giftCombo.value.giftId === giftId && giftCombo.value.userId === userId && now - giftCombo.value.lastTime < 3000) {
    giftCombo.value.count += count
  } else {
    giftCombo.value.count = count
    giftCombo.value.giftId = giftId
    giftCombo.value.userId = userId
  }
  
  giftCombo.value.lastTime = now
  giftCombo.value.show = true
  
  comboShake.value = true
  setTimeout(() => {
    comboShake.value = false
  }, 300)
  
  if (comboTimer) clearTimeout(comboTimer)
  comboTimer = setTimeout(() => {
    giftCombo.value.show = false
  }, 2000)
}

function showFullscreenGift(icon: string, name: string, username: string) {
  fullscreenGift.value = { icon, name, username }
  setTimeout(() => {
    fullscreenGift.value = null
  }, 2000)
}

function startChatSimulation() {
  chatTimer = setInterval(() => {
    const user = getRandomUser()
    const content = getRandomDanmaku()
    const color = getRandomColor()

    const msg: DanmakuMessage & { isSelf?: boolean } = {
      id: String(msgIdCounter++),
      userId: user.id,
      username: user.nickname,
      content,
      color,
      isSelf: false
    }
    chatMessages.value.push(msg)

    if (chatMessages.value.length > 50) {
      chatMessages.value = chatMessages.value.slice(-50)
    }

    scrollChatToBottom()
  }, 2000)
}

function startEntrySimulation() {
  entryTimer = setInterval(() => {
    const username = getRandomEntryUser()
    const id = entryIdCounter++
    entryNotices.value.push({ id, username, delay: 0 })
    
    setTimeout(() => {
      const index = entryNotices.value.findIndex(n => n.id === id)
      if (index > -1) {
        entryNotices.value.splice(index, 1)
      }
    }, 4000)
  }, 5000)
}

function startViewerSimulation() {
  viewerTimer = setInterval(() => {
    if (room.value) {
      const change = Math.floor(Math.random() * 7) - 3
      room.value.viewers = Math.max(1, room.value.viewers + change)
      
      viewerBump.value = true
      setTimeout(() => {
        viewerBump.value = false
      }, 300)
    }
  }, 3000)
}

watch(() => room.value?.viewers, (newVal, oldVal) => {
  if (newVal !== undefined && oldVal !== undefined && newVal !== oldVal) {
    const diff = newVal - oldVal
    const steps = Math.min(Math.abs(diff), 8)
    const stepValue = diff / steps
    let current = oldVal
    let step = 0
    const interval = setInterval(() => {
      step++
      current += stepValue
      displayViewers.value = Math.round(current)
      if (step >= steps) {
        displayViewers.value = newVal
        clearInterval(interval)
      }
    }, 40)
  }
})

function scrollChatToBottom() {
  nextTick(() => {
    chatScrollTop.value = chatMessages.value.length * 100
  })
}
</script>

<style lang="scss" scoped>
.live-room {
  min-height: 100vh;
  background: #0a0a0f;
  display: flex;
  flex-direction: column;
}

.video-area {
  position: relative;
  width: 100%;
  height: 65vh;
  flex-shrink: 0;
}

.top-bar {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  padding: 80rpx 24rpx 24rpx;
  display: flex;
  align-items: center;
  gap: 16rpx;
  z-index: 30;
  background: linear-gradient(180deg, rgba(0,0,0,0.6) 0%, transparent 100%);
}

.back-btn {
  width: 64rpx;
  height: 64rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.4);
  border-radius: 50%;
  flex-shrink: 0;
  min-width: 88rpx;
  min-height: 88rpx;
}

.back-icon {
  font-size: 36rpx;
  color: #fff;
}

.anchor-info {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 16rpx;
  background: rgba(0, 0, 0, 0.4);
  padding: 8rpx 16rpx 8rpx 8rpx;
  border-radius: 48rpx;
}

.anchor-detail {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2rpx;
}

.anchor-top {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.anchor-name {
  font-size: 26rpx;
  font-weight: bold;
  color: #fff;
}

.anchor-fans {
  font-size: 20rpx;
  color: rgba(255, 255, 255, 0.7);
}

.follow-btn {
  padding: 8rpx 20rpx;
  background: linear-gradient(135deg, #FF2D55 0%, #AF52ED 100%);
  border-radius: 24rpx;
  flex-shrink: 0;
  transition: all 0.2s ease;
  min-width: 88rpx;
  min-height: 44rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.follow-btn.followed {
  background: rgba(255, 255, 255, 0.2);
}

.follow-btn.animating {
  animation: followPop 0.35s ease;
}

@keyframes followPop {
  0% { transform: scale(1); }
  50% { transform: scale(1.2); }
  100% { transform: scale(1); }
}

.follow-text {
  font-size: 22rpx;
  color: #fff;
  font-weight: 500;
}

.share-btn {
  width: 64rpx;
  height: 64rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.4);
  border-radius: 50%;
  flex-shrink: 0;
  min-width: 88rpx;
  min-height: 88rpx;
}

.share-icon {
  font-size: 28rpx;
}

.entry-notification {
  position: absolute;
  top: 180rpx;
  left: 24rpx;
  right: 160rpx;
  z-index: 25;
  overflow: hidden;
  pointer-events: none;
}

.entry-track {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.entry-item {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 8rpx 16rpx;
  background: linear-gradient(90deg, rgba(175, 82, 237, 0.3) 0%, transparent 100%);
  border-radius: 20rpx;
  animation: slideInRight 0.5s ease forwards, fadeOut 0.5s ease 3.5s forwards;
}

@keyframes slideInRight {
  from {
    transform: translateX(100%);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

@keyframes fadeOut {
  from {
    opacity: 1;
    transform: translateX(0);
  }
  to {
    opacity: 0;
    transform: translateX(-20rpx);
  }
}

.entry-icon {
  font-size: 20rpx;
}

.entry-text {
  font-size: 22rpx;
  color: rgba(255, 255, 255, 0.9);
}

.entry-name {
  color: #00D4FF;
  font-weight: 500;
}

.viewer-info {
  position: absolute;
  top: 180rpx;
  right: 24rpx;
  display: flex;
  align-items: center;
  gap: 8rpx;
  background: rgba(0, 0, 0, 0.5);
  padding: 8rpx 16rpx;
  border-radius: 24rpx;
  z-index: 30;
}

.viewer-icon {
  font-size: 24rpx;
}

.viewer-count {
  font-size: 24rpx;
  color: #fff;
  font-weight: 500;
  display: inline-block;
}

.viewer-count.bump {
  animation: numberBump 0.3s ease;
}

@keyframes numberBump {
  0% { transform: scale(1); }
  50% { transform: scale(1.3); }
  100% { transform: scale(1); }
}

.like-float-btn {
  position: absolute;
  bottom: 32rpx;
  right: 24rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
  z-index: 25;
  min-width: 88rpx;
  min-height: 88rpx;
  justify-content: center;
}

.like-icon {
  font-size: 56rpx;
  filter: drop-shadow(0 4rpx 12rpx rgba(255, 45, 85, 0.6));
}

.like-count {
  font-size: 22rpx;
  color: #fff;
  text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.8);
  display: inline-block;
}

.like-count.bump {
  animation: numberBump 0.3s ease;
}

.bottom-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #12121a;
}

.chat-list {
  flex: 1;
  padding: 16rpx 24rpx;
  min-height: 0;
}

.chat-item {
  padding: 6rpx 0;
  font-size: 26rpx;
  line-height: 1.5;
}

.chat-item.is-self .chat-content {
  color: #FFD700;
}

.chat-item.is-self {
  padding: 6rpx 16rpx;
  margin: 4rpx 0;
  background: linear-gradient(90deg, rgba(255, 215, 0, 0.15) 0%, transparent 100%);
  border-radius: 20rpx;
  border-left: 3rpx solid #FFD700;
}

.chat-user {
  font-weight: 500;
  margin-right: 8rpx;
}

.chat-content {
  color: #fff;
}

.gift-item {
  display: flex;
  align-items: center;
  gap: 6rpx;
  padding: 8rpx 16rpx;
  margin: 6rpx 0;
  background: linear-gradient(90deg, rgba(255, 215, 0, 0.2) 0%, transparent 100%);
  border-radius: 24rpx;
  font-size: 24rpx;
}

.gift-user {
  color: #FFD700;
  font-weight: 500;
}

.gift-action {
  color: rgba(255, 255, 255, 0.7);
}

.gift-icon {
  font-size: 28rpx;
}

.gift-name {
  color: #fff;
  font-weight: 500;
}

.gift-count {
  color: #FFD700;
  font-weight: bold;
}

.fullscreen-gift {
  position: absolute;
  inset: 0;
  z-index: 40;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  pointer-events: none;
  animation: fullscreenGiftAppear 2s ease forwards;
}

@keyframes fullscreenGiftAppear {
  0% {
    opacity: 0;
    transform: translateY(100%) scale(0.5);
  }
  20% {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
  80% {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
  100% {
    opacity: 0;
    transform: translateY(-20%) scale(1.1);
  }
}

.gift-glow {
  position: absolute;
  width: 400rpx;
  height: 400rpx;
  background: radial-gradient(circle, rgba(255, 215, 0, 0.6) 0%, transparent 70%);
  border-radius: 50%;
  animation: glowPulse 1s ease-in-out infinite;
}

@keyframes glowPulse {
  0%, 100% { transform: scale(1); opacity: 0.6; }
  50% { transform: scale(1.3); opacity: 0.9; }
}

.fullscreen-gift-icon {
  font-size: 200rpx;
  filter: drop-shadow(0 0 30rpx rgba(255, 215, 0, 0.8));
  animation: giftBounce 0.6s ease-in-out infinite alternate;
}

@keyframes giftBounce {
  from { transform: translateY(0); }
  to { transform: translateY(-20rpx); }
}

.fullscreen-gift-info {
  margin-top: 32rpx;
  text-align: center;
}

.fullscreen-gift-user {
  display: block;
  font-size: 36rpx;
  font-weight: bold;
  color: #FFD700;
  text-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.8);
}

.fullscreen-gift-name {
  display: block;
  font-size: 28rpx;
  color: #fff;
  margin-top: 8rpx;
  text-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.8);
}

.gift-combo {
  position: absolute;
  top: 50%;
  right: 32rpx;
  transform: translateY(-50%);
  z-index: 35;
  text-align: center;
  pointer-events: none;
  animation: comboAppear 0.3s ease;
}

@keyframes comboAppear {
  from {
    opacity: 0;
    transform: translateY(-50%) scale(0.5);
  }
  to {
    opacity: 1;
    transform: translateY(-50%) scale(1);
  }
}

.combo-count {
  display: block;
  font-size: 72rpx;
  font-weight: bold;
  background: linear-gradient(135deg, #FFD700 0%, #FF6B6B 100%);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  text-shadow: 0 4rpx 20rpx rgba(255, 215, 0, 0.5);
}

.combo-count.shake {
  animation: comboShake 0.3s ease;
}

@keyframes comboShake {
  0%, 100% { transform: scale(1); }
  25% { transform: scale(1.3) rotate(-5deg); }
  75% { transform: scale(1.3) rotate(5deg); }
}

.combo-text {
  display: block;
  font-size: 28rpx;
  font-weight: bold;
  color: #FFD700;
  text-transform: uppercase;
  letter-spacing: 4rpx;
  text-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.8);
}
</style>
