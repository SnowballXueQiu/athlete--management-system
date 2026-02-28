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
              <div class="stat-label">总考勤</div>
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
      <!-- Activity Bar Chart -->
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>本周训练活跃度</span>
            </div>
          </template>
          <div ref="activityChartRef" style="height: 300px;"></div>
        </el-card>
      </el-col>

      <!-- Pie Chart & Line Chart Section -->
      <el-col :span="12">
        <el-row :gutter="20">
          <el-col :span="24" style="margin-bottom: 20px;">
            <el-card class="chart-card">
              <template #header>
                <div class="card-header">
                  <span>学员分布</span>
                </div>
              </template>
              <div ref="distributionChartRef" style="height: 250px;"></div>
            </el-card>
          </el-col>
          <el-col :span="24">
            <el-card class="chart-card">
              <template #header>
                <div class="card-header">
                  <span>成绩趋势 (近6个月)</span>
                </div>
              </template>
              <div ref="gradeChartRef" style="height: 250px;"></div>
            </el-card>
          </el-col>
        </el-row>
      </el-col>
    </el-row>

    <!-- Quick Actions & Notices -->
    <el-row :gutter="20" class="bottom-row" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card class="activity-card">
          <template #header>
            <div class="card-header">
              <span>快捷入口</span>
            </div>
          </template>
          <div class="quick-actions horizontal">
            <el-button type="primary" plain icon="Plus" @click="$router.push('/students')">添加学员</el-button>
            <el-button type="success" plain icon="Check" @click="$router.push('/attendance')">考勤打卡</el-button>
            <el-button type="warning" plain icon="DataLine" @click="$router.push('/statistics')">查看报表</el-button>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="notice-card">
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
import { ref, onMounted, onUnmounted } from 'vue'
import request from '@/utils/request'
import { User, Reading, Calendar, Trophy, Plus, Check, DataLine } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'
import * as echarts from 'echarts'

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

// Chart Refs
const activityChartRef = ref<HTMLElement | null>(null)
const distributionChartRef = ref<HTMLElement | null>(null)
const gradeChartRef = ref<HTMLElement | null>(null)

// Chart Instances
let activityChart: echarts.ECharts | null = null
let distributionChart: echarts.ECharts | null = null
let gradeChart: echarts.ECharts | null = null

const initCharts = () => {
  if (activityChartRef.value) activityChart = echarts.init(activityChartRef.value)
  if (distributionChartRef.value) distributionChart = echarts.init(distributionChartRef.value)
  if (gradeChartRef.value) gradeChart = echarts.init(gradeChartRef.value)
}

const updateCharts = (data: any) => {
  // 1. Weekly Activity Chart (Bar)
  if (activityChart && data.weeklyActivity) {
    activityChart.setOption({
      tooltip: {
        trigger: 'axis',
        axisPointer: { type: 'shadow' }
      },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: {
        type: 'category',
        data: data.weeklyActivity.map((item: any) => item.day),
        axisTick: { alignWithLabel: true }
      },
      yAxis: { type: 'value' },
      series: [
        {
          name: '出勤人数',
          type: 'bar',
          barWidth: '60%',
          data: data.weeklyActivity.map((item: any) => item.value),
          itemStyle: { color: '#409EFF' }
        }
      ]
    })
  }

  // 2. Student Distribution Chart (Pie)
  if (distributionChart && data.studentDistribution) {
    distributionChart.setOption({
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b}: {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        left: 'left'
      },
      series: [
        {
          name: '学员分布',
          type: 'pie',
          radius: ['50%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 10,
            borderColor: '#fff',
            borderWidth: 2
          },
          label: {
            show: false,
            position: 'center'
          },
          emphasis: {
            label: {
              show: true,
              fontSize: 20,
              fontWeight: 'bold'
            }
          },
          labelLine: { show: false },
          data: data.studentDistribution.map((item: any) => ({
            value: item.count,
            name: item.name
          }))
        }
      ]
    })
  }

  // 3. Grade Trend Chart (Line)
  if (gradeChart && data.gradeTrend) {
    gradeChart.setOption({
      tooltip: {
        trigger: 'axis'
      },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: data.gradeTrend.map((item: any) => item.month)
      },
      yAxis: {
        type: 'value',
        min: (value: any) => Math.floor(value.min - 5),
        max: 100
      },
      series: [
        {
          name: '平均成绩',
          type: 'line',
          data: data.gradeTrend.map((item: any) => item.avgScore),
          smooth: true,
          itemStyle: { color: '#67C23A' },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(103, 194, 58, 0.5)' },
              { offset: 1, color: 'rgba(103, 194, 58, 0.1)' }
            ])
          }
        }
      ]
    })
  }
}

const fetchSummary = async () => {
  try {
    const res = await request.get('/statistics/summary')
    if (res.data) {
      summary.value = res.data
      updateCharts(res.data)
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

const handleResize = () => {
  activityChart?.resize()
  distributionChart?.resize()
  gradeChart?.resize()
}

onMounted(() => {
  initCharts()
  fetchSummary()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  activityChart?.dispose()
  distributionChart?.dispose()
  gradeChart?.dispose()
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

.quick-actions {
  display: flex;
  gap: 10px;
}

.quick-actions.horizontal {
  flex-direction: row;
  justify-content: space-around;
}

.quick-actions .el-button {
  margin-left: 0;
  flex: 1;
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
