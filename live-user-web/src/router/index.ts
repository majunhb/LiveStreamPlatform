import { createRouter, createWebHashHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = createRouter({
  history: createWebHashHistory(),
  routes: [
    { path: '/', redirect: '/home' },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/Login.vue'),
      meta: { public: true }
    },
    {
      path: '/home',
      name: 'home',
      component: () => import('@/views/Home.vue')
    },
    {
      path: '/room/:id',
      name: 'room',
      component: () => import('@/views/LiveRoom.vue'),
      meta: { public: true }
    },
    {
      path: '/video',
      name: 'video',
      component: () => import('@/views/VideoFeed.vue')
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('@/views/Profile.vue')
    }
  ]
})

router.beforeEach((to) => {
  const store = useUserStore()
  if (!to.meta.public && !store.isLogin) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }
  if (to.name === 'login' && store.isLogin) {
    return { name: 'home' }
  }
  return true
})

export default router
