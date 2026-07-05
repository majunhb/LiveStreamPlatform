<template>
  <view class="heart-burst-container">
    <view
      v-for="heart in hearts"
      :key="heart.id"
      class="heart-particle"
      :style="{
        left: `${heart.x}rpx`,
        bottom: `${heart.y}rpx`,
        animationDuration: `${heart.duration}s`,
        '--rotate': `${heart.rotate}deg`,
        '--scale': heart.scale,
        '--x-end': `${heart.xEnd}rpx`,
        '--y-end': `${heart.yEnd}rpx`
      }"
    >
      <text class="heart-icon">{{ heart.icon }}</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'

interface HeartParticle {
  id: string
  x: number
  y: number
  xEnd: number
  yEnd: number
  rotate: number
  scale: number
  duration: number
  icon: string
}

const hearts = ref<HeartParticle[]>([])
let idCounter = 0

const heartIcons = ['❤️', '💖', '💕', '💗', '💓', '💝']

function burst(x: number, y: number, count = 8) {
  for (let i = 0; i < count; i++) {
    const id = `heart_${idCounter++}`
    const angle = (Math.PI * 2 * i) / count + Math.random() * 0.5
    const distance = 80 + Math.random() * 120
    const xEnd = Math.cos(angle) * distance
    const yEnd = Math.sin(angle) * distance + 100

    const heart: HeartParticle = {
      id,
      x,
      y,
      xEnd,
      yEnd,
      rotate: Math.random() * 60 - 30,
      scale: 0.6 + Math.random() * 0.8,
      duration: 0.8 + Math.random() * 0.6,
      icon: heartIcons[Math.floor(Math.random() * heartIcons.length)]
    }

    hearts.value.push(heart)

    setTimeout(() => {
      const index = hearts.value.findIndex(h => h.id === id)
      if (index > -1) {
        hearts.value.splice(index, 1)
      }
    }, heart.duration * 1000 + 100)
  }
}

defineExpose({ burst })
</script>

<style lang="scss" scoped>
.heart-burst-container {
  position: absolute;
  inset: 0;
  pointer-events: none;
  overflow: hidden;
  z-index: 20;
}

.heart-particle {
  position: absolute;
  animation: heartFloat ease-out forwards;
  will-change: transform, opacity;
}

.heart-icon {
  font-size: 40rpx;
}

@keyframes heartFloat {
  0% {
    transform: translate(0, 0) scale(0) rotate(0deg);
    opacity: 1;
  }
  20% {
    transform: translate(calc(var(--x-end) * 0.3), calc(var(--y-end) * -0.3)) scale(var(--scale)) rotate(calc(var(--rotate) * 0.3));
    opacity: 1;
  }
  100% {
    transform: translate(var(--x-end), calc(var(--y-end) * -1)) scale(0.2) rotate(var(--rotate));
    opacity: 0;
  }
}
</style>
