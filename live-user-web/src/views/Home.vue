<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { api } from '@/api'
import type { LiveRoom } from '@/types'
import LiveCard from '@/components/LiveCard.vue'
import BottomNav from '@/components/BottomNav.vue'
import LoadingSpinner from '@/components/LoadingSpinner.vue'

const router = useRouter()

const categories = ['推荐', '游戏', '音乐', '聊天', '美食']
const activeCat = ref('推荐')
const rooms = ref<LiveRoom[]>([])
const loading = ref(false)
const loadingMore = ref(false)
const page = ref(1)
const total = ref(0)
const refreshing = ref(false)
const searchKey = ref('')

const hasMore = computed(() => rooms.value.length < total.value)

async function loadRooms(reset = false) {
  if (reset) {
    page.value = 1
    rooms.value = []
    loading.value = true
  } else {
    loadingMore.value = true
  }
  try {
    const res = await api.getLivingRooms(page.value, 10, activeCat.value)
    rooms.value = reset ? res.records : [...rooms.value, ...res.records]
    total.value = res.total
  } finally {
    loading.value = false
    loadingMore.value = false
    refreshing.value = false
  }
}

async function switchCat(cat: string) {
  if (activeCat.value === cat) return
  activeCat.value = cat
  await loadRooms(true)
}

async function loadMore() {
  if (!hasMore.value || loadingMore.value) return
  page.value += 1
  await loadRooms(false)
}

async function refresh() {
  refreshing.value = true
  await loadRooms(true)
  refreshing.value = false
}

function openRoom(room: LiveRoom) {
  router.push(`/room/${room.id}`)
}

function goVideo() {
  router.push('/video')
}

onMounted(() => loadRooms(true))
</script>

<template>
  <div class="home-page">
    <!-- Top bar -->
    <header class="top-bar">
      <div class="brand">
        <span class="logo-chip">L</span>
        <span class="brand-name">LIVE<span class="grad">星球</span></span>
      </div>
      <div class="search">
        <span class="icon">🔍</span>
        <input v-model="searchKey" placeholder="搜索主播 / 直播间" />
      </div>
    </header>

    <!-- Category tabs -->
    <div class="cat-bar no-scrollbar">
      <button
        v-for="c in categories"
        :key="c"
        class="cat"
        :class="{ active: activeCat === c }"
        @click="switchCat(c)"
      >
        {{ c }}
      </button>
    </div>

    <!-- Rooms grid -->
    <main class="rooms-scroll no-scrollbar">
      <div v-if="loading" class="loading-wrap">
        <LoadingSpinner label="正在拉取直播间..." />
      </div>

      <template v-else>
        <div class="rooms-grid">
          <LiveCard
            v-for="(room, i) in rooms"
            :key="room.id"
            :room="room"
            :index="i"
            @click="openRoom"
          />
        </div>

        <div v-if="rooms.length === 0" class="empty">
          <div class="empty-icon">🛰️</div>
          <p>该分类下暂无直播</p>
        </div>

        <div class="load-more-wrap">
          <button
            v-if="hasMore"
            class="load-more"
            :disabled="loadingMore"
            @click="loadMore"
          >
            <LoadingSpinner v-if="loadingMore" :size="16" />
            <span>{{ loadingMore ? '加载中...' : '刷新 · 加载更多' }}</span>
          </button>
          <button v-else class="load-more refresh" :disabled="refreshing" @click="refresh">
            <span>{{ refreshing ? '刷新中...' : '↻ 下拉刷新' }}</span>
          </button>
        </div>
      </template>
    </main>

    <!-- Floating action button -->
    <button class="fab" @click="goVideo">
      <span class="fab-icon">🎬</span>
      <span class="fab-label">短视频</span>
    </button>

    <BottomNav />
  </div>
</template>

<style scoped lang="scss">
.home-page {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  background:
    radial-gradient(100% 60% at 50% 0%, rgba(255, 45, 85, 0.14), transparent 60%),
    var(--bg-deep);
}

.top-bar {
  flex: none;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px 10px;
  padding-top: calc(14px + env(safe-area-inset-top, 0));
}
.brand {
  display: flex;
  align-items: center;
  gap: 7px;
  flex: none;
}
.logo-chip {
  width: 30px;
  height: 30px;
  border-radius: 9px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: var(--font-display);
  font-weight: 800;
  font-size: 15px;
  color: #fff;
  background: var(--grad-primary);
  box-shadow: 0 6px 14px rgba(255, 45, 85, 0.45);
}
.brand-name {
  font-size: 18px;
  font-weight: 900;
  letter-spacing: 0.5px;
}
.brand-name .grad {
  background: var(--grad-primary);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}
.search {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 8px;
  height: 36px;
  padding: 0 12px;
  border-radius: var(--radius-pill);
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid var(--border);
}
.search .icon {
  font-size: 13px;
  opacity: 0.6;
}
.search input {
  flex: 1;
  font-size: 13px;
  color: #fff;
}
.search input::placeholder {
  color: var(--text-muted);
}

.cat-bar {
  flex: none;
  display: flex;
  gap: 8px;
  padding: 4px 16px 12px;
  overflow-x: auto;
}
.cat {
  flex: none;
  padding: 7px 16px;
  border-radius: var(--radius-pill);
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid var(--border);
  font-size: 13px;
  font-weight: 600;
  color: var(--text-secondary);
  transition: all 0.22s ease;
}
.cat.active {
  background: var(--grad-primary);
  color: #fff;
  border-color: transparent;
  box-shadow: 0 6px 16px rgba(255, 45, 85, 0.4);
}

.rooms-scroll {
  flex: 1;
  overflow-y: auto;
  padding: 0 12px 80px;
  -webkit-overflow-scrolling: touch;
}
.loading-wrap {
  padding: 60px 0;
}
.rooms-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 11px;
}
.empty {
  padding: 80px 0;
  text-align: center;
  color: var(--text-muted);
}
.empty-icon {
  font-size: 48px;
  margin-bottom: 10px;
  opacity: 0.5;
}
.load-more-wrap {
  display: flex;
  justify-content: center;
  padding: 22px 0 10px;
}
.load-more {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 24px;
  border-radius: var(--radius-pill);
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid var(--border);
  color: var(--text-secondary);
  font-size: 13px;
  font-weight: 600;
  transition: all 0.2s ease;
}
.load-more:active {
  transform: scale(0.97);
  background: rgba(255, 255, 255, 0.1);
}
.load-more.refresh {
  background: linear-gradient(135deg, rgba(0, 212, 255, 0.15), rgba(175, 82, 237, 0.15));
  color: var(--cyan);
  border-color: rgba(0, 212, 255, 0.3);
}

.fab {
  position: absolute;
  right: 14px;
  bottom: calc(var(--nav-height) + 16px);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 2px;
  width: 58px;
  height: 58px;
  border-radius: 50%;
  background: var(--grad-accent);
  box-shadow: 0 10px 26px rgba(175, 82, 237, 0.55);
  z-index: 100;
  animation: pop-in 0.5s ease 0.2s both;
}
.fab:active {
  transform: scale(0.92);
}
.fab-icon {
  font-size: 22px;
}
.fab-label {
  font-size: 9px;
  font-weight: 700;
  color: #fff;
  letter-spacing: 0.5px;
}
</style>
