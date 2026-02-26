<template>
  <div class="grades-container">
    <div class="header">
      <el-select v-model="searchCourseId" placeholder="选择课程" clearable @change="fetchGrades" style="width: 200px">
        <el-option v-for="course in courseList" :key="course.id" :label="course.name" :value="course.id" />
      </el-select>
      <el-input v-model="searchGradeType" placeholder="成绩类型(如:期末测试)" style="width: 200px" clearable @clear="fetchGrades" @keyup.enter="fetchGrades" />
      <el-button type="primary" @click="fetchGrades">搜索</el-button>
      <el-button type="success" @click="handleAdd">录入成绩</el-button>
    </div>

    <el-table :data="gradeList" style="width: 100%" v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="studentName" label="学员姓名" width="120" />
      <el-table-column prop="courseName" label="课程名称" width="150" />
      <el-table-column prop="gradeType" label="成绩类型" width="120" />
      <el-table-column prop="score" label="得分" width="100" />
      <el-table-column prop="maxScore" label="满分" width="100" />
      <el-table-column prop="gradeDate" label="日期" width="120" />
      <el-table-column prop="comment" label="评语" show-overflow-tooltip />
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
        @size-change="fetchGrades"
        @current-change="fetchGrades"
      />
    </div>

    <!-- Dialog for Add -->
    <el-dialog v-model="dialogVisible" title="录入成绩" width="500px">
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
        <el-form-item label="成绩类型">
          <el-input v-model="form.gradeType" placeholder="如: 期中测试, 体能测试" />
        </el-form-item>
        <el-form-item label="得分">
          <el-input-number v-model="form.score" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="满分">
          <el-input-number v-model="form.maxScore" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="日期">
          <el-date-picker v-model="form.gradeDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="评语">
          <el-input v-model="form.comment" type="textarea" />
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

const gradeList = ref([])
const courseList = ref<any[]>([])
const studentList = ref<any[]>([])
const loading = ref(false)
const searchCourseId = ref()
const searchGradeType = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)

const form = reactive({
  id: undefined,
  courseId: undefined,
  studentId: undefined,
  gradeType: '',
  score: 0,
  maxScore: 100,
  gradeDate: new Date().toISOString().split('T')[0],
  comment: ''
})

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

const fetchGrades = async () => {
  loading.value = true
  try {
    const res = await request.get('/grades', {
      params: {
        page: currentPage.value,
        size: pageSize.value,
        courseId: searchCourseId.value,
        gradeType: searchGradeType.value
      }
    })
    gradeList.value = res.data.records
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
    gradeType: '',
    score: 0,
    maxScore: 100,
    gradeDate: new Date().toISOString().split('T')[0],
    comment: ''
  })
  dialogVisible.value = true
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm('确认删除该成绩记录吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await request.delete(`/grades/${row.id}`)
      ElMessage.success('删除成功')
      fetchGrades()
    } catch (error) {
      console.error(error)
    }
  })
}

const handleSubmit = async () => {
  if (!form.courseId || !form.studentId || !form.gradeType || !form.gradeDate) {
    ElMessage.warning('请填写完整信息')
    return
  }
  try {
    await request.post('/grades', form)
    ElMessage.success('录入成功')
    dialogVisible.value = false
    fetchGrades()
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  fetchCommonData()
  fetchGrades()
})
</script>

<style scoped>
.grades-container {
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
