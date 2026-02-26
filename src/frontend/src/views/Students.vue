<template>
  <div class="students-container">
    <div class="header">
      <el-input v-model="searchName" placeholder="搜索学员姓名" style="width: 200px" clearable @clear="fetchStudents"
        @keyup.enter="fetchStudents" />
      <el-button type="primary" @click="fetchStudents">搜索</el-button>
      <el-button @click="handleReset">重置</el-button>
      <el-button type="success" @click="handleAdd">添加学员</el-button>
    </div>

    <el-table :data="students" style="width: 100%" v-loading="loading" @sort-change="handleSortChange">
      <el-table-column prop="id" label="ID" width="80" sortable="custom" align="center" />
      <el-table-column prop="name" label="姓名" width="100" align="center" />
      <el-table-column prop="gender" label="性别" width="60" align="center" />
      <el-table-column prop="age" label="年龄" width="60" align="center" />
      <el-table-column prop="phone" label="电话" width="120" show-overflow-tooltip align="center" />
      <el-table-column prop="email" label="邮箱" width="180" show-overflow-tooltip align="center" />
      <el-table-column prop="address" label="地址" show-overflow-tooltip align="center" />
      <el-table-column label="操作" width="180" align="center" fixed="right">
        <template #default="scope">
          <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper" :total="total" @size-change="fetchStudents"
        @current-change="fetchStudents" />
    </div>

    <!-- Dialog for Add/Edit -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑学员' : '添加学员'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="姓名">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="form.gender" placeholder="请选择">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="年龄">
          <el-input-number v-model="form.age" :min="1" />
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="form.address" />
        </el-form-item>
        <el-form-item label="紧急联系人">
          <el-input v-model="form.emergencyContact" />
        </el-form-item>
        <el-form-item label="紧急电话">
          <el-input v-model="form.emergencyPhone" />
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

const students = ref([])
const loading = ref(false)
const searchName = ref('')
const sortOrder = ref('asc')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)

const form = reactive({
  id: undefined,
  name: '',
  gender: '',
  age: 18,
  phone: '',
  email: '',
  address: '',
  emergencyContact: '',
  emergencyPhone: ''
})

const fetchStudents = async () => {
  loading.value = true
  try {
    const res = await request.get('/students', {
      params: {
        page: currentPage.value,
        size: pageSize.value,
        name: searchName.value,
        order: sortOrder.value
      }
    })
    students.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  searchName.value = ''
  sortOrder.value = 'asc'
  fetchStudents()
}

const handleSortChange = ({ prop, order }: { prop: string, order: string }) => {
  if (prop === 'id') {
    sortOrder.value = order === 'descending' ? 'desc' : 'asc'
    fetchStudents()
  }
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, {
    id: undefined,
    name: '',
    gender: '',
    age: 18,
    phone: '',
    email: '',
    address: '',
    emergencyContact: '',
    emergencyPhone: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm('确认删除该学员吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await request.delete(`/students/${row.id}`)
      ElMessage.success('删除成功')
      fetchStudents()
    } catch (error) {
      console.error(error)
    }
  })
}

const handleSubmit = async () => {
  try {
    if (isEdit.value) {
      await request.put(`/students/${form.id}`, form)
      ElMessage.success('更新成功')
    } else {
      await request.post('/students', form)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    fetchStudents()
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  fetchStudents()
})
</script>

<style scoped>
.students-container {
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
