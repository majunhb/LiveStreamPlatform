<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { api, avatarStyle, firstChar, coverGradients } from '@/api'
import { useUserStore } from '@/stores/user'
import type { ShortVideo, GiftRecord, Wallet } from '@/types'
import BottomNav from '@/components/BottomNav.vue'
import LoadingSpinner from '@/components/LoadingSpinner.vue'

const router = useRouter()
const store = useUserStore()

const myVideos = ref<ShortVideo[]>([])
const giftRecords = ref<GiftRecord[]>([])
const loading = ref(true)
const showRecharge = ref(false)
const recharging = ref(false)
const rechargeAmount = ref<number | null>(null)
const amounts = [10, 50, 100, 500, 1000, 5000]

const user = computed(() => store.user)
const avatarBg = computed(() => avatarStyle(user.value?.nickname, 0))
const initial = computed(() => firstChar(user.value?.nickname))
const balance = computed(() => store.coinBalance)
const totalConsume = computed(() => store.wallet?.totalConsume ?? 0)
const totalRecharge = computed(() => store.wallet?.totalRecharge ?? 0)

const stats = computed(() => [
  { label: '关注', value: 128 },
  { label: '粉丝', value: 2336 },
  { label: '获赞', value: 18900 }
])

function fmt(n: number): string {
  if (n >= 10000) return (n / 10000).toFixed(1).replace(/\.0$/, '') + 'w'
  return String(n)
}
function coverBg(v: ShortVideo): string {
  const idx = parseInt(v.id.replace(/\D/g, '') || '0', 10)
  return coverGradients[idx % coverGradients.length]
}

async function loadData() {
  loading.value = true
  try {
    const [videos, records] = await Promise.all([
      api.getMyVideos(),
      api.getRoomGiftRecords(0)
    ])
    myVideos.value = videos
    giftRecords.value = records
  } finally {
    loading.value = false
  }
}

function pickAmount(a: number) {
  rechargeAmount.value = a
}

async function confirmRecharge() {
  if (!rechargeAmount.value || recharging.value) return
  recharging.value = true
  try {
    const w: Wallet = await api.recharge(rechargeAmount.value)
    store.setWallet(w)
    showRecharge.value = false
    rechargeAmount.value = null
  } finally {
    recharging.value = false
  }
}

function logout() {
  store.logout()
  router.replace('/login')
}

onMounted(loadData)
</script>

<template>
  <div class="profile-page">
    <div class="scroll no-scrollbar">
      <!-- Header -->
      <header class="header">
        <div class="header-bg" />
        <div class="header-content">
          <div class="row">
            <div class="avatar" :style="{ background: avatarBg }">{{ initial }}</div>
            <div class="user-meta">
              <p class="nickname">{{ user?.nickname || '未登录' }}</p>
              <p class="username">@{{ user?.username || 'guest' }}</p>
              <p class="bio">{{ user?.bio || '这个主播很懒，什么都没留下' }}</p>
            </div>
          </div>
          <div class="stats">
            <div v-for="s in stats" :key="s.label" class="stat">
              <span class="stat-num">{{ fmt(s.value) }}</span>
              <span class="stat-label">{{ s.label }}</span>
            </div>
          </div>
        </div>
      </header>

      <!-- Wallet card -->
      <section class="wallet-card glass">
        <div class="wallet-top">
          <div>
            <p class="wallet-label">我的钱包（金币）</p>
            <p class="wallet-balance">{{ balance.toLocaleString() }}</p>
          </div>
          <div class="coin-emoji">🪙</div>
        </div>
        <div class="wallet-stats">
          <div class="w-stat">
            <span class="w-num">{{ totalRecharge.toLocaleString() }}</span>
            <span class="w-label">累计充值</span>
          </div>
          <div class="w-divider" />
          <div class="w-stat">
            <span class="w-num">{{ totalConsume.toLocaleString() }}</span>
            <span class="w-label">累计消费</span>
          </div>
        </div>
        <button class="recharge-btn" @click="showRecharge = true">
          <span>＋</span> 立即充值
        </button>
      </section>

      <!-- My videos -->
      <section class="block">
        <div class="block-head">
          <span class="block-title">我的作品</span>
          <span class="block-count">{{ myVideos.length }} 个</span>
        </div>
        <div v-if="loading" class="loading-mini">
          <LoadingSpinner :size="24" />
        </div>
        <div v-else-if="myVideos.length" class="video-grid">
          <div v-for="v in myVideos" :key="v.id" class="my-video">
            <div class="mv-cover" :style="{ background: coverBg(v) }">
              <span class="mv-play">▶</span>
              <span class="mv-likes">❤ {{ fmt(v.likeCount) }}</span>
            </div>
            <p class="mv-title ellipsis">{{ v.title }}</p>
          </div>
        </div>
        <div v-else class="empty-block">
          <span>📹 还没有作品，去发布第一条吧～</span>
        </div>
      </section>

      <!-- Gift records -->
      <section class="block">
        <div class="block-head">
          <span class="block-title">送礼记录</span>
          <span class="block-count">{{ giftRecords.length }} 条</span>
        </div>
        <div v-if="giftRecords.length" class="gift-list">
          <div v-for="r in giftRecords.slice(0, 5)" :key="r.id" class="gift-row">
            <span class="g-icon">{{ r.giftIcon }}</span>
            <div class="g-meta">
              <p class="g-name">送给 {{ r.toUserName }}</p>
              <p class="g-time">{{ new Date(r.createTime).toLocaleString('zh-CN', { hour12: false }) }}</p>
            </div>
            <div class="g-amount">
              <p class="g-count">×{{ r.count }}</p>
              <p class="g-coin">🪙 {{ r.totalCoin }}</p>
            </div>
          </div>
        </div>
        <div v-else class="empty-block">
          <span>🎁 暂无送礼记录</span>
        </div>
      </section>

      <!-- Settings -->
      <section class="block">
        <div class="menu-list">
          <button class="menu-row">
            <span class="m-icon">⚙️</span>
            <span class="m-text">设置</span>
            <span class="m-arrow">›</span>
          </button>
          <button class="menu-row">
            <span class="m-icon">🔔</span>
            <span class="m-text">消息通知</span>
            <span class="m-arrow">›</span>
          </button>
          <button class="menu-row">
            <span class="m-icon">❓</span>
            <span class="m-text">帮助与反馈</span>
            <span class="m-arrow">›</span>
          </button>
          <button class="menu-row danger" @click="logout">
            <span class="m-icon">🚪</span>
            <span class="m-text">退出登录</span>
            <span class="m-arrow">›</span>
          </button>
        </div>
      </section>

      <div class="bottom-pad" />
    </div>

    <BottomNav />

    <!-- Recharge modal -->
    <Transition name="fade">
      <div v-if="showRecharge" class="recharge-mask" @click.self="showRecharge = false">
        <div class="recharge-sheet">
          <div class="sheet-handle" />
          <p class="sheet-title">选择充值金额</p>
          <div class="amount-grid">
            <button
              v-for="a in amounts"
              :key="a"
              class="amount-cell"
              :class="{ active: rechargeAmount === a }"
              @click="pickAmount(a)"
            >
              <span class="a-num">{{ a }}</span>
              <span class="a-unit">金币</span>
              <span v-if="a >= 500" class="a-tag">超值</span>
            </button>
          </div>
          <div class="sheet-balance">
            当前余额：<b>{{ balance.toLocaleString() }}</b> 🪙
          </div>
          <button
            class="confirm-recharge"
            :disabled="!rechargeAmount || recharging"
            @click="confirmRecharge"
          >
            <LoadingSpinner v-if="recharging" :size="18" />
            <span v-else>充值 {{ rechargeAmount || 0 }} 🪙</span>
          </button>
        </div>
      </div>
    </Transition>
  </div>
</template>

<style scoped lang="scss">
.profile-page {
  position: absolute;
  inset: 0;
  background: var(--bg-deep);
}
.scroll {
  height: 100%;
  overflow-y: auto;
  padding-bottom: calc(var(--nav-height) + 12px);
  -webkit-overflow-scrolling: touch;
}

/* Header */
.header {
  position: relative;
  padding: 0 16px 18px;
  padding-top: calc(20px + env(safe-area-inset-top, 0));
  overflow: hidden;
}
.header-bg {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(120% 80% at 0% 0%, rgba(255, 45, 85, 0.4), transparent 55%),
    radial-gradient(120% 80% at 100% 0%, rgba(175, 82, 237, 0.4), transparent 55%),
    radial-gradient(120% 100% at 50% 120%, rgba(0, 212, 255, 0.18), transparent 55%);
}
.header-content {
  position: relative;
  z-index: 2;
}
.row {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 18px;
}
.avatar {
  width: 68px;
  height: 68px;
  border-radius: 22px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 30px;
  font-weight: 900;
  color: #fff;
  border: 2px solid rgba(255, 255, 255, 0.5);
  box-shadow: 0 10px 24px rgba(0, 0, 0, 0.4);
  flex: none;
}
.user-meta {
  flex: 1;
  min-width: 0;
}
.nickname {
  font-size: 20px;
  font-weight: 800;
  color: #fff;
  margin-bottom: 3px;
}
.username {
  font-size: 12px;
  color: var(--text-secondary);
  margin-bottom: 6px;
}
.bio {
  font-size: 12px;
  color: var(--text-secondary);
  line-height: 1.4;
}
.stats {
  display: flex;
  gap: 8px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid var(--border);
  border-radius: 16px;
  padding: 12px 0;
  backdrop-filter: blur(10px);
}
.stat {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 3px;
}
.stat-num {
  font-size: 17px;
  font-weight: 800;
  color: #fff;
}
.stat-label {
  font-size: 11px;
  color: var(--text-secondary);
}

/* Wallet */
.wallet-card {
  margin: 0 16px 16px;
  border-radius: 20px;
  padding: 16px;
  background: linear-gradient(135deg, rgba(255, 45, 85, 0.16), rgba(175, 82, 237, 0.16));
  border: 1px solid rgba(255, 45, 85, 0.25);
}
.wallet-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 14px;
}
.wallet-label {
  font-size: 12px;
  color: var(--text-secondary);
  margin-bottom: 4px;
}
.wallet-balance {
  font-size: 30px;
  font-weight: 900;
  background: var(--grad-warm);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}
.coin-emoji {
  font-size: 38px;
  filter: drop-shadow(0 4px 10px rgba(255, 194, 75, 0.5));
}
.wallet-stats {
  display: flex;
  align-items: center;
  padding: 10px 0;
  border-top: 1px solid var(--border);
  border-bottom: 1px solid var(--border);
  margin-bottom: 12px;
}
.w-stat {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 3px;
}
.w-num {
  font-size: 15px;
  font-weight: 800;
  color: #fff;
}
.w-label {
  font-size: 11px;
  color: var(--text-secondary);
}
.w-divider {
  width: 1px;
  height: 24px;
  background: var(--border);
}
.recharge-btn {
  width: 100%;
  height: 42px;
  border-radius: 12px;
  background: var(--grad-warm);
  color: #fff;
  font-size: 14px;
  font-weight: 800;
  letter-spacing: 1px;
  box-shadow: 0 8px 20px rgba(255, 194, 75, 0.35);
}
.recharge-btn:active {
  transform: scale(0.98);
}

/* Blocks */
.block {
  margin: 0 16px 16px;
}
.block-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}
.block-title {
  font-size: 16px;
  font-weight: 800;
  color: #fff;
}
.block-count {
  font-size: 12px;
  color: var(--text-muted);
}
.loading-mini {
  display: flex;
  justify-content: center;
  padding: 24px;
}
.video-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
}
.my-video {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.mv-cover {
  position: relative;
  aspect-ratio: 9 / 14;
  border-radius: 10px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}
.mv-play {
  font-size: 18px;
  color: #fff;
  opacity: 0.85;
}
.mv-likes {
  position: absolute;
  bottom: 5px;
  left: 5px;
  font-size: 10px;
  color: #fff;
  font-weight: 600;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.6);
}
.mv-title {
  font-size: 11px;
  color: var(--text-secondary);
}
.empty-block {
  padding: 30px 0;
  text-align: center;
  font-size: 12px;
  color: var(--text-muted);
  background: var(--bg-card);
  border-radius: 14px;
  border: 1px dashed var(--border);
}

/* Gift records */
.gift-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.gift-row {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
  border-radius: 14px;
  background: var(--bg-card);
  border: 1px solid var(--border);
}
.g-icon {
  font-size: 26px;
  flex: none;
}
.g-meta {
  flex: 1;
  min-width: 0;
}
.g-name {
  font-size: 13px;
  color: #fff;
  font-weight: 600;
}
.g-time {
  font-size: 11px;
  color: var(--text-muted);
  margin-top: 2px;
}
.g-amount {
  text-align: right;
  flex: none;
}
.g-count {
  font-size: 13px;
  font-weight: 700;
  color: var(--gold);
}
.g-coin {
  font-size: 11px;
  color: var(--text-secondary);
}

/* Menu */
.menu-list {
  background: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: 16px;
  overflow: hidden;
}
.menu-row {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 14px;
  border-bottom: 1px solid var(--border);
  text-align: left;
  transition: background 0.18s ease;
}
.menu-row:last-child {
  border-bottom: none;
}
.menu-row:active {
  background: rgba(255, 255, 255, 0.04);
}
.m-icon {
  font-size: 18px;
  flex: none;
}
.m-text {
  flex: 1;
  font-size: 14px;
  color: #fff;
}
.m-arrow {
  color: var(--text-muted);
  font-size: 18px;
}
.menu-row.danger .m-text {
  color: #ff6b6b;
}

.bottom-pad {
  height: 12px;
}

/* Recharge modal */
.recharge-mask {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.6);
  z-index: 1000;
  display: flex;
  align-items: flex-end;
}
.recharge-sheet {
  width: 100%;
  background: linear-gradient(180deg, rgba(28, 28, 38, 0.98), rgba(14, 14, 20, 0.99));
  border-top-left-radius: 24px;
  border-top-right-radius: 24px;
  border-top: 1px solid var(--border-strong);
  padding: 10px 18px calc(20px + env(safe-area-inset-bottom, 0));
  animation: sheet-up 0.3s cubic-bezier(0.22, 1, 0.36, 1);
}
.sheet-handle {
  width: 40px;
  height: 4px;
  border-radius: 2px;
  background: rgba(255, 255, 255, 0.22);
  margin: 4px auto 14px;
}
.sheet-title {
  text-align: center;
  font-size: 16px;
  font-weight: 800;
  color: #fff;
  margin-bottom: 18px;
}
.amount-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
  margin-bottom: 16px;
}
.amount-cell {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
  padding: 14px 4px;
  border-radius: 14px;
  background: var(--bg-card);
  border: 1px solid var(--border);
  transition: all 0.18s ease;
}
.amount-cell.active {
  border-color: transparent;
  background: linear-gradient(180deg, rgba(255, 194, 75, 0.2), rgba(255, 45, 85, 0.18));
  box-shadow: 0 0 0 1.5px var(--grad-warm), 0 8px 20px rgba(255, 194, 75, 0.3);
}
.a-num {
  font-size: 22px;
  font-weight: 900;
  background: var(--grad-warm);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}
.a-unit {
  font-size: 11px;
  color: var(--text-secondary);
}
.a-tag {
  position: absolute;
  top: 6px;
  right: 6px;
  padding: 1px 6px;
  border-radius: var(--radius-pill);
  background: var(--grad-primary);
  font-size: 9px;
  color: #fff;
  font-weight: 700;
}
.sheet-balance {
  text-align: center;
  font-size: 12px;
  color: var(--text-secondary);
  margin-bottom: 14px;
}
.sheet-balance b {
  color: var(--gold);
  font-weight: 800;
}
.confirm-recharge {
  width: 100%;
  height: 46px;
  border-radius: 14px;
  background: var(--grad-warm);
  color: #fff;
  font-size: 15px;
  font-weight: 800;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 22px rgba(255, 194, 75, 0.4);
}
.confirm-recharge:disabled {
  opacity: 0.5;
  box-shadow: none;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.25s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
