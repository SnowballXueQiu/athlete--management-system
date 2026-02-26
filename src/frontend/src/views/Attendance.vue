<template>
  <div class="attendance-container">
    <div class="header">
      <el-select v-model="searchCourseId" placeholder="选择课程" clearable @change="fetchAttendance" style="width: 200px">
        <el-option v-for="course in courseList" :key="course.id" :label="course.name" :value="course.id" />
      </el-select>
      <el-date-picker v-model="searchDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" @change="fetchAttendance" style="width: 200px" />
      <el-button type="primary" @click="fetchAttendance">搜索</el-button>
      <el-button type="success" @click="handleAdd">录入考勤</el-button>
    </div>

    <el-table :data="attendanceList" style="width: 100%" v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="studentName" label="学员姓名" width="120" />
      <el-table-column prop="courseName" label="课程名称" width="150" />
      <el-table-column prop="attendanceDate" label="日期" width="120" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">{{ getStatusLabel(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="note" label="备注" />
      <el-table-column label="操作" width="120">
        <template #default="scope">
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
        @size-change="fetchAttendance"
        @current-change="fetchAttendance"
      />
    </div>

    <!-- Dialog for Add -->
    <el-dialog v-model="dialogVisible" title="录入考勤" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="课程">
          <el-select v-model="form.courseId" placeholder="请选择课程" style="width: 100%">
            <el-option v-for="course in courseList" :key="course.id" :label="course.name" :value="course.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="学员">
          <el-select v-model="form.studentId" placeholder="请选择学员" filterable style="width: 100%">
            <el-option v-for="student in studentList" :key="student.id" :label="student.name" :value="student.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期">
          <el-date-picker v-model="form.attendanceDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio label="present">出勤</el-radio>
            <el-radio label="absent">缺勤</el-radio>
            <el-radio label="leave">请假</el-radio>
            <el-radio label="late">迟到</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.note" type="textarea" />
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

const attendanceList = ref([])
const courseList = ref<any[]>([])
const studentList = ref<any[]>([])
const loading = ref(false)
const searchCourseId = ref()
const searchDate = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)

const form = reactive({
  id: undefined,
  courseId: undefined,
  studentId: undefined,
  attendanceDate: new Date().toISOString().split('T')[0],
  status: 'present',
  note: ''
})

const getStatusType = (status: string) => {
  const map: Record<string, string> = {
    present: 'success',
    absent: 'danger',
    leave: 'warning',
    late: 'info'
  }
  return map[status] || ''
}

const getStatusLabel = (status: string) => {
  const map: Record<string, string> = {
    present: '出勤',
    absent: '缺勤',
    leave: '请假',
    late: '迟到'
  }
  return map[status] || status
}

const fetchCommonData = async () => {
  try {
    const [coursesRes, studentsRes] = await Promise.all([
      request.get('/courses', { params: { size: 100 } }),
      request.get('/students', { params: { size: 1000 } })
    ])
    courseList.value = coursesRes.data.records
    studentList.value = studentsRes.data.records
  } catch (error) {
    console.error(error)
  }
}

const fetchAttendance = async () => {
  loading.value = true
  try {
    const res = await request.get('/attendance', {
      params: {
        page: currentPage.value,
        size: pageSize.value,
        courseId: searchCourseId.value,
        date: searchDate.value
      }
    })
    attendanceList.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  Object.assign(form, {
    id: undefined,
    courseId: undefined,
    studentId: undefined,
    attendanceDate: new Date().toISOString().split('T')[0],
    status: 'present',
    note: ''
  })
  dialogVisible.value = true
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm('确认删除该考勤记录吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await request.delete(`/attendance/${row.id}`)
      ElMessage.success('删除成功')
      fetchAttendance()
    } catch (error) {
      console.error(error)
    }
  })
}

const handleSubmit = async () => {
  if (!form.courseId || !form.studentId || !form.attendanceDate) {
    ElMessage.warning('请填写完整信息')
    return
  }
  try {
    await request.post('/attendance', form)
    ElMessage.success('录入成功')
    dialogVisible.value = false
    fetchAttendance()
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  fetchCommonData()
  fetchAttendance()
})
</script>

<style scoped>
.attendance-container {
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
