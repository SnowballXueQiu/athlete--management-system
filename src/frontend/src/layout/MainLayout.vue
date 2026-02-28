<template>
  <div class="common-layout">
    <el-container>
      <el-aside width="200px">
        <el-menu router :default-active="$route.path" class="el-menu-vertical">
          <div class="logo-container">
            <img src="/favicon.svg" alt="logo" class="logo" />
            <span class="title">运动员管理系统</span>
          </div>
          <el-menu-item index="/dashboard">
            <el-icon>
              <DataBoard />
            </el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="/users" v-if="['admin'].includes(authStore.user.role)">
            <el-icon>
              <Avatar />
            </el-icon>
            <span>员工管理</span>
          </el-menu-item>
          <el-menu-item index="/students" v-if="['admin', 'coach', 'staff'].includes(authStore.user.role)">
            <el-icon>
              <User />
            </el-icon>
            <span>学员管理</span>
          </el-menu-item>
          <el-menu-item index="/courses" v-if="['admin', 'coach', 'staff'].includes(authStore.user.role)">
            <el-icon>
              <Reading />
            </el-icon>
            <span>课程管理</span>
          </el-menu-item>
          <el-menu-item index="/attendance" v-if="['admin', 'coach', 'staff'].includes(authStore.user.role)">
            <el-icon>
              <Calendar />
            </el-icon>
            <span>考勤管理</span>
          </el-menu-item>
          <el-menu-item index="/grades" v-if="['admin', 'coach', 'staff'].includes(authStore.user.role)">
            <el-icon>
              <Trophy />
            </el-icon>
            <span>成绩管理</span>
          </el-menu-item>
          <el-menu-item index="/statistics" v-if="['admin', 'coach', 'staff'].includes(authStore.user.role)">
            <el-icon>
              <PieChart />
            </el-icon>
            <span>数据统计</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-container>
        <el-header>
          <div class="header-left">
            <el-breadcrumb separator="/">
              <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
              <el-breadcrumb-item v-for="(item, index) in breadcrumbs" :key="index"
                :to="item.path ? { path: item.path } : undefined">
                {{ item.meta.title }}
              </el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          <div class="header-right">
            <el-dropdown>
              <span class="el-dropdown-link">
                {{ authStore.user.name }} ({{ authStore.user.role }})
                <el-icon class="el-icon--right"><arrow-down /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>
        <el-main>
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useAuthStore } from '@/store/auth'
import { useRouter, useRoute } from 'vue-router'
import { DataBoard, User, Reading, Calendar, Trophy, PieChart, ArrowDown, Avatar } from '@element-plus/icons-vue'

const authStore = useAuthStore()
const router = useRouter()
const route = useRoute()

const breadcrumbs = computed(() => {
  return route.matched.filter(item => item.meta && item.meta.title && item.path !== '/dashboard')
})

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.common-layout {
  height: 100vh;
}

.el-container {
  height: 100%;
}

.el-aside {
  background-color: #fff;
  border-right: 1px solid #dcdfe6;
}

.el-menu-vertical {
  height: 100%;
  border-right: none;
}

.logo-container {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid #dcdfe6;
  padding: 0 10px;
}

.logo {
  width: 30px;
  height: 30px;
  margin-right: 10px;
}

.title {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.el-header {
  background-color: #fff;
  border-bottom: 1px solid #dcdfe6;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.header-right {
  display: flex;
  align-items: center;
}

.el-dropdown-link {
  cursor: pointer;
  color: #409EFF;
  display: flex;
  align-items: center;
}
</style>
