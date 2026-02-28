<template>
  <div class="courses-container">
    <div class="header">
      <el-input v-model="searchName" placeholder="搜索课程名称" style="width: 200px" clearable @clear="fetchCourses" @keyup.enter="fetchCourses" />
      <el-button type="primary" @click="fetchCourses">搜索</el-button>
      <el-button type="success" @click="handleAdd" v-if="['admin', 'staff'].includes(authStore.user.role)">添加课程</el-button>
    </div>

    <el-table :data="courses" style="width: 100%" v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="name" label="课程名称" width="150" />
      <el-table-column prop="description" label="描述" show-overflow-tooltip />
      <el-table-column label="上课时间" width="200">
        <template #default="scope">
          {{ scope.row.daysOfWeek }} {{ scope.row.startTime }}-{{ scope.row.endTime }}
        </template>
      </el-table-column>
      <el-table-column prop="location" label="地点" width="120" />
      <el-table-column prop="maxStudents" label="最大人数" width="100" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.status === 'active' ? 'success' : 'info'">
            {{ scope.row.status === 'active' ? '进行中' : '已结束' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" v-if="['admin', 'staff'].includes(authStore.user.role)">
        <template #default="scope">
          <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="fetchCourses"
        @current-change="fetchCourses"
      />
    </div>

    <!-- Dialog for Add/Edit -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑课程' : '添加课程'" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="课程名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="课程描述">
          <el-input v-model="form.description" type="textarea" />
        </el-form-item>
        <el-form-item label="上课日">
          <el-checkbox-group v-model="daysOfWeekList">
            <el-checkbox label="周一" />
            <el-checkbox label="周二" />
            <el-checkbox label="周三" />
            <el-checkbox label="周四" />
            <el-checkbox label="周五" />
            <el-checkbox label="周六" />
            <el-checkbox label="周日" />
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="开始时间">
          <el-time-picker v-model="form.startTime" format="HH:mm" value-format="HH:mm:ss" />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-time-picker v-model="form.endTime" format="HH:mm" value-format="HH:mm:ss" />
        </el-form-item>
        <el-form-item label="上课地点">
          <el-input v-model="form.location" />
        </el-form-item>
        <el-form-item label="最大人数">
          <el-input-number v-model="form.maxStudents" :min="1" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio label="active">进行中</el-radio>
            <el-radio label="inactive">已结束</el-radio>
          </el-radio-group>
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
import { ref, reactive, onMounted, watch } from 'vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/store/auth'

const authStore = useAuthStore()
const courses = ref([])
const loading = ref(false)
const searchName = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const daysOfWeekList = ref<string[]>([])

const form = reactive({
  id: undefined,
  name: '',
  description: '',
  daysOfWeek: '',
  startTime: '09:00:00',
  endTime: '10:30:00',
  location: '',
  maxStudents: 30,
  status: 'active'
})

watch(daysOfWeekList, (val) => {
  form.daysOfWeek = val.join(',')
})

const fetchCourses = async () => {
  loading.value = true
  try {
    const res = await request.get('/courses', {
      params: {
        page: currentPage.value,
        size: pageSize.value,
        name: searchName.value
      }
    })
    courses.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, {
    id: undefined,
    name: '',
    description: '',
    daysOfWeek: '',
    startTime: '09:00:00',
    endTime: '10:30:00',
    location: '',
    maxStudents: 30,
    status: 'active'
  })
  daysOfWeekList.value = []
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  isEdit.value = true
  Object.assign(form, row)
  daysOfWeekList.value = row.daysOfWeek ? row.daysOfWeek.split(',') : []
  dialogVisible.value = true
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm('确认删除该课程吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await request.delete(`/courses/${row.id}`)
      ElMessage.success('删除成功')
      fetchCourses()
    } catch (error) {
      console.error(error)
    }
  })
}

const handleSubmit = async () => {
  try {
    if (isEdit.value) {
      await request.put(`/courses/${form.id}`, form)
      ElMessage.success('更新成功')
    } else {
      await request.post('/courses', form)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    fetchCourses()
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  fetchCourses()
})
</script>

<style scoped>
.courses-container {
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
