<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-info">
              <p class="stat-label">总用户数</p>
              <p class="stat-value">{{ stats.totalUsers?.toLocaleString() || '0' }}</p>
            </div>
            <el-icon class="stat-icon" :size="48" color="#409eff"><User /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-info">
              <p class="stat-label">当前直播</p>
              <p class="stat-value">{{ stats.activeLives || '0' }}</p>
            </div>
            <el-icon class="stat-icon" :size="48" color="#67c23a"><VideoCamera /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-info">
              <p class="stat-label">今日收入(元)</p>
              <p class="stat-value">{{ stats.todayIncome?.toLocaleString() || '0' }}</p>
            </div>
            <el-icon class="stat-icon" :size="48" color="#e6a23c"><Wallet /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-info">
              <p class="stat-label">待审核内容</p>
              <p class="stat-value">{{ stats.pendingReview || '0' }}</p>
            </div>
            <el-icon class="stat-icon" :size="48" color="#f56c6c"><Document /></el-icon>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="16">
        <el-card shadow="hover">
          <template #header>
            <span>近7天趋势</span>
          </template>
          <div class="chart-placeholder">
            <el-empty description="图表数据加载中..." :image-size="80" />
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <span>今日快捷操作</span>
          </template>
          <div class="quick-actions">
            <el-button type="primary" @click="$router.push('/videos')">审核短视频</el-button>
            <el-button type="success" @click="$router.push('/live-rooms')">查看直播间</el-button>
            <el-button type="warning" @click="$router.push('/withdrawals')">提现审批</el-button>
            <el-button @click="$router.push('/users')">用户管理</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最近动态 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card shadow="hover">
          <template #header>
            <span>系统公告</span>
          </template>
          <el-table :data="notices" style="width: 100%">
            <el-table-column prop="time" label="时间" width="180" />
            <el-table-column prop="content" label="内容" />
            <el-table-column prop="operator" label="操作人" width="120" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { get } from '@/utils/request'

interface DashboardStats {
  totalUsers?: number
  activeLives?: number
  todayIncome?: number
  pendingReview?: number
}

const stats = ref<DashboardStats>({})
const notices = ref([
  { time: '2026-07-02 10:30', content: '系统初始化完成，所有服务运行正常', operator: '系统' },
  { time: '2026-07-02 09:00', content: 'SRS流媒体服务器已启动', operator: '系统' },
  { time: '2026-07-01 18:00', content: '管理平台首次部署上线', operator: '管理员' }
])

onMounted(async () => {
  try {
    const res = await get<DashboardStats>('/admin/dashboard/stats')
    stats.value = res.data || {}
  } catch {
    // 使用默认空数据
  }
})
</script>

<style lang="scss" scoped>
.stat-cards {
  .stat-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }
  .stat-label {
    font-size: 14px;
    color: #999;
    margin-bottom: 8px;
  }
  .stat-value {
    font-size: 24px;
    font-weight: 600;
    color: #333;
  }
  .stat-icon {
    opacity: 0.8;
  }
}

.chart-placeholder {
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.quick-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}
</style>
