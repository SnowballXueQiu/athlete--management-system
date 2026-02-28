<template>
  <div class="users-container">
    <div class="header">
      <el-input v-model="searchName" placeholder="搜索员工姓名" style="width: 200px" clearable @clear="fetchUsers"
        @keyup.enter="fetchUsers" />
      <el-button type="primary" @click="fetchUsers">搜索</el-button>
      <el-button @click="handleReset">重置</el-button>
      <el-button type="success" @click="handleAdd">添加员工</el-button>
    </div>

    <el-table :data="users" style="width: 100%" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" align="center" />
      <el-table-column prop="username" label="用户名" width="120" align="center" />
      <el-table-column prop="name" label="姓名" width="120" align="center" />
      <el-table-column prop="role" label="角色" width="120" align="center">
        <template #default="scope">
          <el-tag :type="getRoleTag(scope.row.role)">{{ getRoleLabel(scope.row.role) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" align="center" />
      <el-table-column label="操作" width="180" align="center" fixed="right">
        <template #default="scope">
          <el-button size="small" @click="handleEdit(scope.row)" :disabled="scope.row.role === 'admin' && scope.row.username === 'admin'">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(scope.row)" :disabled="scope.row.role === 'admin'">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper" :total="total" @size-change="fetchUsers"
        @current-change="fetchUsers" />
    </div>

    <!-- Dialog for Add/Edit -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑员工' : '添加员工'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.role" placeholder="请选择角色">
            <el-option v-for="role in roles" :key="role.value" :label="role.label" :value="role.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" placeholder="不修改请留空 (默认123456)" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const users = ref([])
const loading = ref(false)
const searchName = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const roles = ref([
  { value: 'coach', label: '教练' },
  { value: 'staff', label: '行政员' },
  { value: 'admin', label: '管理员' }
])

const form = reactive({
  id: undefined,
  username: '',
  name: '',
  role: 'coach',
  password: ''
})

const getRoleTag = (role: string) => {
  const map: Record<string, string> = {
    admin: 'danger',
    coach: 'success',
    staff: 'warning'
  }
  return map[role] || 'info'
}

const getRoleLabel = (role: string) => {
  const map: Record<string, string> = {
    admin: '管理员',
    coach: '教练',
    staff: '行政员'
  }
  return map[role] || role
}

const fetchUsers = async () => {
  loading.value = true
  try {
    const res = await request.get('/users', {
      params: {
        page: currentPage.value,
        size: pageSize.value,
        name: searchName.value
      }
    })
    users.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  searchName.value = ''
  fetchUsers()
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, {
    id: undefined,
    username: '',
    name: '',
    role: 'coach',
    password: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  isEdit.value = true
  Object.assign(form, { ...row, password: '' }) // Clear password field for edit
  dialogVisible.value = true
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确认删除员工 ${row.name} 吗?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await request.delete(`/users/${row.id}`)
      ElMessage.success('删除成功')
      fetchUsers()
    } catch (error) {
      console.error(error)
    }
  })
}

const handleSubmit = async () => {
  try {
    if (isEdit.value) {
      await request.put(`/users/${form.id}`, form)
      ElMessage.success('更新成功')
    } else {
      await request.post('/users', form)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    fetchUsers()
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  fetchUsers()
})
</script>

<style scoped>
.users-container {
  padding: 20px;
}

.header {
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>