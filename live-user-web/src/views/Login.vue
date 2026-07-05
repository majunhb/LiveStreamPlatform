<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { api } from '@/api'
import { useUserStore } from '@/stores/user'
import LoadingSpinner from '@/components/LoadingSpinner.vue'

const router = useRouter()
const route = useRoute()
const store = useUserStore()

const mode = ref<'login' | 'register'>('login')
const username = ref('demo')
const password = ref('123456')
const confirm = ref('')
const loading = ref(false)
const errorMsg = ref('')

const canSubmit = computed(() => {
  if (!username.value.trim() || !password.value) return false
  if (mode.value === 'register' && password.value !== confirm.value) return false
  if (mode.value === 'register' && password.value.length < 6) return false
  return true
})

function switchMode(m: 'login' | 'register') {
  mode.value = m
  errorMsg.value = ''
}

async function submit() {
  if (!canSubmit.value || loading.value) return
  errorMsg.value = ''
  loading.value = true
  try {
    const res =
      mode.value === 'login'
        ? await api.login(username.value, password.value)
        : await api.register(username.value, password.value)
    store.setAuth(res.token, res.user)
    const wallet = await api.getWallet()
    store.setWallet(wallet)
    const redirect = (route.query.redirect as string) || '/home'
    router.replace(redirect)
  } catch (e: any) {
    errorMsg.value = e?.message || '操作失败，请重试'
  } finally {
    loading.value = false
  }
}

async function guestLogin() {
  errorMsg.value = ''
  loading.value = true
  try {
    const res = await api.guestLogin()
    store.setAuth(res.token, res.user)
    const wallet = await api.getWallet()
    store.setWallet(wallet)
    router.replace('/home')
  } catch (e: any) {
    errorMsg.value = e?.message || '游客登录失败'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <!-- animated floating shapes -->
    <div class="shapes">
      <span class="shape s1" />
      <span class="shape s2" />
      <span class="shape s3" />
      <span class="shape s4" />
      <span class="shape s5" />
    </div>
    <div class="grid-overlay" />

    <div class="content">
      <div class="brand">
        <div class="logo">LIVE</div>
        <h1 class="title">LIVE<span class="grad">星球</span></h1>
        <p class="subtitle">沉浸式直播 · 每一帧都滚烫</p>
      </div>

      <div class="card glass">
        <div class="tabs">
          <button class="tab" :class="{ active: mode === 'login' }" @click="switchMode('login')">
            登录
          </button>
          <button class="tab" :class="{ active: mode === 'register' }" @click="switchMode('register')">
            注册
          </button>
          <span class="tab-indicator" :class="{ right: mode === 'register' }" />
        </div>

        <div class="form">
          <div class="field">
            <span class="icon">👤</span>
            <input v-model="username" type="text" placeholder="请输入用户名" />
          </div>
          <div class="field">
            <span class="icon">🔒</span>
            <input v-model="password" type="password" placeholder="请输入密码" />
          </div>
          <div v-if="mode === 'register'" class="field">
            <span class="icon">🔐</span>
            <input v-model="confirm" type="password" placeholder="请确认密码" />
          </div>

          <Transition name="fade-slide">
            <p v-if="errorMsg" class="error">⚠ {{ errorMsg }}</p>
          </Transition>

          <button class="submit" :class="{ loading }" :disabled="!canSubmit || loading" @click="submit">
            <LoadingSpinner v-if="loading" :size="18" />
            <span v-else>{{ mode === 'login' ? '登 录' : '注 册' }}</span>
          </button>

          <div class="divider"><span>或</span></div>

          <button class="guest" :disabled="loading" @click="guestLogin">
            🚀 游客登录（体验 demo 账号）
          </button>

          <p class="hint">
            体验账号：<b>demo</b> / <b>123456</b>
          </p>
        </div>
      </div>

      <p class="footer-tip">登录即代表同意《LIVE星球用户协议》与《隐私政策》</p>
    </div>
  </div>
</template>

<style scoped lang="scss">
.login-page {
  position: absolute;
  inset: 0;
  overflow: hidden;
  background:
    radial-gradient(120% 80% at 50% -10%, rgba(255, 45, 85, 0.35), transparent 55%),
    radial-gradient(120% 80% at 0% 110%, rgba(175, 82, 237, 0.35), transparent 55%),
    radial-gradient(120% 80% at 100% 80%, rgba(0, 212, 255, 0.2), transparent 55%),
    #0a0a0f;
  display: flex;
  flex-direction: column;
}
.grid-overlay {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(255, 255, 255, 0.04) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.04) 1px, transparent 1px);
  background-size: 36px 36px;
  mask-image: radial-gradient(circle at 50% 40%, #000 30%, transparent 75%);
  -webkit-mask-image: radial-gradient(circle at 50% 40%, #000 30%, transparent 75%);
  pointer-events: none;
}
.shapes {
  position: absolute;
  inset: 0;
  pointer-events: none;
  overflow: hidden;
}
.shape {
  position: absolute;
  border-radius: 50%;
  filter: blur(8px);
  opacity: 0.55;
  animation: floaty 14s ease-in-out infinite;
}
.s1 {
  width: 120px; height: 120px; top: 12%; left: -30px;
  background: radial-gradient(circle, #ff2d55, transparent 70%);
}
.s2 {
  width: 90px; height: 90px; top: 55%; left: 8%;
  background: radial-gradient(circle, #00d4ff, transparent 70%);
  animation-delay: -3s;
}
.s3 {
  width: 140px; height: 140px; top: 70%; right: -40px;
  background: radial-gradient(circle, #af52ed, transparent 70%);
  animation-delay: -6s;
}
.s4 {
  width: 70px; height: 70px; top: 20%; right: 14%;
  background: radial-gradient(circle, #ffc24b, transparent 70%);
  animation-delay: -9s;
}
.s5 {
  width: 100px; height: 100px; top: 40%; left: 45%;
  background: radial-gradient(circle, #ff6b9d, transparent 70%);
  animation-delay: -4s;
}
@keyframes floaty {
  0%, 100% { transform: translateY(0) translateX(0) scale(1); }
  33% { transform: translateY(-22px) translateX(14px) scale(1.08); }
  66% { transform: translateY(16px) translateX(-12px) scale(0.94); }
}

.content {
  position: relative;
  z-index: 2;
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 24px 22px;
}
.brand {
  text-align: center;
  margin-bottom: 28px;
}
.logo {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 64px;
  height: 64px;
  border-radius: 20px;
  font-family: var(--font-display);
  font-weight: 800;
  font-size: 20px;
  color: #fff;
  background: var(--grad-primary);
  box-shadow: 0 12px 32px rgba(255, 45, 85, 0.5);
  margin-bottom: 14px;
  letter-spacing: 1px;
  animation: pop-in 0.6s ease;
}
.title {
  font-size: 32px;
  font-weight: 900;
  letter-spacing: 1px;
  margin-bottom: 6px;
}
.title .grad {
  background: var(--grad-primary);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}
.subtitle {
  font-size: 13px;
  color: var(--text-secondary);
  letter-spacing: 2px;
}

.card {
  border-radius: 24px;
  padding: 20px 18px 22px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(24px);
  -webkit-backdrop-filter: blur(24px);
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.5);
  animation: rise-in 0.6s ease 0.1s both;
}

.tabs {
  position: relative;
  display: flex;
  background: rgba(0, 0, 0, 0.3);
  border-radius: var(--radius-pill);
  padding: 4px;
  margin-bottom: 20px;
}
.tab {
  flex: 1;
  padding: 9px 0;
  font-size: 14px;
  font-weight: 700;
  color: var(--text-secondary);
  border-radius: var(--radius-pill);
  position: relative;
  z-index: 2;
  transition: color 0.25s ease;
}
.tab.active {
  color: #fff;
}
.tab-indicator {
  position: absolute;
  top: 4px;
  left: 4px;
  width: calc(50% - 4px);
  height: calc(100% - 8px);
  border-radius: var(--radius-pill);
  background: var(--grad-primary);
  box-shadow: 0 6px 16px rgba(255, 45, 85, 0.45);
  transition: transform 0.3s cubic-bezier(0.22, 1, 0.36, 1);
  z-index: 1;
}
.tab-indicator.right {
  transform: translateX(100%);
}

.form {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.field {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 14px;
  border-radius: 14px;
  background: rgba(0, 0, 0, 0.28);
  border: 1px solid var(--border);
  transition: border-color 0.2s ease, background 0.2s ease;
}
.field:focus-within {
  border-color: var(--purple);
  background: rgba(175, 82, 237, 0.08);
}
.field .icon {
  font-size: 16px;
  opacity: 0.8;
}
.field input {
  flex: 1;
  font-size: 14px;
  color: #fff;
}
.field input::placeholder {
  color: var(--text-muted);
}

.error {
  font-size: 12px;
  color: #ff6b6b;
  padding: 4px 2px;
}

.submit {
  margin-top: 4px;
  height: 46px;
  border-radius: 14px;
  background: var(--grad-primary);
  color: #fff;
  font-size: 15px;
  font-weight: 800;
  letter-spacing: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 10px 24px rgba(255, 45, 85, 0.45);
  transition: transform 0.18s ease, opacity 0.18s ease;
}
.submit:active {
  transform: scale(0.98);
}
.submit:disabled {
  opacity: 0.5;
  box-shadow: none;
  cursor: not-allowed;
}
.submit.loading {
  opacity: 0.85;
}

.divider {
  display: flex;
  align-items: center;
  gap: 12px;
  color: var(--text-muted);
  font-size: 12px;
  margin: 6px 0 2px;
}
.divider::before,
.divider::after {
  content: '';
  flex: 1;
  height: 1px;
  background: var(--border);
}

.guest {
  height: 44px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid var(--border-strong);
  color: #fff;
  font-size: 14px;
  font-weight: 700;
  transition: transform 0.18s ease, background 0.18s ease;
}
.guest:active {
  transform: scale(0.98);
  background: rgba(255, 255, 255, 0.1);
}

.hint {
  text-align: center;
  font-size: 12px;
  color: var(--text-muted);
  margin-top: 4px;
}
.hint b {
  color: var(--gold);
  font-weight: 700;
}

.footer-tip {
  position: relative;
  z-index: 2;
  text-align: center;
  font-size: 11px;
  color: var(--text-muted);
  padding: 16px 0 8px;
}
</style>
