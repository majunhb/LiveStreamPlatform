<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, watch } from 'vue'
import type { Danmaku } from '@/types'
import { makeAutoDanmaku } from '@/api'

const props = defineProps<{
  roomId: number
  seedDanmakus?: Danmaku[]
  paused?: boolean
}>()

const lanes = 5
const items = ref<Array<Danmaku & { lane: number; dur: number; key: string }>>([])
let timer: ReturnType<typeof setInterval> | null = null
let autoKey = 0

function push(dm: Danmaku) {
  const lane = Math.floor(Math.random() * lanes)
  const dur = 7 + Math.random() * 4 // 7-11s
  const key = `dm-${autoKey++}-${dm.id}`
  items.value.push({ ...dm, lane, dur, key })
  // cap length to avoid runaway memory
  if (items.value.length > 40) items.value.splice(0, items.value.length - 40)
  // remove after animation
  setTimeout(() => {
    const i = items.value.findIndex((d) => d.key === key)
    if (i !== -1) items.value.splice(i, 1)
  }, dur * 1000 + 300)
}

function startAuto() {
  stopAuto()
  timer = setInterval(() => {
    if (props.paused) return
    push(makeAutoDanmaku(props.roomId))
    if (Math.random() < 0.35) push(makeAutoDanmaku(props.roomId))
  }, 1400)
}
function stopAuto() {
  if (timer) {
    clearInterval(timer)
    timer = null
  }
}

watch(
  () => props.seedDanmakus,
  (list) => {
    if (list && list.length) {
      // stagger seed danmakus
      list.forEach((dm, i) => setTimeout(() => push(dm), i * 350))
    }
  },
  { immediate: true }
)

onMounted(() => startAuto())
onBeforeUnmount(() => stopAuto())

defineExpose({ push })
</script>

<template>
  <div class="danmaku-layer">
    <div
      v-for="d in items"
      :key="d.key"
      class="danmaku"
      :class="`lane-${d.lane}`"
      :style="{
        color: d.color,
        fontSize: d.fontSize + 'px',
        animationDuration: d.dur + 's',
        fontWeight: d.userId === 1 ? 800 : 600
      }"
    >
      <span class="name">{{ d.username }}：</span>{{ d.content }}
    </div>
  </div>
</template>

<style scoped lang="scss">
.danmaku-layer {
  position: absolute;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
  z-index: 50;
}
.danmaku {
  position: absolute;
  white-space: nowrap;
  left: 100%;
  text-shadow: 0 1px 4px rgba(0, 0, 0, 0.7), 0 0 8px rgba(0, 0, 0, 0.5);
  padding: 2px 8px;
  border-radius: var(--radius-pill);
  will-change: transform;
  animation-name: dm-scroll;
  animation-timing-function: linear;
  animation-fill-mode: forwards;
}
@each $i in 0, 1, 2, 3, 4 {
  .lane-#{$i} {
    top: calc(8% + #{$i} * 17%);
  }
}
@keyframes dm-scroll {
  from {
    transform: translateX(0);
  }
  to {
    transform: translateX(-120vw);
  }
}
.danmaku .name {
  opacity: 0.7;
  font-weight: 500;
  margin-right: 2px;
}
</style>
