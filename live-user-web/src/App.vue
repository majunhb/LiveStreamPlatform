<script setup lang="ts">
import { onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { api } from '@/api'

const store = useUserStore()

// Ensure wallet is loaded into the store when user is logged in.
onMounted(async () => {
  if (store.isLogin && !store.wallet) {
    try {
      const w = await api.getWallet()
      store.setWallet(w)
    } catch {
      /* ignore */
    }
  }
})
</script>

<template>
  <div class="phone-frame">
    <RouterView v-slot="{ Component }">
      <Transition name="fade-slide" mode="out-in">
        <component :is="Component" />
      </Transition>
    </RouterView>
  </div>
</template>
