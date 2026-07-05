import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { User, Wallet } from '@/types'

const TOKEN_KEY = 'live_token'
const USER_KEY = 'live_user'
const WALLET_KEY = 'live_wallet'

function readJSON<T>(key: string, fallback: T): T {
  try {
    const raw = localStorage.getItem(key)
    return raw ? (JSON.parse(raw) as T) : fallback
  } catch {
    return fallback
  }
}

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem(TOKEN_KEY) || '')
  const user = ref<User | null>(readJSON<User | null>(USER_KEY, null))
  const wallet = ref<Wallet | null>(readJSON<Wallet | null>(WALLET_KEY, null))

  const isLogin = computed(() => !!token.value && !!user.value)
  const coinBalance = computed(() => wallet.value?.balance ?? user.value?.coinBalance ?? 0)

  function setAuth(t: string, u: User) {
    token.value = t
    user.value = u
    localStorage.setItem(TOKEN_KEY, t)
    localStorage.setItem(USER_KEY, JSON.stringify(u))
  }

  function setUser(u: User) {
    user.value = u
    localStorage.setItem(USER_KEY, JSON.stringify(u))
  }

  function setWallet(w: Wallet) {
    wallet.value = w
    localStorage.setItem(WALLET_KEY, JSON.stringify(w))
  }

  function addCoins(amount: number) {
    if (wallet.value) {
      const next: Wallet = {
        ...wallet.value,
        balance: wallet.value.balance + amount,
        totalRecharge: amount > 0 ? wallet.value.totalRecharge + amount : wallet.value.totalRecharge,
        updateTime: new Date().toISOString()
      }
      setWallet(next)
    }
    if (user.value) {
      const nextUser: User = { ...user.value, coinBalance: coinBalance.value }
      setUser(nextUser)
    }
  }

  function spendCoins(amount: number): boolean {
    if (coinBalance.value < amount) return false
    if (wallet.value) {
      const next: Wallet = {
        ...wallet.value,
        balance: wallet.value.balance - amount,
        totalConsume: wallet.value.totalConsume + amount,
        updateTime: new Date().toISOString()
      }
      setWallet(next)
    }
    if (user.value) {
      setUser({ ...user.value, coinBalance: coinBalance.value })
    }
    return true
  }

  function logout() {
    token.value = ''
    user.value = null
    wallet.value = null
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(USER_KEY)
    localStorage.removeItem(WALLET_KEY)
  }

  return {
    token,
    user,
    wallet,
    isLogin,
    coinBalance,
    setAuth,
    setUser,
    setWallet,
    addCoins,
    spendCoins,
    logout
  }
})
