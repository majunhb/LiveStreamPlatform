<script setup lang="ts">
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

const tabs = [
  { key: 'home', label: '首页', icon: '🏠', path: '/home' },
  { key: 'video', label: '视频', icon: '🎬', path: '/video' },
  { key: 'profile', label: '我的', icon: '👤', path: '/profile' }
]

const activeKey = computed(() => {
  const match = tabs.find((t) => route.path.startsWith(t.path))
  return match?.key ?? 'home'
})

function go(path: string) {
  if (route.path !== path) router.push(path)
}
</script>

<template>
  <nav class="bottom-nav glass">
    <button
      v-for="tab in tabs"
      :key="tab.key"
      class="tab"
      :class="{ active: activeKey === tab.key }"
      @click="go(tab.path)"
    >
      <span class="icon">{{ tab.icon }}</span>
      <span class="label">{{ tab.label }}</span>
      <span v-if="activeKey === tab.key" class="dot" />
    </button>
  </nav>
</template>

<style scoped lang="scss">
.bottom-nav {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  height: var(--nav-height);
  display: flex;
  align-items: stretch;
  padding-bottom: env(safe-area-inset-bottom, 0);
  border-top: 1px solid var(--border);
  background: rgba(10, 10, 15, 0.78);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  z-index: 1000;
}
.tab {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  position: relative;
  color: rgba(255, 255, 255, 0.45);
  transition: color 0.2s ease, transform 0.2s ease;
}
.tab .icon {
  font-size: 20px;
  line-height: 1;
  transition: transform 0.25s ease;
}
.tab .label {
  font-size: 11px;
  font-weight: 500;
  letter-spacing: 0.5px;
}
.tab.active {
  color: #fff;
}
.tab.active .icon {
  transform: translateY(-2px) scale(1.12);
  filter: drop-shadow(0 4px 10px rgba(255, 45, 85, 0.5));
}
.tab.active .label {
  background: var(--grad-primary);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
  font-weight: 700;
}
.tab .dot {
  position: absolute;
  bottom: 6px;
  width: 4px;
  height: 4px;
  border-radius: 50%;
  background: var(--grad-primary);
  box-shadow: 0 0 8px #ff2d55;
}
</style>
