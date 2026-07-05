<script setup lang="ts">
defineProps<{
  size?: number
  label?: string
}>()
</script>

<template>
  <div class="loading-spinner" :style="{ '--sz': (size || 36) + 'px' }">
    <div class="ring" />
    <span v-if="label" class="label">{{ label }}</span>
  </div>
</template>

<style scoped lang="scss">
.loading-spinner {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  justify-content: center;
  padding: 24px;
}
.ring {
  width: var(--sz);
  height: var(--sz);
  border-radius: 50%;
  border: 3px solid rgba(255, 255, 255, 0.12);
  border-top-color: #ff2d55;
  border-right-color: #af52ed;
  animation: spin 0.8s linear infinite;
  filter: drop-shadow(0 0 8px rgba(175, 82, 237, 0.5));
}
@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
.label {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
  letter-spacing: 0.5px;
}
</style>
