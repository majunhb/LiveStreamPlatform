<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { api, coverGradients, avatarStyle, firstChar } from '@/api'
import { useUserStore } from '@/stores/user'
import type { LiveRoom, Danmaku, GiftRecord, Gift } from '@/types'
import DanmakuLayer from '@/components/DanmakuLayer.vue'
import GiftPanel from '@/components/GiftPanel.vue'
import LoadingSpinner from '@/components/LoadingSpinner.vue'

const route = useRoute()
const router = useRouter()
const store = useUserStore()

const roomId = Number(route.params.id)
const room = ref<LiveRoom | null>(null)
const loading = ref(true)

const seedDanmakus = ref<Danmaku[]>([])
const giftRecords = ref<GiftRecord[]>([])
const chatList = ref<Array<Danmaku & { kind: 'chat' | 'system' | 'gift' }>>([])
const inputText = ref('')
const showGiftPanel = ref(false)
const sending = ref(false)

const danmakuRef = ref<InstanceType<typeof DanmakuLayer> | null>(null)
const chatScroll = ref<HTMLElement | null>(null)

// like animation
const likeBursts = ref<Array<{ id: number; x: number; color: string }>>([])
let likeId = 0

// gift toast
const giftToasts = ref<Array<{ id: number; record: GiftRecord }>>([])
let toastId = 0

// anchor info
const anchorBg = computed(() => avatarStyle(room.value?.anchorName, room.value?.id ?? 0))
const anchorInitial = computed(() => firstChar(room.value?.anchorName))
const coverBg = computed(() => {
  if (!room.value) return coverGradients[0]
  return coverGradients[(room.value.id - 1) % coverGradients.length]
})
const balance = computed(() => store.coinBalance)

function fmt(n: number): string {
  if (n >= 10000) return (n / 10000).toFixed(1).replace(/\.0$/, '') + '万'
  return String(n)
}
const viewers = computed(() => (room.value ? fmt(room.value.viewerCount) : '0'))

// auto increment viewer count
let viewerTimer: ReturnType<typeof setInterval> | null = null

async function loadRoom() {
  loading.value = true
  try {
    const [detail, dms, records] = await Promise.all([
      api.getRoomDetail(roomId),
      api.getRoomDanmakus(roomId),
      api.getRoomGiftRecords(roomId)
    ])
    room.value = detail
    seedDanmakus.value = dms
    giftRecords.value = records
    // seed chat list with a few danmakus + gift records
    records.slice(0, 3).forEach((r) => {
      chatList.value.push({
        id: `gr-${r.id}`,
        roomId: r.roomId,
        userId: r.fromUserId,
        username: r.fromUserName || '用户',
        content: `送出 ${r.giftIcon} ${r.giftName} ×${r.count}`,
        color: '#FFC24B',
        fontSize: 14,
        sendTime: r.createTime,
        kind: 'gift'
      })
    })
    dms.slice(-4).forEach((d) => {
      chatList.value.push({ ...d, kind: 'chat' })
    })
    scrollChatBottom()
  } finally {
    loading.value = false
  }
}

function scrollChatBottom() {
  nextTick(() => {
    const el = chatScroll.value
    if (el) el.scrollTop = el.scrollHeight
  })
}

function back() {
  router.back()
}

async function sendChat() {
  const text = inputText.value.trim()
  if (!text || sending.value) return
  sending.value = true
  try {
    const dm = await api.sendDanmaku(roomId, text)
    chatList.value.push({ ...dm, kind: 'chat' })
    danmakuRef.value?.push(dm)
    inputText.value = ''
    scrollChatBottom()
  } finally {
    sending.value = false
  }
}

function like() {
  const burst = {
    id: ++likeId,
    x: 20 + Math.random() * 60,
    color: ['#FF2D55', '#FF6B9D', '#AF52ED', '#FFC24B'][Math.floor(Math.random() * 4)]
  }
  likeBursts.value.push(burst)
  setTimeout(() => {
    const i = likeBursts.value.findIndex((b) => b.id === burst.id)
    if (i !== -1) likeBursts.value.splice(i, 1)
  }, 1100)
  // bump like count
  if (room.value) room.value = { ...room.value, likeCount: room.value.likeCount + 1 }
}

async function onGiftSend(gift: Gift, count: number) {
  const cost = gift.price * count
  if (!room.value) return
  // optimistic deduction
  if (!store.spendCoins(cost)) {
    showGiftPanel.value = false
    return
  }
  try {
    const record = await api.sendGift(roomId, room.value.userId, gift.id, count)
    // chat record
    chatList.value.push({
      id: `gr-${record.id}`,
      roomId: record.roomId,
      userId: record.fromUserId,
      username: record.fromUserName || '我',
      content: `送出 ${gift.icon} ${gift.name} ×${count}`,
      color: '#FFC24B',
      fontSize: 14,
      sendTime: record.createTime,
      kind: 'gift'
    })
    giftRecords.value.unshift(record)
    // toast
    const toast = { id: ++toastId, record }
    giftToasts.value.push(toast)
    setTimeout(() => {
      const i = giftToasts.value.findIndex((t) => t.id === toast.id)
      if (i !== -1) giftToasts.value.splice(i, 1)
    }, 3200)
    scrollChatBottom()
  } catch (e) {
    // refund on failure
    store.addCoins(cost)
  } finally {
    showGiftPanel.value = false
  }
}

onMounted(() => {
  loadRoom()
  viewerTimer = setInterval(() => {
    if (room.value && Math.random() < 0.7) {
      room.value = {
        ...room.value,
        viewerCount: room.value.viewerCount + Math.floor(Math.random() * 8)
      }
    }
  }, 2000)
})

onBeforeUnmount(() => {
  if (viewerTimer) clearInterval(viewerTimer)
})
</script>

<template>
  <div class="room-page">
    <LoadingSpinner v-if="loading" :size="40" label="进入直播间..." class="full-loader" />

    <template v-else-if="room">
      <!-- Video player area -->
      <div class="player" :style="{ background: coverBg }">
        <div class="player-glow" />
        <div class="player-noise" />

        <!-- top overlay -->
        <div class="player-top">
          <button class="back-btn" @click="back">‹</button>
          <div class="anchor-info">
            <div class="anchor-avatar" :style="{ background: anchorBg }">{{ anchorInitial }}</div>
            <div class="anchor-meta">
              <p class="anchor-name">{{ room.anchorName }}</p>
              <button class="follow">+ 关注</button>
            </div>
          </div>
          <div class="top-right">
            <div class="live-badge"><span class="dot" />LIVE</div>
            <div class="viewers">👁 {{ viewers }}</div>
          </div>
        </div>

        <!-- center play hint -->
        <div class="play-hint">
          <div class="play-ring">
            <span>▶</span>
          </div>
          <p class="play-text">直播播放中</p>
        </div>

        <!-- danmaku overlay -->
        <DanmakuLayer ref="danmakuRef" :room-id="roomId" :seed-danmakus="seedDanmakus" />

        <!-- gift toasts -->
        <TransitionGroup name="toast" tag="div" class="toast-stack">
          <div v-for="t in giftToasts" :key="t.id" class="gift-toast">
            <div class="toast-avatar" :style="{ background: anchorBg }">{{ anchorInitial }}</div>
            <div class="toast-body">
              <p class="toast-from">{{ t.record.fromUserName }} 送出</p>
              <p class="toast-gift">
                <span class="toast-icon">{{ t.record.giftIcon }}</span>
                <span class="toast-name">{{ t.record.giftName }}</span>
                <span class="toast-count">×{{ t.record.count }}</span>
              </p>
            </div>
            <div class="toast-spark">✨</div>
          </div>
        </TransitionGroup>

        <!-- like bursts -->
        <div class="like-layer">
          <span
            v-for="b in likeBursts"
            :key="b.id"
            class="like-burst"
            :style="{ left: b.x + '%', color: b.color }"
          >
            ❤
          </span>
        </div>
      </div>

      <!-- Chat area -->
      <div class="chat-area">
        <div ref="chatScroll" class="chat-list no-scrollbar">
          <div class="sys-msg">— 欢迎来到「{{ room.title }}」直播间 —</div>
          <div
            v-for="msg in chatList"
            :key="msg.id"
            class="chat-msg"
            :class="msg.kind"
          >
            <span v-if="msg.kind === 'gift'" class="gift-emoji">{{ msg.content.match(/[\u{1F300}-\u{1FAFF}]/u)?.[0] || '🎁' }}</span>
            <span class="msg-user" :style="{ color: msg.color }">{{ msg.username }}</span>
            <span v-if="msg.kind !== 'gift'" class="msg-colon">：</span>
            <span class="msg-content">{{ msg.kind === 'gift' ? msg.content.replace(/[\u{1F300}-\u{1FAFF}]/u, '').trim() : msg.content }}</span>
          </div>
        </div>

        <!-- Input bar -->
        <div class="input-bar">
          <button class="icon-btn chat-toggle">💬</button>
          <input
            v-model="inputText"
            class="chat-input"
            placeholder="发条弹幕聊聊吧～"
            @keyup.enter="sendChat"
          />
          <button class="icon-btn like-btn" @click="like">❤</button>
          <button class="icon-btn gift-btn" @click="showGiftPanel = true">🎁</button>
          <button class="icon-btn share-btn">↗</button>
        </div>
      </div>

      <GiftPanel :visible="showGiftPanel" @close="showGiftPanel = false" @send="onGiftSend" />
    </template>
  </div>
</template>

<style scoped lang="scss">
.room-page {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  background: #000;
  overflow: hidden;
}
.full-loader {
  margin: auto;
}

/* ===== Player ===== */
.player {
  position: relative;
  flex: none;
  aspect-ratio: 16 / 11;
  overflow: hidden;
}
.player-glow {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(120% 90% at 80% 0%, rgba(255, 255, 255, 0.35), transparent 50%),
    radial-gradient(120% 90% at 0% 100%, rgba(0, 0, 0, 0.5), transparent 50%);
  mix-blend-mode: overlay;
}
.player-noise {
  position: absolute;
  inset: 0;
  opacity: 0.5;
  background-image: radial-gradient(rgba(255, 255, 255, 0.08) 1px, transparent 1px);
  background-size: 4px 4px;
  mix-blend-mode: overlay;
  pointer-events: none;
}

.player-top {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 12px;
  padding-top: calc(14px + env(safe-area-inset-top, 0));
  z-index: 60;
  background: linear-gradient(180deg, rgba(0, 0, 0, 0.5), transparent);
}
.back-btn {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(8px);
  color: #fff;
  font-size: 24px;
  line-height: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  flex: none;
}
.anchor-info {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
  min-width: 0;
}
.anchor-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 15px;
  font-weight: 800;
  color: #fff;
  border: 2px solid rgba(255, 255, 255, 0.5);
  flex: none;
}
.anchor-meta {
  min-width: 0;
}
.anchor-name {
  font-size: 13px;
  font-weight: 700;
  color: #fff;
  text-shadow: 0 1px 4px rgba(0, 0, 0, 0.6);
}
.follow {
  margin-top: 2px;
  padding: 2px 10px;
  border-radius: var(--radius-pill);
  background: var(--grad-primary);
  font-size: 11px;
  color: #fff;
  font-weight: 700;
}
.top-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
  flex: none;
}
.live-badge {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 3px 8px;
  border-radius: var(--radius-pill);
  background: var(--grad-live);
  font-size: 10px;
  font-weight: 800;
  letter-spacing: 1px;
  color: #fff;
  animation: pulse-live 1.6s ease-in-out infinite;
}
.live-badge .dot {
  width: 5px;
  height: 5px;
  border-radius: 50%;
  background: #fff;
}
.viewers {
  font-size: 11px;
  color: #fff;
  font-weight: 600;
  background: rgba(0, 0, 0, 0.4);
  padding: 2px 8px;
  border-radius: var(--radius-pill);
  backdrop-filter: blur(6px);
}

.play-hint {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  z-index: 40;
  pointer-events: none;
}
.play-ring {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.35);
  backdrop-filter: blur(8px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  margin: 0 auto 8px;
  color: #fff;
  font-size: 22px;
  padding-left: 4px;
}
.play-text {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.7);
  letter-spacing: 2px;
}

.toast-stack {
  position: absolute;
  left: 12px;
  top: 90px;
  z-index: 70;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.gift-toast {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 7px 14px 7px 7px;
  border-radius: var(--radius-pill);
  background: linear-gradient(90deg, rgba(255, 194, 75, 0.25), rgba(255, 45, 85, 0.25));
  border: 1px solid rgba(255, 194, 75, 0.4);
  backdrop-filter: blur(10px);
  box-shadow: 0 8px 22px rgba(255, 45, 85, 0.3);
}
.toast-avatar {
  width: 26px;
  height: 26px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 800;
  color: #fff;
}
.toast-from {
  font-size: 10px;
  color: var(--text-secondary);
}
.toast-gift {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #fff;
  font-weight: 700;
}
.toast-icon {
  font-size: 16px;
}
.toast-name {
  color: var(--gold);
}
.toast-count {
  color: #fff;
}
.toast-spark {
  font-size: 14px;
  animation: spin 1.5s linear infinite;
}
@keyframes spin {
  to { transform: rotate(360deg); }
}

.like-layer {
  position: absolute;
  right: 18px;
  bottom: 10px;
  width: 80px;
  height: 60%;
  z-index: 55;
  pointer-events: none;
}
.like-burst {
  position: absolute;
  bottom: 0;
  font-size: 26px;
  animation: heart-rise 1.1s ease-out forwards;
  filter: drop-shadow(0 0 6px rgba(255, 45, 85, 0.6));
}
@keyframes heart-rise {
  0% { transform: translateY(0) scale(0.4) rotate(-15deg); opacity: 0; }
  20% { transform: translateY(-20px) scale(1.2); opacity: 1; }
  100% { transform: translateY(-160px) scale(0.8) rotate(15deg); opacity: 0; }
}

/* ===== Chat ===== */
.chat-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
  background:
    radial-gradient(100% 60% at 50% 100%, rgba(175, 82, 237, 0.1), transparent 60%),
    var(--bg-deep);
}
.chat-list {
  flex: 1;
  overflow-y: auto;
  padding: 12px 12px 6px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.sys-msg {
  text-align: center;
  font-size: 11px;
  color: var(--text-muted);
  padding: 6px 0;
}
.chat-msg {
  display: flex;
  align-items: center;
  gap: 3px;
  font-size: 13px;
  line-height: 1.4;
  padding: 5px 10px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.04);
  animation: rise-in 0.3s ease;
  flex: none;
}
.chat-msg.gift {
  background: linear-gradient(90deg, rgba(255, 194, 75, 0.12), rgba(255, 45, 85, 0.12));
  border: 1px solid rgba(255, 194, 75, 0.2);
}
.gift-emoji {
  font-size: 16px;
  margin-right: 2px;
}
.msg-user {
  font-weight: 700;
  flex: none;
}
.msg-colon {
  color: var(--text-secondary);
}
.msg-content {
  color: var(--text-secondary);
  word-break: break-all;
}
.chat-msg.gift .msg-content {
  color: var(--gold);
  font-weight: 600;
}

.input-bar {
  flex: none;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 12px;
  padding-bottom: calc(10px + env(safe-area-inset-bottom, 0));
  border-top: 1px solid var(--border);
  background: rgba(10, 10, 15, 0.9);
  backdrop-filter: blur(16px);
}
.chat-input {
  flex: 1;
  height: 36px;
  padding: 0 14px;
  border-radius: var(--radius-pill);
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid var(--border);
  font-size: 13px;
  color: #fff;
}
.chat-input::placeholder {
  color: var(--text-muted);
}
.chat-input:focus {
  border-color: var(--purple);
}
.icon-btn {
  flex: none;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid var(--border);
  transition: transform 0.18s ease, background 0.18s ease;
}
.icon-btn:active {
  transform: scale(0.9);
}
.like-btn {
  color: #ff6b9d;
}
.gift-btn {
  background: var(--grad-warm);
  border-color: transparent;
  box-shadow: 0 4px 12px rgba(255, 45, 85, 0.35);
}

/* toast transition */
.toast-enter-active {
  transition: all 0.4s cubic-bezier(0.22, 1, 0.36, 1);
}
.toast-leave-active {
  transition: all 0.3s ease;
  position: absolute;
}
.toast-enter-from {
  opacity: 0;
  transform: translateX(-30px) scale(0.8);
}
.toast-leave-to {
  opacity: 0;
  transform: scale(0.9);
}
</style>
