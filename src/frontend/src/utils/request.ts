import axios from 'axios'
import { useAuthStore } from '@/store/auth'
import { ElMessage } from 'element-plus'

const service = axios.create({
  baseURL: '/api',
  timeout: 5000,
})

service.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore()
    if (authStore.token) {
      config.headers.Authorization = `Bearer ${authStore.token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

service.interceptors.response.use(
  (response) => {
    return response
  },
  (error) => {
    if (error.response && error.response.status === 401) {
      const authStore = useAuthStore()
      authStore.logout()
      location.reload()
    }
    // Show detailed error message from backend if available
    const errorMsg = error.response?.data || error.message || 'Error'
    ElMessage.error(typeof errorMsg === 'string' ? errorMsg : JSON.stringify(errorMsg))
    return Promise.reject(error)
  }
)

export default service
