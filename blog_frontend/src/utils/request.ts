import axios, { type AxiosRequestConfig, type InternalAxiosRequestConfig } from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

// 创建axios实例
const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
  timeout: 10000
})

// 是否正在刷新token
let isRefreshing = false
// 等待刷新token的请求队列
let refreshSubscribers: ((token: string) => void)[] = []

// 将请求添加到队列
const subscribeTokenRefresh = (callback: (token: string) => void) => {
  refreshSubscribers.push(callback)
}

// 刷新token后，执行队列中的请求
const onTokenRefreshed = (token: string) => {
  refreshSubscribers.forEach(callback => callback(token))
  refreshSubscribers = []
}

// 请求拦截器
api.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers.Authorization = `Bearer ${userStore.token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  response => {
    const res = response.data
    // 如果响应码不是200，则判断为错误
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    } else {
      return res
    }
  },
  async error => {
    const originalRequest = error.config
    
    if (error.response) {
      // 401错误且不是刷新token的请求
      if (error.response.status === 401 && !originalRequest._retry && !originalRequest.url?.includes('/auth/refresh')) {
        const userStore = useUserStore()
        const refreshToken = localStorage.getItem('refreshToken')
        
        // 如果有refreshToken，尝试刷新
        if (refreshToken) {
          if (!isRefreshing) {
            isRefreshing = true
            originalRequest._retry = true
            
            try {
              await userStore.refreshToken()
              isRefreshing = false
              onTokenRefreshed(userStore.token || '')
              
              // 重试原请求
              originalRequest.headers.Authorization = `Bearer ${userStore.token}`
              return api(originalRequest)
            } catch (refreshError) {
              isRefreshing = false
              refreshSubscribers = []
              userStore.logout()
              ElMessage.error('登录已过期，请重新登录')
              return Promise.reject(refreshError)
            }
          } else {
            // 正在刷新token，将请求加入队列
            return new Promise(resolve => {
              subscribeTokenRefresh((token: string) => {
                originalRequest.headers.Authorization = `Bearer ${token}`
                resolve(api(originalRequest))
              })
            })
          }
        } else {
          userStore.logout()
          ElMessage.error('未授权，请重新登录')
        }
      } else {
        switch (error.response.status) {
          case 403:
            ElMessage.error('拒绝访问')
            break
          case 404:
            ElMessage.error('请求的资源不存在')
            break
          case 500:
            ElMessage.error('服务器内部错误')
            break
          default:
            ElMessage.error(`请求失败: ${error.response.data?.message || '未知错误'}`)
        }
      }
    } else {
      ElMessage.error('网络错误，请检查网络连接')
    }
    
    return Promise.reject(error)
  }
)

export default api