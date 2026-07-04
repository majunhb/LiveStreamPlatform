<template>
  <div class="user-list">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <div class="search-bar">
            <el-input v-model="query.keyword" placeholder="搜索用户ID/昵称/手机号" clearable style="width: 260px" @clear="loadData" @keyup.enter="loadData" />
            <el-select v-model="query.status" placeholder="状态" clearable style="width: 120px" @change="loadData">
              <el-option label="正常" :value="1" />
              <el-option label="封禁" :value="0" />
            </el-select>
            <el-button type="primary" @click="loadData">搜索</el-button>
          </div>
        </div>
      </template>

      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="nickname" label="昵称" width="150" />
        <el-table-column prop="phone" label="手机号" width="140" />
        <el-table-column prop="gender" label="性别" width="70">
          <template #default="{ row }">
            {{ row.gender === 1 ? '男' : row.gender === 2 ? '女' : '未知' }}
          </template>
        </el-table-column>
        <el-table-column prop="balance" label="余额(元)" width="100" />
        <el-table-column prop="followCount" label="关注" width="80" />
        <el-table-column prop="fansCount" label="粉丝" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '封禁' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="170" />
        <el-table-column label="操作" fixed="right" width="180">
          <template #default="{ row }">
            <el-button v-if="row.status === 1" type="danger" size="small" @click="handleBan(row)">封禁</el-button>
            <el-button v-else type="success" size="small" @click="handleUnban(row)">解封</el-button>
            <el-button type="primary" size="small" link @click="handleDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="query.page"
          v-model:page-size="query.size"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getUserList, banUser, unbanUser } from '@/api/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { UserInfo } from '@/types'

interface UserRow extends UserInfo {
  gender?: number
  balance?: number
  followCount?: number
  fansCount?: number
}

const loading = ref(false)
const list = ref<UserRow[]>([])
const total = ref(0)
const query = reactive({ keyword: '', status: undefined as number | undefined, page: 1, size: 20 })

async function loadData() {
  loading.value = true
  try {
    const res = await getUserList(query as unknown as Record<string, unknown>)
    list.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch { /* ignore */ } finally {
    loading.value = false
  }
}

async function handleBan(row: UserRow) {
  await ElMessageBox.confirm(`确定封禁用户「${row.nickname}」？`, '封禁确认', { type: 'warning' })
  await banUser(row.id, '管理员操作')
  ElMessage.success('封禁成功')
  loadData()
}

async function handleUnban(row: UserRow) {
  await unbanUser(row.id)
  ElMessage.success('解封成功')
  loadData()
}

function handleDetail(row: UserRow) {
  ElMessage.info(`查看用户 ${row.nickname} 详情（功能开发中）`)
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
