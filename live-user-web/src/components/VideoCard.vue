<script setup lang="ts">
import { computed } from 'vue'
import type { ShortVideo } from '@/types'
import { coverGradients, avatarStyle, firstChar } from '@/api'

const props = defineProps<{
  video: ShortVideo
  index?: number
}>()

const emit = defineEmits<{ (e: 'click', video: ShortVideo): void }>()

const coverBg = computed(() => {
  const idx = parseInt(props.video.id.replace(/\D/g, '') || '0', 10)
  return coverGradients[idx % coverGradients.length]
})
const avatarBg = computed(() => avatarStyle(props.video.authorName, idx(props.video.id)))
const initial = computed(() => firstChar(props.video.authorName))

function idx(id: string): number {
  return parseInt(id.replace(/\D/g, '') || '0', 10)
}

function fmt(n: number): string {
  if (n >= 10000) return (n / 10000).toFixed(1).replace(/\.0$/, '') + '万'
  return String(n)
}
const plays = computed(() => fmt(props.video.playCount))
const likes = computed(() => fmt(props.video.likeCount))
const duration = computed(() => {
  const m = Math.floor(props.video.duration / 60)
  const s = props.video.duration % 60
  return `${m}:${s.toString().padStart(2, '0')}`
})
const delay = computed(() => `${(props.index ?? 0) * 70}ms`)
</script>

<template>
  <button class="video-card" :style="{ animationDelay: delay }" @click="emit('click', video)">
    <div class="thumb" :style="{ background: coverBg }">
      <div class="thumb-glow" />
      <div class="play-mark">▶</div>
      <div class="duration">{{ duration }}</div>
    </div>
    <div class="body">
      <p class="title clamp-2">{{ video.title }}</p>
      <div class="foot">
        <div class="author">
          <span class="avatar" :style="{ background: avatarBg }">{{ initial }}</span>
          <span class="name ellipsis">{{ video.authorName }}</span>
        </div>
        <span class="stat">❤️ {{ likes }}</span>
      </div>
    </div>
  </button>
</template>

<style scoped lang="scss">
.video-card {
  display: flex;
  flex-direction: column;
  text-align: left;
  border-radius: 16px;
  overflow: hidden;
  background: var(--bg-card);
  border: 1px solid var(--border);
  opacity: 0;
  animation: rise-in 0.5s ease forwards;
  transition: transform 0.25s ease, box-shadow 0.25s ease;
}
.video-card:active {
  transform: scale(0.97);
  box-shadow: 0 12px 36px rgba(175, 82, 237, 0.25);
}
.thumb {
  position: relative;
  aspect-ratio: 9 / 14;
  overflow: hidden;
}
.thumb-glow {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(100% 70% at 70% 0%, rgba(255, 255, 255, 0.32), transparent 55%),
    radial-gradient(100% 80% at 0% 100%, rgba(0, 0, 0, 0.35), transparent 55%);
  mix-blend-mode: overlay;
}
.play-mark {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 44px;
  height: 44px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.35);
  backdrop-filter: blur(6px);
  color: #fff;
  font-size: 16px;
  padding-left: 3px;
  border: 1px solid rgba(255, 255, 255, 0.3);
}
.duration {
  position: absolute;
  bottom: 6px;
  right: 6px;
  padding: 2px 6px;
  border-radius: var(--radius-pill);
  background: rgba(0, 0, 0, 0.5);
  font-size: 10px;
  font-weight: 600;
  color: #fff;
}
.body {
  padding: 8px 10px 10px;
}
.title {
  font-size: 13px;
  font-weight: 600;
  line-height: 1.35;
  color: #fff;
  margin-bottom: 6px;
  min-height: 36px;
}
.foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 6px;
}
.author {
  display: flex;
  align-items: center;
  gap: 5px;
  min-width: 0;
}
.avatar {
  flex: none;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  font-weight: 800;
  color: #fff;
  border: 1px solid rgba(255, 255, 255, 0.4);
}
.name {
  font-size: 11px;
  color: var(--text-secondary);
  max-width: 90px;
}
.stat {
  font-size: 11px;
  color: var(--text-secondary);
  flex: none;
}
</style>
