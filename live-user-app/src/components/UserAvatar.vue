<template>
  <view
    class="user-avatar"
    :class="`size-${size}`"
    :style="{ background: gradient }"
    @tap="$emit('tap')"
  >
    <text class="avatar-text">{{ initial }}</text>
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { getGradientByName } from '@/utils/format'

const props = withDefaults(defineProps<{
  name: string
  size?: 'xs' | 'sm' | 'md' | 'lg' | 'xl'
}>(), {
  size: 'md'
})

defineEmits(['tap'])

const initial = computed(() => {
  if (!props.name) return '?'
  return props.name.charAt(0)
})

const gradient = computed(() => getGradientByName(props.name))
</script>

<style lang="scss" scoped>
.user-avatar {
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  flex-shrink: 0;
  overflow: hidden;
  font-weight: bold;

  &.size-xs {
    width: 36rpx;
    height: 36rpx;
    font-size: 16rpx;
  }

  &.size-sm {
    width: 48rpx;
    height: 48rpx;
    font-size: 20rpx;
  }

  &.size-md {
    width: 72rpx;
    height: 72rpx;
    font-size: 28rpx;
  }

  &.size-lg {
    width: 100rpx;
    height: 100rpx;
    font-size: 36rpx;
  }

  &.size-xl {
    width: 160rpx;
    height: 160rpx;
    font-size: 56rpx;
  }
}

.avatar-text {
  color: #fff;
  text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.2);
}
</style>
