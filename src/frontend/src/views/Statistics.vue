<template>
  <div class="statistics-container">
    <div class="header">
      <h2>数据统计与导出</h2>
    </div>

    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>学员总数</span>
            </div>
          </template>
          <div class="card-value">{{ summary.totalStudents }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>课程总数</span>
            </div>
          </template>
          <div class="card-value">{{ summary.totalCourses }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>进行中课程</span>
            </div>
          </template>
          <div class="card-value">{{ summary.activeCourses }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>考勤记录数</span>
            </div>
          </template>
          <div class="card-value">{{ summary.totalAttendance }}</div>
        </el-card>
      </el-col>
    </el-row>
    
    <div class="export-section">
      <h3>数据导出</h3>
      <div class="export-buttons">
        <el-button type="primary" size="large" @click="handleExport('students')">导出学员信息</el-button>
        <el-button type="success" size="large" @click="handleExport('attendance')">导出考勤记录</el-button>
        <el-button type="warning" size="large" @click="handleExport('grades')">导出成绩记录</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const summary = ref({
  totalStudents: 0,
  totalCourses: 0,
  activeCourses: 0,
  totalAttendance: 0,
  totalGrades: 0
})

const fetchSummary = async () => {
  try {
    const res = await request.get('/statistics/summary')
    summary.value = res.data
  } catch (error) {
    console.error(error)
  }
}

const handleExport = async (type: string) => {
  try {
    const res = await request.get(`/export/${type}`, {
      responseType: 'blob'
    })
    
    // Create blob link to download
    const url = window.URL.createObjectURL(new Blob([res.data]))
    const link = document.createElement('a')
    link.href = url
    
    // Set filename based on type
    const filenameMap: Record<string, string> = {
      'students': '学员信息.xlsx',
      'attendance': '考勤记录.xlsx',
      'grades': '成绩记录.xlsx'
    }
    link.setAttribute('download', filenameMap[type] || 'export.xlsx')
    
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('Export failed:', error)
    ElMessage.error('导出失败')
  }
}

onMounted(() => {
  fetchSummary()
})
</script>

<style scoped>
.statistics-container {
  padding: 20px;
}
.header {
  margin-bottom: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.card-value {
  font-size: 28px;
  font-weight: bold;
  text-align: center;
  color: #409EFF;
}
.export-section {
  margin-top: 40px;
}
.export-buttons {
  display: flex;
  gap: 20px;
  margin-top: 20px;
}
</style>
