<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import type { Gift } from '@/types'
import { api } from '@/api'
import { useUserStore } from '@/stores/user'
import LoadingSpinner from './LoadingSpinner.vue'

const props = defineProps<{
  visible: boolean
}>()
const emit = defineEmits<{
  (e: 'close'): void
  (e: 'send', gift: Gift, count: number): void
}>()

const store = useUserStore()
const gifts = ref<Gift[]>([])
const loading = ref(false)
const selectedId = ref<number | null>(null)
const quantity = ref(1)

const balance = computed(() => store.coinBalance)
const selectedGift = computed(() => gifts.value.find((g) => g.id === selectedId.value) || null)
const totalCost = computed(() => (selectedGift.value ? selectedGift.value.price * quantity.value : 0))
const canAfford = computed(() => selectedGift.value !== null && balance.value >= totalCost.value)

const qtyOptions = [1, 6, 9, 18, 66, 188, 520, 1314]

async function load() {
  loading.value = true
  try {
    gifts.value = await api.getGiftList()
    if (gifts.value.length && selectedId.value === null) selectedId.value = gifts.value[0].id
  } finally {
    loading.value = false
  }
}

function pick(g: Gift) {
  selectedId.value = g.id
}

function setQty(q: number) {
  quantity.value = q
}

function confirm() {
  if (!selectedGift.value || !canAfford.value) return
  emit('send', selectedGift.value, quantity.value)
  // reset quantity for next pick
  quantity.value = 1
}

onMounted(load)

function stop() {}
</script>

<template>
  <Transition name="fade">
    <div v-if="visible" class="gift-mask" @click.self="emit('close')">
      <div class="gift-sheet" @click="stop">
        <div class="handle" />
        <div class="sheet-head">
          <span class="title">礼物商城</span>
          <span class="balance">
            <span class="coin">🪙</span>
            <span class="num">{{ balance.toLocaleString() }}</span>
            <span class="recharge" @click="$emit('close')">充值</span>
          </span>
        </div>

        <div v-if="loading" class="loading-wrap">
          <LoadingSpinner label="加载礼物中..." />
        </div>

        <div v-else class="gift-grid">
          <button
            v-for="g in gifts"
            :key="g.id"
            class="gift-cell"
            :class="{ active: selectedId === g.id }"
            @click="pick(g)"
          >
            <span class="icon">{{ g.icon }}</span>
            <span class="name">{{ g.name }}</span>
            <span class="price">🪙 {{ g.price }}</span>
          </button>
        </div>

        <div class="qty-row">
          <span class="qty-label">数量</span>
          <div class="qty-options no-scrollbar">
            <button
              v-for="q in qtyOptions"
              :key="q"
              class="qty-chip"
              :class="{ active: quantity === q }"
              @click="setQty(q)"
            >
              {{ q }}
            </button>
          </div>
        </div>

        <div class="footer">
          <div class="cost">
            <span class="label">合计</span>
            <span class="value">🪙 {{ totalCost.toLocaleString() }}</span>
          </div>
          <button
            class="send-btn"
            :class="{ disabled: !canAfford || !selectedGift }"
            :disabled="!canAfford || !selectedGift"
            @click="confirm"
          >
            {{ !selectedGift ? '请选择礼物' : canAfford ? '赠送' : '余额不足' }}
          </button>
        </div>
      </div>
    </div>
  </Transition>
</template>

<style scoped lang="scss">
.gift-mask {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.55);
  z-index: 900;
  display: flex;
  align-items: flex-end;
}
.gift-sheet {
  width: 100%;
  background: linear-gradient(180deg, rgba(28, 28, 38, 0.96), rgba(14, 14, 20, 0.98));
  border-top-left-radius: 24px;
  border-top-right-radius: 24px;
  border-top: 1px solid var(--border-strong);
  padding: 10px 16px calc(16px + env(safe-area-inset-bottom, 0));
  animation: sheet-up 0.32s cubic-bezier(0.22, 1, 0.36, 1);
}
.handle {
  width: 40px;
  height: 4px;
  border-radius: 2px;
  background: rgba(255, 255, 255, 0.22);
  margin: 4px auto 12px;
}
.sheet-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 14px;
}
.title {
  font-size: 16px;
  font-weight: 800;
  color: #fff;
  letter-spacing: 0.5px;
}
.balance {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 13px;
  color: var(--gold);
  font-weight: 700;
}
.balance .num {
  color: #fff;
}
.balance .recharge {
  margin-left: 6px;
  padding: 2px 9px;
  border-radius: var(--radius-pill);
  background: var(--grad-warm);
  font-size: 11px;
  color: #fff;
  font-weight: 700;
}
.loading-wrap {
  padding: 30px 0;
}
.gift-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;
}
.gift-cell {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 3px;
  padding: 12px 4px 9px;
  border-radius: 14px;
  background: var(--bg-card);
  border: 1px solid var(--border);
  transition: transform 0.18s ease, border-color 0.18s ease, background 0.18s ease;
}
.gift-cell .icon {
  font-size: 30px;
  line-height: 1;
  filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.4));
}
.gift-cell .name {
  font-size: 12px;
  color: #fff;
  font-weight: 600;
  margin-top: 4px;
}
.gift-cell .price {
  font-size: 11px;
  color: var(--gold);
  font-weight: 700;
}
.gift-cell.active {
  border-color: transparent;
  background: linear-gradient(180deg, rgba(255, 45, 85, 0.22), rgba(175, 82, 237, 0.18));
  box-shadow: 0 0 0 1.5px var(--grad-primary), 0 8px 22px rgba(255, 45, 85, 0.3);
  transform: translateY(-2px);
}
.qty-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 16px;
}
.qty-label {
  font-size: 13px;
  color: var(--text-secondary);
  flex: none;
}
.qty-options {
  display: flex;
  gap: 8px;
  overflow-x: auto;
  flex: 1;
}
.qty-chip {
  flex: none;
  padding: 6px 14px;
  border-radius: var(--radius-pill);
  background: var(--bg-card);
  border: 1px solid var(--border);
  font-size: 13px;
  font-weight: 600;
  color: var(--text-secondary);
  transition: all 0.18s ease;
}
.qty-chip.active {
  background: var(--grad-primary);
  color: #fff;
  border-color: transparent;
  box-shadow: 0 4px 14px rgba(255, 45, 85, 0.4);
}
.footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-top: 16px;
}
.cost .label {
  font-size: 12px;
  color: var(--text-secondary);
  display: block;
  margin-bottom: 2px;
}
.cost .value {
  font-size: 18px;
  font-weight: 800;
  color: var(--gold);
}
.send-btn {
  padding: 11px 28px;
  border-radius: var(--radius-pill);
  background: var(--grad-primary);
  color: #fff;
  font-size: 15px;
  font-weight: 800;
  letter-spacing: 1px;
  box-shadow: 0 8px 22px rgba(255, 45, 85, 0.45);
  transition: transform 0.18s ease, opacity 0.18s ease;
}
.send-btn:active {
  transform: scale(0.95);
}
.send-btn.disabled {
  background: rgba(255, 255, 255, 0.1);
  box-shadow: none;
  color: var(--text-muted);
  cursor: not-allowed;
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
