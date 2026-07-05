<template>
  <view class="danmaku-layer">
    <view
      v-for="item in danmakuList"
      :key="item.id"
      class="danmaku-item"
      :style="{
        top: `${item.lane * 60 + 20}rpx`,
        color: item.color,
        animationDuration: `${item.duration}s`,
        animationDelay: `${item.delay}s`
      }"
    >
      <text class="danmaku-content">{{ item.content }}</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { getRandomDanmaku, getRandomColor, getRandomUser } from '@/mock/data'

interface DanmakuItem {
  id: string
  content: string
  color: string
  lane: number
  duration: number
  delay: number
}

const props = withDefaults(defineProps<{
  autoGenerate?: boolean
  intervalMin?: number
  intervalMax?: number
}>(), {
  autoGenerate: true,
  intervalMin: 1000,
  intervalMax: 3000
})

const danmakuList = ref<DanmakuItem[]>([])
let timer: ReturnType<typeof setTimeout> | null = null
let idCounter = 0

function pushDanmaku(content: string, color?: string) {
  const lane = Math.floor(Math.random() * 5)
  const duration = 8 + Math.random() * 6
  const delay = 0

  const item: DanmakuItem = {
    id: `danmaku_${idCounter++}`,
    content,
    color: color || getRandomColor(),
    lane,
    duration,
    delay
  }

  danmakuList.value.push(item)

  setTimeout(() => {
    const index = danmakuList.value.findIndex(d => d.id === item.id)
    if (index > -1) {
      danmakuList.value.splice(index, 1)
    }
  }, (duration + delay) * 1000)
}

function startAutoGenerate() {
  if (!props.autoGenerate) return

  function scheduleNext() {
    const interval = props.intervalMin + Math.random() * (props.intervalMax - props.intervalMin)
    timer = setTimeout(() => {
      const user = getRandomUser()
      pushDanmaku(`${user.nickname}: ${getRandomDanmaku()}`)
      scheduleNext()
    }, interval)
  }

  scheduleNext()
}

function stopAutoGenerate() {
  if (timer) {
    clearTimeout(timer)
    timer = null
  }
}

function sendDanmaku(content: string, color?: string) {
  pushDanmaku(content, color)
}

defineExpose({
  pushDanmaku,
  sendDanmaku,
  startAutoGenerate,
  stopAutoGenerate
})

onMounted(() => {
  startAutoGenerate()
})

onUnmounted(() => {
  stopAutoGenerate()
})
</script>

<style lang="scss" scoped>
.danmaku-layer {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  overflow: hidden;
  z-index: 10;
}

.danmaku-item {
  position: absolute;
  left: 100%;
  white-space: nowrap;
  font-size: 26rpx;
  text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.8), 0 0 8rpx rgba(0, 0, 0, 0.5);
  animation: danmaku-scroll linear forwards;
  will-change: transform;
}

.danmaku-content {
  font-weight: 500;
}

@keyframes danmaku-scroll {
  0% {
    transform: translateX(0);
  }
  100% {
    transform: translateX(-200vw);
  }
}
</style>
