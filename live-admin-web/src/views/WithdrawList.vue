<template>
  <div class="withdraw-list">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>提现审核</span>
          <div class="search-bar">
            <el-input v-model="query.keyword" placeholder="搜索用户ID/昵称" clearable style="width: 200px" @clear="loadData" @keyup.enter="loadData" />
            <el-select v-model="query.status" placeholder="状态" clearable style="width: 120px" @change="loadData">
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
        <el-table-column prop="userName" label="用户" width="120" />
        <el-table-column prop="amount" label="提现金额(元)" width="130">
          <template #default="{ row }">
            <span style="color: #e6a23c; font-weight: 600">¥{{ row.amount?.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="fee" label="手续费(元)" width="110" />
        <el-table-column prop="actualAmount" label="实际到账(元)" width="130" />
        <el-table-column prop="accountType" label="到账方式" width="100">
          <template #default="{ row }">
            {{ row.accountType === 'alipay' ? '支付宝' : row.accountType === 'wechat' ? '微信' : '银行卡' }}
          </template>
        </el-table-column>
        <el-table-column prop="accountInfo" label="到账账号" width="180" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="['warning', 'success', 'danger'][row.status]" size="small">
              {{ ['待审核', '已通过', '已拒绝'][row.status] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="170" />
        <el-table-column label="操作" fixed="right" width="180">
          <template #default="{ row }">
            <template v-if="row.status === 0">
              <el-button type="success" size="small" @click="handleApprove(row)">通过</el-button>
              <el-button type="danger" size="small" @click="handleReject(row)">拒绝</el-button>
            </template>
            <span v-else style="color: #999">已处理</span>
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
import { getWithdrawList, approveWithdraw, rejectWithdraw } from '@/api/gift'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const list = ref<any[]>([])
const total = ref(0)
const query = reactive({ keyword: '', status: undefined as number | undefined, page: 1, size: 20 })

async function loadData() {
  loading.value = true
  try {
    const res: any = await getWithdrawList(query)
    list.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch { /* ignore */ } finally {
    loading.value = false
  }
}

async function handleApprove(row: any) {
  await ElMessageBox.confirm(`确认通过用户「${row.userName}」的 ¥${row.amount} 提现申请？`, '审批确认')
  await approveWithdraw(row.id)
  ElMessage.success('审批通过')
  loadData()
}

async function handleReject(row: any) {
  const { value: reason } = await ElMessageBox.prompt('请输入拒绝原因', '拒绝提现', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPattern: /.+/,
    inputErrorMessage: '原因不能为空'
  })
  await rejectWithdraw(row.id, reason)
  ElMessage.success('已拒绝')
  loadData()
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
