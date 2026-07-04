<template>
  <div class="video-list">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>短视频管理</span>
          <div class="search-bar">
            <el-input v-model="query.keyword" placeholder="搜索视频ID/标题/作者" clearable style="width: 240px" @clear="loadData" @keyup.enter="loadData" />
            <el-select v-model="query.status" placeholder="审核状态" clearable style="width: 120px" @change="loadData">
              <el-option label="待审核" :value="0" />
              <el-option label="已通过" :value="1" />
              <el-option label="已拒绝" :value="2" />
            </el-select>
            <el-button type="primary" @click="loadData">搜索</el-button>
          </div>
        </div>
      </template>

      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" width="200" show-overflow-tooltip />
        <el-table-column prop="authorName" label="作者" width="120" />
        <el-table-column prop="duration" label="时长(秒)" width="90" />
        <el-table-column prop="playCount" label="播放量" width="100" />
        <el-table-column prop="likeCount" label="点赞" width="80" />
        <el-table-column prop="commentCount" label="评论" width="80" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="170" />
        <el-table-column label="操作" fixed="right" width="260">
          <template #default="{ row }">
            <el-button v-if="row.status === 0" type="success" size="small" @click="handleReview(row, 1)">通过</el-button>
            <el-button v-if="row.status === 0" type="danger" size="small" @click="handleReject(row)">拒绝</el-button>
            <el-button type="primary" size="small" link @click="handleView(row)">查看</el-button>
            <el-button type="warning" size="small" link @click="handleRecommend(row)">推荐</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="query.page"
          v-model:page-size="query.size"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getVideoList, reviewVideo, recommendVideo } from '@/api/video'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { VideoInfo } from '@/types'

interface VideoRow extends VideoInfo {
  authorName?: string
  duration?: number
  playCount?: number
}

const loading = ref(false)
const list = ref<VideoRow[]>([])
const total = ref(0)
const query = reactive({ keyword: '', status: undefined as number | undefined, page: 1, size: 20 })

function statusText(status: number): string {
  return ['待审核', '已通过', '已拒绝'][status] || '未知'
}

function statusType(status: number): string {
  return ['warning', 'success', 'danger'][status] || 'info'
}

async function loadData() {
  loading.value = true
  try {
    const res = await getVideoList(query as any)
    list.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch { /* ignore */ } finally {
    loading.value = false
  }
}

async function handleReview(row: VideoRow, status: number) {
  await reviewVideo(row.id, status)
  ElMessage.success('审核通过')
  loadData()
}

async function handleReject(row: VideoRow) {
  const { value: reason } = await ElMessageBox.prompt('请输入拒绝原因', '拒绝审核', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPattern: /.+/,
    inputErrorMessage: '原因不能为空'
  })
  await reviewVideo(row.id, 2, reason)
  ElMessage.success('已拒绝')
  loadData()
}

async function handleRecommend(row: VideoRow) {
  await recommendVideo(row.id)
  ElMessage.success('已设为推荐')
}

function handleView(row: VideoRow) {
  ElMessage.info(`查看视频 ${row.title}（功能开发中）`)
}

onMounted(loadData)
</script>

<style lang="scss" scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.search-bar {
  display: flex;
  gap: 10px;
}
.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
