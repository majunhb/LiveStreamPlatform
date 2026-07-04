<template>
  <div class="gift-list">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>礼物管理</span>
          <el-button type="primary" @click="showCreateDialog = true">新增礼物</el-button>
        </div>
      </template>

      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="礼物名称" width="150" />
        <el-table-column prop="icon" label="图标" width="80">
          <template #default="{ row }">
            <el-image v-if="row.icon" :src="row.icon" style="width: 40px; height: 40px" fit="contain" />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="price" label="价格(钻石)" width="120" />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="totalSent" label="累计发送" width="100" />
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" fixed="right" width="180">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="handleEdit(row)">编辑</el-button>
            <el-button :type="row.status === 1 ? 'warning' : 'success'" size="small" link @click="handleToggleStatus(row)">
              {{ row.status === 1 ? '下架' : '上架' }}
            </el-button>
            <el-popconfirm title="确定删除该礼物？" @confirm="handleDelete(row)">
              <template #reference>
                <el-button type="danger" size="small" link>删除</el-button>
              </template>
            </el-popconfirm>
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

    <!-- 新增/编辑礼物弹窗 -->
    <el-dialog v-model="showCreateDialog" :title="editId ? '编辑礼物' : '新增礼物'" width="500px">
      <el-form :model="giftForm" label-width="100px">
        <el-form-item label="礼物名称" required>
          <el-input v-model="giftForm.name" placeholder="请输入礼物名称" />
        </el-form-item>
        <el-form-item label="价格(钻石)" required>
          <el-input-number v-model="giftForm.price" :min="1" />
        </el-form-item>
        <el-form-item label="图标URL">
          <el-input v-model="giftForm.icon" placeholder="礼物图标地址" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="giftForm.sort" :min="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="giftForm.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getGiftList, createGift, updateGift, deleteGift } from '@/api/gift'
import { ElMessage } from 'element-plus'
import type { GiftInfo } from '@/types'

interface GiftRow extends GiftInfo {
  totalSent?: number
}

const loading = ref(false)
const list = ref<GiftRow[]>([])
const total = ref(0)
const showCreateDialog = ref(false)
const editId = ref<number | null>(null)
const query = reactive({ page: 1, size: 20 })

const giftForm = reactive({ name: '', price: 1, icon: '', sort: 0, status: 1 })

async function loadData() {
  loading.value = true
  try {
    const res = await getGiftList(query as any)
    list.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch { /* ignore */ } finally {
    loading.value = false
  }
}

function handleEdit(row: GiftRow) {
  editId.value = row.id
  Object.assign(giftForm, { name: row.name, price: row.price, icon: row.icon, sort: row.sort, status: row.status })
  showCreateDialog.value = true
}

async function handleSave() {
  if (editId.value) {
    await updateGift(editId.value, giftForm)
    ElMessage.success('更新成功')
  } else {
    await createGift(giftForm)
    ElMessage.success('创建成功')
  }
  showCreateDialog.value = false
  editId.value = null
  loadData()
}

async function handleToggleStatus(row: GiftRow) {
  await updateGift(row.id, { status: row.status === 1 ? 0 : 1 })
  ElMessage.success('状态更新成功')
  loadData()
}

async function handleDelete(row: GiftRow) {
  await deleteGift(row.id)
  ElMessage.success('删除成功')
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
.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
