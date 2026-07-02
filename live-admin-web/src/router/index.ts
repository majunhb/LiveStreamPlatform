import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { getToken } from '@/utils/auth'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'

NProgress.configure({ showSpinner: false })

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: () => import('@/layouts/DefaultLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '仪表盘', icon: 'Odometer' }
      },
      {
        path: 'users',
        name: 'UserList',
        component: () => import('@/views/UserList.vue'),
        meta: { title: '用户管理', icon: 'User' }
      },
      {
        path: 'live-rooms',
        name: 'LiveRoomList',
        component: () => import('@/views/LiveRoomList.vue'),
        meta: { title: '直播间管理', icon: 'VideoCamera' }
      },
      {
        path: 'videos',
        name: 'VideoList',
        component: () => import('@/views/VideoList.vue'),
        meta: { title: '短视频管理', icon: 'Film' }
      },
      {
        path: 'gifts',
        name: 'GiftList',
        component: () => import('@/views/GiftList.vue'),
        meta: { title: '礼物管理', icon: 'Present' }
      },
      {
        path: 'withdrawals',
        name: 'WithdrawList',
        component: () => import('@/views/WithdrawList.vue'),
        meta: { title: '提现审核', icon: 'Wallet' }
      },
      {
        path: 'admins',
        name: 'AdminList',
        component: () => import('@/views/AdminList.vue'),
        meta: { title: '管理员管理', icon: 'Lock' }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/Login.vue'),
    meta: { title: '404' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, _from, next) => {
  NProgress.start()
  document.title = `${to.meta.title || ''} - 直播平台管理后台`

  if (to.path === '/login') {
    next()
    return
  }

  const token = getToken()
  if (!token) {
    next('/login')
  } else {
    next()
  }
})

router.afterEach(() => {
  NProgress.done()
})

export default router
