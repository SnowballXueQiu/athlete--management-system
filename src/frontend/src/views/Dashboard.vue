<template>
  <div class="dashboard-container">
    <!-- Welcome Section -->
    <el-card class="welcome-card">
      <div class="welcome-content">
        <div class="welcome-text">
          <h2>欢迎回来，{{ authStore.user.name }}</h2>
          <p class="subtitle">{{ currentDate }} | {{ authStore.user.role === 'admin' ? '管理员' : '普通用户' }}</p>
        </div>
        <img src="/favicon.svg" alt="Sports Icon" class="welcome-icon" />
      </div>
    </el-card>

    <!-- Statistics Cards -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card blue">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon>
                <User />
              </el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">学员总数</div>
              <div class="stat-value">{{ summary.totalStudents }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card green">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon>
                <Reading />
              </el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">课程总数</div>
              <div class="stat-value">{{ summary.totalCourses }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card orange">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon>
                <Calendar />
              </el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">今日考勤</div>
              <div class="stat-value">{{ summary.totalAttendance }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card purple">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon>
                <Trophy />
              </el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">平均成绩</div>
              <div class="stat-value">{{ summary.averageGrade }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Charts & Activity Section -->
    <el-row :gutter="20" class="charts-row">
      <el-col :span="16">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>本周训练活跃度</span>
            </div>
          </template>
          <div class="chart-placeholder">
            <!-- Simulated Bar Chart -->
            <div class="bar-chart">
              <div class="bar-group" v-for="(item, index) in weeklyActivity" :key="index">
                <div class="bar-value" :style="{ bottom: (item.percent || 0) + '%' }">{{ item.value }}</div>
                <div class="bar" :style="{ height: (item.percent || 0) + '%' }"></div>
                <div class="label">{{ item.day }}</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="activity-card">
          <template #header>
            <div class="card-header">
              <span>快捷入口</span>
            </div>
          </template>
          <div class="quick-actions">
            <el-button type="primary" plain icon="Plus" @click="$router.push('/students')">添加学员</el-button>
            <el-button type="success" plain icon="Check" @click="$router.push('/attendance')">考勤打卡</el-button>
            <el-button type="warning" plain icon="DataLine" @click="$router.push('/statistics')">查看报表</el-button>
          </div>
        </el-card>

        <el-card class="notice-card" style="margin-top: 20px;">
          <template #header>
            <div class="card-header">
              <span>系统公告</span>
            </div>
          </template>
          <div class="notice-list">
            <div class="notice-item"
              @click="showNotice('维护通知', '系统将于本周日凌晨2:00-4:00进行服务器例行维护，届时将无法访问，请提前保存数据。\n(此为测试公告)')">
              <span class="notice-dot"></span>
              <span class="notice-text">系统维护通知</span>
            </div>
            <div class="notice-item"
              @click="showNotice('新课程上线', '【游泳体能强化】课程已全新上线！\n授课教练：李教练\n时间：每周一、四晚18:00\n欢迎各位学员踊跃报名！\n(此为测试公告)')">
              <span class="notice-dot"></span>
              <span class="notice-text">新课程上线通知</span>
            </div>
            <div class="notice-item"
              @click="showNotice('表彰名单', '恭喜以下学员在期中测试中取得优异成绩：\n张三 (篮球 - 98分)\n李四 (足球 - 95分)\n希望大家向榜样学习，再接再厉！\n(此为测试公告)')">
              <span class="notice-dot"></span>
              <span class="notice-text">优秀学员表彰名单</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { useAuthStore } from '@/store/auth'
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { User, Reading, Calendar, Trophy, Plus, Check, DataLine } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'

const authStore = useAuthStore()
const currentDate = ref(new Date().toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' }))

const summary = ref({
  totalStudents: 0,
  totalCourses: 0,
  activeCourses: 0,
  totalAttendance: 0,
  totalGrades: 0,
  averageGrade: '0.0'
})

const weeklyActivity = ref<{ day: string, value: number, percent?: number }[]>([])

const fetchSummary = async () => {
  try {
    // Reuse existing statistics API
    const res = await request.get('/statistics/summary')
    if (res.data) {
      summary.value = res.data

      if (res.data.weeklyActivity && Array.isArray(res.data.weeklyActivity)) {
        const activities = res.data.weeklyActivity
        const max = Math.max(...activities.map((item: any) => item.value), 10) // Minimum scale 10

        weeklyActivity.value = activities.map((item: any) => ({
          day: item.day,
          value: item.value,
          percent: (item.value / max) * 100
        }))
      }
    }
  } catch (error) {
    console.error('Failed to fetch summary:', error)
  }
}

const showNotice = (title: string, content: string) => {
  ElMessageBox.alert(content, title, {
    confirmButtonText: '关闭',
    dangerouslyUseHTMLString: false,
    customStyle: { whiteSpace: 'pre-line' }
  })
}

onMounted(() => {
  fetchSummary()
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.welcome-card {
  margin-bottom: 20px;
  background: linear-gradient(135deg, #fff 0%, #f0f9ff 100%);
}

.welcome-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 20px;
}

.welcome-text h2 {
  margin: 0 0 10px 0;
  color: #303133;
}

.subtitle {
  color: #909399;
  margin: 0;
}

.welcome-icon {
  width: 80px;
  height: 80px;
  opacity: 0.9;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-content {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-right: 15px;
  color: #fff;
}

.stat-icon .el-icon {
  font-size: 30px;
}

.stat-info {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 5px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

/* Card Colors */
.blue .stat-icon {
  background: linear-gradient(135deg, #409EFF, #79bbff);
}

.green .stat-icon {
  background: linear-gradient(135deg, #67C23A, #95d475);
}

.orange .stat-icon {
  background: linear-gradient(135deg, #E6A23C, #eebe77);
}

.purple .stat-icon {
  background: linear-gradient(135deg, #a0cfff, #b37feb);
}

.charts-row .el-card {
  height: 100%;
}

.bar-chart {
  display: flex;
  justify-content: space-around;
  align-items: flex-end;
  height: 300px;
  padding-top: 20px;
}

.bar-group {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 40px;
  height: 100%;
  justify-content: flex-end;
  position: relative;
}

.bar-value {
  position: absolute;
  margin-bottom: 5px;
  font-size: 12px;
  color: #606266;
  font-weight: bold;
  transition: bottom 0.5s ease;
}

.bar {
  width: 100%;
  background: #409EFF;
  border-radius: 4px 4px 0 0;
  transition: height 0.5s ease;
}

.label {
  margin-top: 10px;
  color: #606266;
  font-size: 12px;
}

.quick-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.quick-actions .el-button {
  margin-left: 0;
  justify-content: flex-start;
}

.notice-list {
  padding: 10px 0;
}

.notice-item {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  font-size: 14px;
  color: #606266;
  cursor: pointer;
}

.notice-item:hover {
  color: #409EFF;
}

.notice-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background-color: #E6A23C;
  margin-right: 10px;
}
</style>
