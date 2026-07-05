import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { User } from '@/mock/data'

export const useUserStore = defineStore('user', () => {
  const token = ref<string>('')
  const userInfo = ref<User | null>(null)
  const coins = ref<number>(0)

  const isLoggedIn = computed(() => !!token.value)

  function initFromStorage() {
    try {
      const savedToken = uni.getStorageSync('token')
      const savedUser = uni.getStorageSync('userInfo')
      const savedCoins = uni.getStorageSync('coins')
      if (savedToken) token.value = savedToken
      if (savedUser) userInfo.value = JSON.parse(savedUser)
      if (savedCoins) coins.value = savedCoins
    } catch (e) {
      console.error('Failed to load user data from storage', e)
    }
  }

  function setLogin(tokenValue: string, user: User, coinAmount = 1280) {
    token.value = tokenValue
    userInfo.value = user
    coins.value = coinAmount
    uni.setStorageSync('token', tokenValue)
    uni.setStorageSync('userInfo', JSON.stringify(user))
    uni.setStorageSync('coins', coinAmount)
  }

  function updateUserInfo(user: Partial<User>) {
    if (userInfo.value) {
      userInfo.value = { ...userInfo.value, ...user }
      uni.setStorageSync('userInfo', JSON.stringify(userInfo.value))
    }
  }

  function setCoins(amount: number) {
    coins.value = amount
    uni.setStorageSync('coins', amount)
  }

  function addCoins(amount: number) {
    coins.value += amount
    uni.setStorageSync('coins', coins.value)
  }

  function deductCoins(amount: number): boolean {
    if (coins.value >= amount) {
      coins.value -= amount
      uni.setStorageSync('coins', coins.value)
      return true
    }
    return false
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    coins.value = 0
    uni.removeStorageSync('token')
    uni.removeStorageSync('userInfo')
    uni.removeStorageSync('coins')
  }

  function checkAuth(): boolean {
    if (!isLoggedIn.value) {
      uni.navigateTo({ url: '/pages/auth/login' })
      return false
    }
    return true
  }

  return {
    token,
    userInfo,
    coins,
    isLoggedIn,
    initFromStorage,
    setLogin,
    updateUserInfo,
    setCoins,
    addCoins,
    deductCoins,
    logout,
    checkAuth
  }
})
