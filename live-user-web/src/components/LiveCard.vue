<script setup lang="ts">
import { computed } from 'vue'
import type { LiveRoom } from '@/types'
import { coverGradients, avatarStyle, firstChar } from '@/api'

const props = defineProps<{
  room: LiveRoom
  index?: number
}>()

const emit = defineEmits<{ (e: 'click', room: LiveRoom): void }>()

const coverBg = computed(() => coverGradients[(props.room.id - 1) % coverGradients.length])
const avatarBg = computed(() => avatarStyle(props.room.anchorName, props.room.id))
const initial = computed(() => firstChar(props.room.anchorName))

function fmt(n: number): string {
  if (n >= 10000) return (n / 10000).toFixed(1).replace(/\.0$/, '') + '万'
  return String(n)
}
const viewers = computed(() => fmt(props.room.viewerCount))
const delay = computed(() => `${(props.index ?? 0) * 70}ms`)
</script>

<template>
  <button class="live-card" :style="{ animationDelay: delay }" @click="emit('click', room)">
    <div class="cover" :style="{ background: coverBg }">
      <div class="cover-glow" />
      <div class="live-badge">
        <span class="dot" />
        <span>LIVE</span>
      </div>
      <div class="viewers">
        <span class="eye">👁</span>
        <span>{{ viewers }}</span>
      </div>
      <div v-if="room.isRecommend === 1" class="rec-tag">推荐</div>
      <div class="category-chip">{{ room.category }}</div>
    </div>
    <div class="meta">
      <div class="avatar" :style="{ background: avatarBg }">{{ initial }}</div>
      <div class="info">
        <p class="title clamp-2">{{ room.title }}</p>
        <p class="anchor ellipsis">{{ room.anchorName }}</p>
      </div>
    </div>
  </button>
</template>

<style scoped lang="scss">
.live-card {
  display: flex;
  flex-direction: column;
  text-align: left;
  border-radius: 18px;
  overflow: hidden;
  background: var(--bg-card);
  border: 1px solid var(--border);
  backdrop-filter: blur(14px);
  -webkit-backdrop-filter: blur(14px);
  opacity: 0;
  animation: rise-in 0.5s ease forwards;
  transition: transform 0.25s ease, box-shadow 0.25s ease, border-color 0.25s ease;
}
.live-card:active {
  transform: scale(0.97);
  border-color: var(--border-strong);
  box-shadow: 0 12px 36px rgba(255, 45, 85, 0.25);
}
.cover {
  position: relative;
  aspect-ratio: 3 / 4;
  overflow: hidden;
}
.cover-glow {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(120% 80% at 80% 10%, rgba(255, 255, 255, 0.35), transparent 50%),
    radial-gradient(120% 90% at 0% 100%, rgba(0, 0, 0, 0.35), transparent 50%);
  mix-blend-mode: overlay;
}
.live-badge {
  position: absolute;
  top: 8px;
  left: 8px;
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 3px 8px 3px 7px;
  border-radius: var(--radius-pill);
  background: var(--grad-live);
  font-size: 10px;
  font-weight: 800;
  letter-spacing: 1px;
  color: #fff;
  box-shadow: 0 4px 14px rgba(255, 45, 85, 0.5);
  animation: pulse-live 1.6s ease-in-out infinite;
}
.live-badge .dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #fff;
  box-shadow: 0 0 6px #fff;
}
.viewers {
  position: absolute;
  bottom: 8px;
  left: 8px;
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 3px 8px;
  border-radius: var(--radius-pill);
  background: rgba(0, 0, 0, 0.45);
  backdrop-filter: blur(6px);
  font-size: 11px;
  font-weight: 600;
  color: #fff;
}
.viewers .eye {
  font-size: 10px;
}
.rec-tag {
  position: absolute;
  top: 8px;
  right: 8px;
  padding: 3px 8px;
  border-radius: var(--radius-pill);
  background: linear-gradient(135deg, #ffc24b, #ff2d55);
  font-size: 10px;
  font-weight: 700;
  color: #fff;
  box-shadow: 0 4px 12px rgba(255, 194, 75, 0.45);
}
.category-chip {
  position: absolute;
  bottom: 8px;
  right: 8px;
  padding: 3px 8px;
  border-radius: var(--radius-pill);
  background: rgba(255, 255, 255, 0.14);
  backdrop-filter: blur(6px);
  font-size: 10px;
  font-weight: 600;
  color: #fff;
  border: 1px solid rgba(255, 255, 255, 0.18);
}
.meta {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px;
}
.avatar {
  flex: none;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 800;
  color: #fff;
  border: 1.5px solid rgba(255, 255, 255, 0.4);
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.3);
}
.info {
  flex: 1;
  min-width: 0;
}
.title {
  font-size: 13px;
  font-weight: 600;
  line-height: 1.35;
  color: #fff;
  margin-bottom: 3px;
}
.anchor {
  font-size: 11px;
  color: var(--text-secondary);
}
</style>
