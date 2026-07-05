<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { api, coverGradients, avatarStyle, firstChar } from '@/api'
import type { ShortVideo } from '@/types'
import LoadingSpinner from '@/components/LoadingSpinner.vue'

const router = useRouter()

const videos = ref<ShortVideo[]>([])
const loading = ref(true)
const activeIndex = ref(0)
const container = ref<HTMLElement | null>(null)
const liked = ref<Record<string, boolean>>({})

// double-tap heart
const lastTap = ref(0)
const tapHearts = ref<Array<{ id: number; x: number; y: number }>>([])
let heartId = 0

// play pulse
const playing = ref(true)

function coverBg(v: ShortVideo): string {
  const idx = parseInt(v.id.replace(/\D/g, '') || '0', 10)
  return coverGradients[idx % coverGradients.length]
}
function avatarBg(v: ShortVideo): string {
  return avatarStyle(v.authorName, parseInt(v.id.replace(/\D/g, '') || '0', 10))
}
function fmt(n: number): string {
  if (n >= 10000) return (n / 10000).toFixed(1).replace(/\.0$/, '') + 'w'
  return String(n)
}

async function load() {
  loading.value = true
  try {
    const res = await api.getRecommendFeed(1, 12)
    videos.value = res.records
    await nextTick()
    container.value?.children[0]?.scrollIntoView({ behavior: 'auto' })
  } finally {
    loading.value = false
  }
}

function onScroll() {
  const el = container.value
  if (!el) return
  const idx = Math.round(el.scrollTop / el.clientHeight)
  if (idx !== activeIndex.value) {
    activeIndex.value = idx
  }
}

function toggleLike(v: ShortVideo) {
  const likedNow = !liked.value[v.id]
  liked.value[v.id] = likedNow
  const target = videos.value.find((x) => x.id === v.id)
  if (target) {
    target.likeCount += likedNow ? 1 : -1
  }
}

function onTap(e: TouchEvent | MouseEvent, v: ShortVideo) {
  const now = Date.now()
  if (now - lastTap.value < 300) {
    // double tap → heart
    const rect = (e.currentTarget as HTMLElement).getBoundingClientRect()
    const point = 'touches' in e ? e.touches[0] : (e as MouseEvent)
    const x = point.clientX - rect.left
    const y = point.clientY - rect.top
    const h = { id: ++heartId, x, y }
    tapHearts.value.push(h)
    setTimeout(() => {
      const i = tapHearts.value.findIndex((t) => t.id === h.id)
      if (i !== -1) tapHearts.value.splice(i, 1)
    }, 900)
    if (!liked.value[v.id]) toggleLike(v)
  }
  lastTap.value = now
  playing.value = !playing.value
}

function back() {
  router.back()
}

onMounted(load)
onBeforeUnmount(() => {})
</script>

<template>
  <div class="video-page">
    <LoadingSpinner v-if="loading" :size="40" label="加载精彩视频..." class="loader" />

    <template v-else>
      <div
        ref="container"
        class="feed no-scrollbar"
        @scroll.passive="onScroll"
      >
        <section
          v-for="(v, i) in videos"
          :key="v.id"
          class="slide"
          :style="{ background: coverBg(v) }"
          @touchstart="onTap($event, v)"
          @click="onTap($event, v)"
        >
          <div class="slide-glow" />
          <div class="slide-noise" />

          <!-- top bar -->
          <div class="top-bar">
            <button class="back-btn" @click.stop="back">‹</button>
            <div class="tabs-ind">
              <span class="t active">推荐</span>
              <span class="t">关注</span>
            </div>
            <button class="search-btn">🔍</button>
          </div>

          <!-- center play indicator -->
          <div v-if="activeIndex === i && !playing" class="paused-mark">▶</div>

          <!-- tap hearts -->
          <span
            v-for="h in tapHearts"
            :key="h.id"
            class="tap-heart"
            :style="{ left: h.x + 'px', top: h.y + 'px' }"
          >❤</span>

          <!-- bottom info -->
          <div class="bottom-info">
            <div class="author-row">
              <span class="avatar" :style="{ background: avatarBg(v) }">{{ firstChar(v.authorName) }}</span>
              <span class="author-name">@{{ v.authorName }}</span>
              <button class="follow-btn">+ 关注</button>
            </div>
            <p class="title">{{ v.title }}</p>
            <p class="desc">{{ v.description }}</p>
            <div class="music">
              <span class="music-icon">🎵</span>
              <span class="music-text">原声 - {{ v.authorName }}</span>
            </div>
          </div>

          <!-- right action rail -->
          <div class="rail">
            <button class="rail-btn" @click.stop="toggleLike(v)">
              <span class="r-icon" :class="{ liked: liked[v.id] }">{{ liked[v.id] ? '❤' : '🤍' }}</span>
              <span class="r-num">{{ fmt(v.likeCount) }}</span>
            </button>
            <button class="rail-btn">
              <span class="r-icon">💬</span>
              <span class="r-num">{{ fmt(v.commentCount) }}</span>
            </button>
            <button class="rail-btn">
              <span class="r-icon">↗</span>
              <span class="r-num">分享</span>
            </button>
            <div class="vinyl" :style="{ background: avatarBg(v) }">
              <span class="vinyl-dot">♪</span>
            </div>
          </div>
        </section>
      </div>

      <!-- page dots -->
      <div class="pager">
        <span
          v-for="(v, i) in videos"
          :key="v.id"
          class="dot"
          :class="{ active: activeIndex === i }"
        />
      </div>
    </template>
  </div>
</template>

<style scoped lang="scss">
.video-page {
  position: absolute;
  inset: 0;
  background: #000;
  overflow: hidden;
}
.loader {
  margin: auto;
}
.feed {
  height: 100%;
  overflow-y: scroll;
  scroll-snap-type: y mandatory;
  scrollbar-width: none;
}
.feed::-webkit-scrollbar {
  display: none;
}
.slide {
  position: relative;
  height: 100%;
  scroll-snap-align: start;
  scroll-snap-stop: always;
  overflow: hidden;
  display: flex;
}
.slide-glow {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(120% 80% at 80% 10%, rgba(255, 255, 255, 0.32), transparent 55%),
    radial-gradient(140% 100% at 0% 100%, rgba(0, 0, 0, 0.7), transparent 55%);
  mix-blend-mode: overlay;
}
.slide-noise {
  position: absolute;
  inset: 0;
  opacity: 0.35;
  background-image: radial-gradient(rgba(255, 255, 255, 0.07) 1px, transparent 1px);
  background-size: 4px 4px;
  mix-blend-mode: overlay;
}

.top-bar {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 14px;
  padding-top: calc(14px + env(safe-area-inset-top, 0));
  z-index: 20;
  background: linear-gradient(180deg, rgba(0, 0, 0, 0.45), transparent);
}
.back-btn {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.4);
  color: #fff;
  font-size: 24px;
  line-height: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}
.tabs-ind {
  display: flex;
  gap: 16px;
}
.tabs-ind .t {
  font-size: 15px;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.55);
}
.tabs-ind .t.active {
  color: #fff;
  font-weight: 800;
  position: relative;
}
.tabs-ind .t.active::after {
  content: '';
  position: absolute;
  bottom: -6px;
  left: 50%;
  transform: translateX(-50%);
  width: 18px;
  height: 3px;
  border-radius: 2px;
  background: var(--grad-primary);
}
.search-btn {
  font-size: 18px;
  color: #fff;
}

.paused-mark {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 72px;
  height: 72px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.35);
  backdrop-filter: blur(8px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: #fff;
  font-size: 30px;
  padding-left: 6px;
  z-index: 15;
  animation: pop-in 0.3s ease;
}

.tap-heart {
  position: absolute;
  font-size: 60px;
  color: #ff2d55;
  transform: translate(-50%, -50%);
  animation: tap-burst 0.9s ease-out forwards;
  filter: drop-shadow(0 0 12px rgba(255, 45, 85, 0.7));
  pointer-events: none;
  z-index: 18;
}
@keyframes tap-burst {
  0% { transform: translate(-50%, -50%) scale(0.3); opacity: 0; }
  30% { transform: translate(-50%, -70%) scale(1.2); opacity: 1; }
  100% { transform: translate(-50%, -120%) scale(0.9); opacity: 0; }
}

.bottom-info {
  position: absolute;
  left: 0;
  right: 78px;
  bottom: 0;
  padding: 0 16px 24px;
  z-index: 20;
  background: linear-gradient(0deg, rgba(0, 0, 0, 0.55), transparent);
}
.author-row {
  display: flex;
  align-items: center;
  gap: 9px;
  margin-bottom: 10px;
}
.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 15px;
  font-weight: 800;
  color: #fff;
  border: 2px solid #fff;
}
.author-name {
  font-size: 15px;
  font-weight: 700;
  color: #fff;
}
.follow-btn {
  padding: 4px 14px;
  border-radius: var(--radius-pill);
  background: var(--grad-primary);
  font-size: 12px;
  color: #fff;
  font-weight: 700;
}
.title {
  font-size: 15px;
  font-weight: 700;
  color: #fff;
  line-height: 1.4;
  margin-bottom: 6px;
  text-shadow: 0 1px 4px rgba(0, 0, 0, 0.5);
}
.desc {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.8);
  margin-bottom: 10px;
  text-shadow: 0 1px 4px rgba(0, 0, 0, 0.5);
}
.music {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.75);
  max-width: 80%;
  overflow: hidden;
}
.music-icon {
  flex: none;
}
.music-text {
  white-space: nowrap;
  animation: marquee 8s linear infinite;
}
@keyframes marquee {
  0% { transform: translateX(0); }
  100% { transform: translateX(-100%); }
}

.rail {
  position: absolute;
  right: 10px;
  bottom: 24px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 18px;
  z-index: 20;
}
.rail-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  color: #fff;
}
.r-icon {
  font-size: 30px;
  filter: drop-shadow(0 2px 6px rgba(0, 0, 0, 0.5));
  transition: transform 0.2s ease;
}
.r-icon.liked {
  color: #ff2d55;
  animation: heart-burst 0.4s ease;
}
.rail-btn:active .r-icon {
  transform: scale(0.85);
}
.r-num {
  font-size: 12px;
  font-weight: 600;
  color: #fff;
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.6);
}
.vinyl {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  border: 2px solid rgba(255, 255, 255, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  color: #fff;
  animation: spin 4s linear infinite;
}
.vinyl-dot {
  filter: drop-shadow(0 0 4px rgba(0, 0, 0, 0.5));
}
@keyframes spin {
  to { transform: rotate(360deg); }
}

.pager {
  position: absolute;
  right: 6px;
  top: 50%;
  transform: translateY(-50%);
  display: flex;
  flex-direction: column;
  gap: 5px;
  z-index: 30;
}
.pager .dot {
  width: 4px;
  height: 4px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.3);
}
.pager .dot.active {
  background: #fff;
  height: 14px;
  border-radius: 2px;
}
</style>
