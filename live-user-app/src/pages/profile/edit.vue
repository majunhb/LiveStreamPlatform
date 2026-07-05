<template>
  <view class="edit-page">
    <view class="header">
      <view class="back-btn" @tap="goBack">
        <text class="back-icon">←</text>
      </view>
      <text class="header-title">编辑资料</text>
      <view class="save-btn" @tap="handleSave">
        <text class="save-text">保存</text>
      </view>
    </view>

    <scroll-view class="content-scroll" scroll-y>
      <view class="avatar-section">
        <view class="avatar-wrapper" @tap="changeAvatar">
          <UserAvatar :name="form.nickname" size="xl" />
          <view class="camera-icon">
            <text class="camera-text">📷</text>
          </view>
        </view>
        <text class="avatar-hint">点击更换头像</text>
      </view>

      <view class="form-section">
        <view class="form-item">
          <text class="form-label">昵称</text>
          <input
            class="form-input"
            v-model="form.nickname"
            placeholder="请输入昵称"
            placeholder-class="input-placeholder"
            maxlength="20"
          />
        </view>
        <view class="form-item">
          <text class="form-label">性别</text>
          <view class="gender-options">
            <view
              class="gender-option"
              :class="{ active: form.gender === 'male' }"
              @tap="form.gender = 'male'"
            >
              <text class="gender-icon">♂</text>
              <text class="gender-text">男</text>
            </view>
            <view
              class="gender-option"
              :class="{ active: form.gender === 'female' }"
              @tap="form.gender = 'female'"
            >
              <text class="gender-icon">♀</text>
              <text class="gender-text">女</text>
            </view>
            <view
              class="gender-option"
              :class="{ active: form.gender === 'secret' }"
              @tap="form.gender = 'secret'"
            >
              <text class="gender-icon">🔒</text>
              <text class="gender-text">保密</text>
            </view>
          </view>
        </view>
        <view class="form-item">
          <text class="form-label">简介</text>
          <textarea
            class="form-textarea"
            v-model="form.bio"
            placeholder="介绍一下自己吧..."
            placeholder-class="input-placeholder"
            maxlength="100"
          />
          <text class="char-count">{{ form.bio.length }}/100</text>
        </view>
        <view class="form-item">
          <text class="form-label">手机号</text>
          <input
            class="form-input"
            v-model="form.phone"
            placeholder="请输入手机号"
            placeholder-class="input-placeholder"
            type="number"
            maxlength="11"
          />
        </view>
      </view>

      <view class="bottom-space"></view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import UserAvatar from '@/components/UserAvatar.vue'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()

const form = reactive({
  nickname: '',
  gender: 'secret',
  bio: '',
  phone: ''
})

onMounted(() => {
  if (userStore.userInfo) {
    form.nickname = userStore.userInfo.nickname
    form.bio = userStore.userInfo.bio
    form.phone = userStore.userInfo.phone || ''
  }
})

function goBack() {
  uni.navigateBack()
}

function changeAvatar() {
  uni.showToast({ title: '头像更换功能开发中', icon: 'none' })
}

function handleSave() {
  if (!form.nickname.trim()) {
    uni.showToast({ title: '请输入昵称', icon: 'none' })
    return
  }

  userStore.updateUserInfo({
    nickname: form.nickname,
    bio: form.bio,
    phone: form.phone
  })

  uni.showToast({
    title: '保存成功',
    icon: 'success',
    success: () => {
      setTimeout(() => {
        uni.navigateBack()
      }, 500)
    }
  })
}
</script>

<style lang="scss" scoped>
.edit-page {
  min-height: 100vh;
  background: #0a0a0f;
  display: flex;
  flex-direction: column;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 80rpx 32rpx 24rpx;
  background: #0a0a0f;
}

.back-btn {
  width: 64rpx;
  height: 64rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.back-icon {
  font-size: 36rpx;
  color: #fff;
}

.header-title {
  font-size: 34rpx;
  font-weight: bold;
  color: #fff;
}

.save-btn {
  padding: 12rpx 28rpx;
  background: linear-gradient(135deg, #FF2D55 0%, #AF52ED 100%);
  border-radius: 24rpx;
}

.save-text {
  font-size: 26rpx;
  color: #fff;
  font-weight: 500;
}

.content-scroll {
  flex: 1;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40rpx 0;
}

.avatar-wrapper {
  position: relative;
  margin-bottom: 16rpx;
}

.camera-icon {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 48rpx;
  height: 48rpx;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.camera-text {
  font-size: 24rpx;
}

.avatar-hint {
  font-size: 24rpx;
  color: #888;
}

.form-section {
  margin: 0 32rpx;
}

.form-item {
  padding: 24rpx 0;
  border-bottom: 1rpx solid rgba(255, 255, 255, 0.05);
  position: relative;
}

.form-label {
  font-size: 28rpx;
  color: #888;
  margin-bottom: 16rpx;
  display: block;
}

.form-input {
  width: 100%;
  height: 72rpx;
  font-size: 30rpx;
  color: #fff;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12rpx;
  padding: 0 20rpx;
  box-sizing: border-box;
}

.input-placeholder {
  color: #555;
}

.form-textarea {
  width: 100%;
  height: 180rpx;
  font-size: 30rpx;
  color: #fff;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12rpx;
  padding: 20rpx;
  box-sizing: border-box;
}

.char-count {
  position: absolute;
  right: 16rpx;
  bottom: 32rpx;
  font-size: 22rpx;
  color: #666;
}

.gender-options {
  display: flex;
  gap: 20rpx;
}

.gender-option {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
  padding: 20rpx;
  background: rgba(255, 255, 255, 0.05);
  border: 2rpx solid rgba(255, 255, 255, 0.08);
  border-radius: 12rpx;
}

.gender-option.active {
  border-color: #FF2D55;
  background: rgba(255, 45, 85, 0.1);
}

.gender-icon {
  font-size: 32rpx;
}

.gender-text {
  font-size: 28rpx;
  color: #fff;
}

.bottom-space {
  height: 60rpx;
}
</style>
