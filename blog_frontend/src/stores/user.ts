import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '@/api'
import router from '@/router'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref<string | null>(localStorage.getItem('token'))
  const userInfo = ref<any>(null)
  
  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value?.role === 'ADMIN')
  
  // 方法
  const login = async (loginForm: { username: string; password: string }) => {
    try {
      const res = await authApi.login(loginForm)
      token.value = res.data.token
      localStorage.setItem('token', token.value)
      
      // 获取用户信息
      await getCurrentUser()
      
      return Promise.resolve(res)
    } catch (error) {
      return Promise.reject(error)
    }
  }
  
  const register = async (registerForm: { username: string; password: string; nickname: string; email: string }) => {
    try {
      const res = await authApi.register(registerForm)
      return Promise.resolve(res)
    } catch (error) {
      return Promise.reject(error)
    }
  }
  
  const logout = () => {
    token.value = null
    userInfo.value = null
    localStorage.removeItem('token')
    router.push('/login')
  }
  
  const getCurrentUser = async () => {
    try {
      const res = await authApi.getCurrentUser()
      userInfo.value = res.data
      return Promise.resolve(res)
    } catch (error) {
      logout()
      return Promise.reject(error)
    }
  }
  
  const refreshToken = async () => {
    try {
      const res = await authApi.refreshToken(token.value!)
      token.value = res.data.token
      localStorage.setItem('token', token.value)
      return Promise.resolve(res)
    } catch (error) {
      logout()
      return Promise.reject(error)
    }
  }
  
  // 初始化时获取用户信息
  if (token.value) {
    getCurrentUser()
  }
  
  return {
    token,
    userInfo,
    isLoggedIn,
    isAdmin,
    login,
    register,
    logout,
    getCurrentUser,
    refreshToken
  }
})