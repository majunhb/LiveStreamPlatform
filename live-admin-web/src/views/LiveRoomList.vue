<template>
  <div class="live-room-list">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>直播间管理</span>
          <div class="search-bar">
            <el-input v-model="query.keyword" placeholder="搜索房间ID/主播昵称" clearable style="width: 240px" @clear="loadData" @keyup.enter="loadData" />
            <el-select v-model="query.status" placeholder="直播状态" clearable style="width: 120px" @change="loadData">
              <el-option label="直播中" :value="1" />
              <el-option label="已结束" :value="0" />
            </el-select>
            <el-button type="primary" @click="loadData">搜索</el-button>
          </div>
        </div>
      </template>

      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="id" label="房间ID" width="90" />
        <el-table-column prop="title" label="直播标题" width="200" show-overflow-tooltip />
        <el-table-column prop="anchorName" label="主播" width="120" />
        <el-table-column prop="onlineCount" label="在线人数" width="100" />
        <el-table-column prop="likeCount" label="点赞数" width="100" />
        <el-table-column prop="giftAmount" label="礼物收入(元)" width="120" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'danger' : 'info'" size="small">
              {{ row.status === 1 ? '直播中' : '已结束' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="170" />
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="handleView(row)">查看</el-button>
            <el-button v-if="row.status === 1" type="danger" size="small" @click="handleForceStop(row)">强制下播</el-button>
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
import { getLiveRoomList, forceStopLive } from '@/api/live'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { LiveRoomInfo } from '@/types'

interface LiveRoomRow extends LiveRoomInfo {
  anchorName?: string
  likeCount?: number
  giftAmount?: number
}

const loading = ref(false)
const list = ref<LiveRoomRow[]>([])
const total = ref(0)
const query = reactive({ keyword: '', status: undefined as number | undefined, page: 1, size: 20 })

async function loadData() {
  loading.value = true
  try {
    const res = await getLiveRoomList(query as unknown as Record<string, unknown>)
    list.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch { /* ignore */ } finally {
    loading.value = false
  }
}

async function handleForceStop(row: LiveRoomRow) {
  const { value: reason } = await ElMessageBox.prompt('请输入强制下播原因', '强制下播', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPattern: /.+/,
    inputErrorMessage: '原因不能为空'
  })
  await forceStopLive(row.id, reason)
  ElMessage.success('已强制下播')
  loadData()
}

function handleView(row: LiveRoomRow) {
  ElMessage.info(`查看直播间 ${row.title} 详情（功能开发中）`)
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
