<template>
  <view class="login-page">
    <view class="bg-gradient"></view>
    <view class="login-content">
      <view class="logo-section">
        <view class="logo-icon">🎬</view>
        <text class="app-name">LIVE星球</text>
        <text class="app-desc">发现精彩直播</text>
      </view>

      <view class="tab-row">
        <view
          class="tab-item"
          :class="{ active: activeTab === 'login' }"
          @tap="activeTab = 'login'"
        >
          <text class="tab-text">登录</text>
        </view>
        <view
          class="tab-item"
          :class="{ active: activeTab === 'register' }"
          @tap="activeTab = 'register'"
        >
          <text class="tab-text">注册</text>
        </view>
      </view>

      <view v-if="activeTab === 'login'" class="form-section">
        <view class="input-group">
          <text class="input-icon">👤</text>
          <input
            class="form-input"
            v-model="loginForm.username"
            placeholder="请输入用户名"
            placeholder-class="input-placeholder"
          />
        </view>
        <view class="input-group">
          <text class="input-icon">🔒</text>
          <input
            class="form-input"
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            placeholder-class="input-placeholder"
          />
        </view>
        <view class="login-btn" :class="{ loading: loading }" @tap="handleLogin">
          <text class="btn-text">{{ loading ? '登录中...' : '登 录' }}</text>
        </view>
        <view class="demo-hint">
          <text class="hint-text">💡 演示账号: demo / 123456</text>
        </view>
      </view>

      <view v-else class="form-section">
        <view class="input-group">
          <text class="input-icon">👤</text>
          <input
            class="form-input"
            v-model="registerForm.username"
            placeholder="请输入用户名"
            placeholder-class="input-placeholder"
          />
        </view>
        <view class="input-group">
          <text class="input-icon">📱</text>
          <input
            class="form-input"
            v-model="registerForm.phone"
            type="number"
            placeholder="请输入手机号"
            placeholder-class="input-placeholder"
          />
        </view>
        <view class="input-group">
          <text class="input-icon">🔒</text>
          <input
            class="form-input"
            v-model="registerForm.password"
            type="password"
            placeholder="请设置密码"
            placeholder-class="input-placeholder"
          />
        </view>
        <view class="input-group">
          <text class="input-icon">🔐</text>
          <input
            class="form-input"
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请确认密码"
            placeholder-class="input-placeholder"
          />
        </view>
        <view class="login-btn" :class="{ loading: loading }" @tap="handleRegister">
          <text class="btn-text">{{ loading ? '注册中...' : '注 册' }}</text>
        </view>
      </view>

      <view class="guest-login" @tap="handleGuestLogin">
        <text class="guest-text">游客体验 →</text>
      </view>

      <view class="third-party">
        <text class="third-title">其他登录方式</text>
        <view class="third-icons">
          <view class="third-icon">
            <text class="icon">📱</text>
          </view>
          <view class="third-icon">
            <text class="icon">💬</text>
          </view>
          <view class="third-icon">
            <text class="icon">🐧</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { login, register, guestLogin } from '@/api/auth'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const activeTab = ref('login')
const loading = ref(false)

const loginForm = reactive({
  username: 'demo',
  password: '123456'
})

const registerForm = reactive({
  username: '',
  phone: '',
  password: '',
  confirmPassword: ''
})

async function handleLogin() {
  if (!loginForm.username || !loginForm.password) {
    uni.showToast({ title: '请输入用户名和密码', icon: 'none' })
    return
  }
  loading.value = true
  try {
    const res: any = await login({
      username: loginForm.username,
      password: loginForm.password
    })
    if (res.code === 200) {
      userStore.setLogin(res.data.token, res.data.user, 1280)
      uni.showToast({ title: '登录成功', icon: 'success' })
      setTimeout(() => {
        uni.switchTab({ url: '/pages/index/index' })
      }, 500)
    } else {
      uni.showToast({ title: res.message, icon: 'none' })
    }
  } catch (e) {
    uni.showToast({ title: '登录失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

async function handleRegister() {
  if (!registerForm.username || !registerForm.password || !registerForm.phone) {
    uni.showToast({ title: '请填写完整信息', icon: 'none' })
    return
  }
  if (registerForm.password !== registerForm.confirmPassword) {
    uni.showToast({ title: '两次密码不一致', icon: 'none' })
    return
  }
  loading.value = true
  try {
    const res: any = await register({
      username: registerForm.username,
      password: registerForm.password,
      phone: registerForm.phone
    })
    if (res.code === 200) {
      userStore.setLogin(res.data.token, res.data.user, 100)
      uni.showToast({ title: '注册成功', icon: 'success' })
      setTimeout(() => {
        uni.switchTab({ url: '/pages/index/index' })
      }, 500)
    } else {
      uni.showToast({ title: res.message, icon: 'none' })
    }
  } catch (e) {
    uni.showToast({ title: '注册失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

async function handleGuestLogin() {
  loading.value = true
  try {
    const res: any = await guestLogin()
    if (res.code === 200) {
      userStore.setLogin(res.data.token, res.data.user, 100)
      uni.showToast({ title: '登录成功', icon: 'success' })
      setTimeout(() => {
        uni.switchTab({ url: '/pages/index/index' })
      }, 500)
    }
  } catch (e) {
    uni.showToast({ title: '登录失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  position: relative;
  overflow: hidden;
  background: #0a0a0f;
}

.bg-gradient {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 60vh;
  background: linear-gradient(180deg, rgba(255, 45, 85, 0.3) 0%, rgba(175, 82, 237, 0.2) 50%, transparent 100%);
}

.login-content {
  position: relative;
  z-index: 2;
  padding: 80rpx 48rpx;
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  box-sizing: border-box;
}

.logo-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 80rpx;
  padding-top: 40rpx;
}

.logo-icon {
  font-size: 100rpx;
  margin-bottom: 24rpx;
  filter: drop-shadow(0 8rpx 24rpx rgba(255, 45, 85, 0.4));
}

.app-name {
  font-size: 52rpx;
  font-weight: bold;
  background: linear-gradient(135deg, #FF2D55 0%, #AF52ED 100%);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  margin-bottom: 12rpx;
}

.app-desc {
  font-size: 28rpx;
  color: #888;
}

.tab-row {
  display: flex;
  margin-bottom: 48rpx;
  border-bottom: 1rpx solid rgba(255, 255, 255, 0.1);
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 20rpx 0;
  position: relative;
}

.tab-item.active .tab-text {
  color: #fff;
  font-size: 34rpx;
  font-weight: bold;
}

.tab-text {
  font-size: 30rpx;
  color: #888;
  transition: all 0.2s ease;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: -1rpx;
  left: 50%;
  transform: translateX(-50%);
  width: 60rpx;
  height: 4rpx;
  background: linear-gradient(135deg, #FF2D55 0%, #AF52ED 100%);
  border-radius: 2rpx;
}

.form-section {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.input-group {
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.06);
  border-radius: 16rpx;
  padding: 0 24rpx;
  height: 96rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.08);
}

.input-icon {
  font-size: 32rpx;
  margin-right: 16rpx;
  flex-shrink: 0;
}

.form-input {
  flex: 1;
  height: 96rpx;
  font-size: 30rpx;
  color: #fff;
}

.input-placeholder {
  color: #666;
}

.login-btn {
  margin-top: 16rpx;
  height: 96rpx;
  background: linear-gradient(135deg, #FF2D55 0%, #AF52ED 100%);
  border-radius: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8rpx 32rpx rgba(255, 45, 85, 0.4);
}

.login-btn.loading {
  opacity: 0.7;
}

.btn-text {
  font-size: 32rpx;
  color: #fff;
  font-weight: bold;
  letter-spacing: 4rpx;
}

.demo-hint {
  text-align: center;
  margin-top: 24rpx;
}

.hint-text {
  font-size: 24rpx;
  color: #666;
}

.guest-login {
  text-align: center;
  margin-top: 40rpx;
}

.guest-text {
  font-size: 28rpx;
  color: #00D4FF;
}

.third-party {
  margin-top: auto;
  padding-top: 60rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 32rpx;
}

.third-title {
  font-size: 24rpx;
  color: #666;
}

.third-icons {
  display: flex;
  gap: 48rpx;
}

.third-icon {
  width: 88rpx;
  height: 88rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.08);
  border-radius: 50%;
}

.icon {
  font-size: 40rpx;
}
</style>
