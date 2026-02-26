import {
  createRouter,
  createWebHistory,
  type RouteRecordRaw,
} from "vue-router";
import { useAuthStore } from "@/store/auth";
import Login from "@/views/Login.vue";
import MainLayout from "@/layout/MainLayout.vue";

const routes: RouteRecordRaw[] = [
  {
    path: "/login",
    name: "Login",
    component: Login,
  },
  {
    path: "/",
    component: MainLayout,
    redirect: "/dashboard",
    children: [
      {
        path: "dashboard",
        name: "Dashboard",
        component: () => import("@/views/Dashboard.vue"),
        meta: { requiresAuth: true, title: "首页" },
      },
      {
        path: "students",
        name: "Students",
        component: () => import("@/views/Students.vue"),
        meta: { requiresAuth: true, title: "学员管理" },
      },
      {
        path: "courses",
        name: "Courses",
        component: () => import("@/views/Courses.vue"),
        meta: { requiresAuth: true, title: "课程管理" },
      },
      {
        path: "attendance",
        name: "Attendance",
        component: () => import("@/views/Attendance.vue"),
        meta: { requiresAuth: true, title: "考勤管理" },
      },
      {
        path: "grades",
        name: "Grades",
        component: () => import("@/views/Grades.vue"),
        meta: { requiresAuth: true, title: "成绩管理" },
      },
      {
        path: "statistics",
        name: "Statistics",
        component: () => import("@/views/Statistics.vue"),
        meta: { requiresAuth: true, title: "数据统计" },
      },
    ],
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();
  if (to.meta.requiresAuth && !authStore.token) {
    next("/login");
  } else {
    next();
  }
});

export default router;
