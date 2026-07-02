<template>
  <div class="layout-container">
    <!-- 侧边栏 -->
    <aside class="sidebar" :class="{ 'is-collapsed': appStore.sidebarCollapsed }">
      <div class="logo">
        <el-icon :size="24"><VideoCamera /></el-icon>
        <span v-show="!appStore.sidebarCollapsed" class="logo-text">直播管理平台</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="appStore.sidebarCollapsed"
        :collapse-transition="false"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
      >
        <el-menu-item index="/dashboard">
          <el-icon><Odometer /></el-icon>
          <template #title>仪表盘</template>
        </el-menu-item>
        <el-menu-item index="/users">
          <el-icon><User /></el-icon>
          <template #title>用户管理</template>
        </el-menu-item>
        <el-menu-item index="/live-rooms">
          <el-icon><VideoCamera /></el-icon>
          <template #title>直播间管理</template>
        </el-menu-item>
        <el-menu-item index="/videos">
          <el-icon><Film /></el-icon>
          <template #title>短视频管理</template>
        </el-menu-item>
        <el-menu-item index="/gifts">
          <el-icon><Present /></el-icon>
          <template #title>礼物管理</template>
        </el-menu-item>
        <el-menu-item index="/withdrawals">
          <el-icon><Wallet /></el-icon>
          <template #title>提现审核</template>
        </el-menu-item>
        <el-menu-item index="/admins">
          <el-icon><Lock /></el-icon>
          <template #title>管理员管理</template>
        </el-menu-item>
      </el-menu>
    </aside>

    <!-- 主内容区 -->
    <div class="main-container">
      <!-- 顶部导航 -->
      <header class="header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="appStore.toggleSidebar" :size="20">
            <Fold v-if="!appStore.sidebarCollapsed" />
            <Expand v-else />
          </el-icon>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="admin-info">
              <el-avatar :size="30" icon="UserFilled" />
              <span class="admin-name">{{ userStore.userInfo?.username || '管理员' }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                <el-dropdown-item command="password">修改密码</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <!-- 内容 -->
      <main class="content">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAppStore } from '@/stores/app'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const appStore = useAppStore()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

function handleCommand(command: string) {
  if (command === 'logout') {
    userStore.logout()
    router.push('/login')
  }
}
</script>

<style lang="scss" scoped>
.layout-container {
  display: flex;
  height: 100vh;
  width: 100%;
}

.sidebar {
  width: 220px;
  height: 100%;
  background-color: #304156;
  transition: width 0.3s;
  overflow: hidden;
  flex-shrink: 0;

  &.is-collapsed {
    width: 64px;
  }

  .logo {
    height: 50px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    color: #fff;
    font-size: 16px;
    font-weight: 600;
    border-bottom: 1px solid #1f2d3d;
  }

  .el-menu {
    border-right: none;
  }
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.header {
  height: 50px;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;

  .collapse-btn {
    cursor: pointer;
    &:hover {
      color: #409eff;
    }
  }

  .admin-info {
    display: flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;
    color: #333;
  }
}

.content {
  flex: 1;
  padding: 20px;
  background: #f0f2f5;
  overflow-y: auto;
}

.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.2s;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-10px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(10px);
}
</style>
