<template>
  <div class="admin-list">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>管理员管理</span>
          <el-button type="primary" @click="showDialog = true">新增管理员</el-button>
        </div>
      </template>

      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column prop="realName" label="姓名" width="120" />
        <el-table-column prop="roleName" label="角色" width="120" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastLoginTime" label="最后登录" width="170" />
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="warning" size="small" link @click="handleResetPwd(row)">重置密码</el-button>
            <el-popconfirm title="确定禁用该管理员？" @confirm="handleToggleStatus(row)">
              <template #reference>
                <el-button :type="row.status === 1 ? 'danger' : 'success'" size="small" link>
                  {{ row.status === 1 ? '禁用' : '启用' }}
                </el-button>
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

    <!-- 新增/编辑管理员弹窗 -->
    <el-dialog v-model="showDialog" :title="editId ? '编辑管理员' : '新增管理员'" width="500px">
      <el-form :model="adminForm" label-width="80px">
        <el-form-item label="用户名" required>
          <el-input v-model="adminForm.username" placeholder="登录用户名" :disabled="!!editId" />
        </el-form-item>
        <el-form-item v-if="!editId" label="密码" required>
          <el-input v-model="adminForm.password" type="password" placeholder="初始密码" show-password />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="adminForm.realName" placeholder="真实姓名" />
        </el-form-item>
        <el-form-item label="角色" required>
          <el-select v-model="adminForm.roleId" placeholder="选择角色">
            <el-option label="超级管理员" :value="1" />
            <el-option label="运营管理员" :value="2" />
            <el-option label="审核员" :value="3" />
            <el-option label="财务" :value="4" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { get, post } from '@/utils/request'
import type { PageResult, AdminInfo } from '@/types'
import { ElMessage } from 'element-plus'

interface AdminRow extends AdminInfo {
  realName?: string
  roleId?: number
  roleName?: string
  lastLoginTime?: string
  status: number
}

const loading = ref(false)
const list = ref<AdminRow[]>([])
const total = ref(0)
const showDialog = ref(false)
const editId = ref<number | null>(null)
const query = reactive({ page: 1, size: 20 })

const adminForm = reactive({ username: '', password: '', realName: '', roleId: 2 })

async function loadData() {
  loading.value = true
  try {
    const res = await get<PageResult<AdminRow>>('/admin/admins', query as unknown as Record<string, unknown>)
    list.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch { /* ignore */ } finally {
    loading.value = false
  }
}

function handleEdit(row: AdminRow) {
  editId.value = row.id
  Object.assign(adminForm, { username: row.username, realName: row.realName, roleId: row.roleId })
  showDialog.value = true
}

async function handleSave() {
  if (editId.value) {
    await post(`/admin/admins/${editId.value}/update`, adminForm as unknown as Record<string, unknown>)
    ElMessage.success('更新成功')
  } else {
    await post('/admin/admins/create', adminForm as unknown as Record<string, unknown>)
    ElMessage.success('创建成功')
  }
  showDialog.value = false
  editId.value = null
  loadData()
}

async function handleResetPwd(row: AdminRow) {
  await post(`/admin/admins/${row.id}/reset-password`)
  ElMessage.success('密码已重置为默认密码')
}

async function handleToggleStatus(row: AdminRow) {
  await post(`/admin/admins/${row.id}/toggle-status`)
  ElMessage.success('状态已更新')
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
