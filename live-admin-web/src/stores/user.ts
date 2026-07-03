import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getToken, setToken, getUserInfo, setUserInfo, clearAuth } from '@/utils/auth'
import { post, get } from '@/utils/request'

export const useUserStore = defineStore('user', () => {
  const token = ref<string | null>(null)
  const userInfo = ref<any>(null)
  const roles = ref<string[]>([])

  function init() {
    token.value = getToken()
    userInfo.value = getUserInfo()
  }

  async function login(username: string, password: string) {
    const res: any = await post('/admin/auth/login', { username, password })
    const data = res.data
    token.value = data.token
    setToken(data.token)
    userInfo.value = data.admin
    setUserInfo(data.admin)
    roles.value = data.roles || ['admin']
    return data
  }

  async function getInfo() {
    const res: any = await get('/admin/auth/info')
    const data = res.data
    userInfo.value = data.admin
    setUserInfo(data.admin)
    roles.value = data.roles || ['admin']
    return data
  }

  function logout() {
    token.value = null
    userInfo.value = null
    roles.value = []
    clearAuth()
  }

  return {
    token,
    userInfo,
    roles,
    init,
    login,
    getInfo,
    logout
  }
})
