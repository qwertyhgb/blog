import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '@/api'
import router from '@/router'
import type { User } from '@/types'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref<string | null>(localStorage.getItem('token'))
  const refreshTokenValue = ref<string | null>(localStorage.getItem('refreshToken'))
  const userInfo = ref<User | null>(null)

  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value?.role === 'ADMIN')

  // 方法
  const logout = () => {
    token.value = null
    refreshTokenValue.value = null
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('refreshToken')
    router.push('/login')
  }

  const getCurrentUser = async () => {
    try {
      const res = await authApi.getCurrentUser()
      userInfo.value = res.data
      return Promise.resolve(res)
    } catch (error) {
      // 不在这里logout，让request拦截器处理token刷新
      return Promise.reject(error)
    }
  }

  const login = async (loginForm: { username: string; password: string }) => {
    try {
      const res = await authApi.login(loginForm)
      token.value = res.data.token
      refreshTokenValue.value = res.data.refreshToken
      
      if (token.value) {
        localStorage.setItem('token', token.value)
      }
      if (refreshTokenValue.value) {
        localStorage.setItem('refreshToken', refreshTokenValue.value)
      }

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

  const doRefreshToken = async () => {
    try {
      const currentRefreshToken = refreshTokenValue.value || localStorage.getItem('refreshToken')
      if (!currentRefreshToken) {
        throw new Error('No refresh token')
      }
      
      const res = await authApi.refreshToken(currentRefreshToken)
      token.value = res.data.token
      if (token.value) {
        localStorage.setItem('token', token.value)
      }
      return Promise.resolve(res)
    } catch (error) {
      logout()
      return Promise.reject(error)
    }
  }

  // 不在这里初始化获取用户信息，让路由守卫处理

  return {
    token,
    refreshTokenValue,
    userInfo,
    user: userInfo, // alias for compatibility
    isLoggedIn,
    isAdmin,
    login,
    register,
    logout,
    getCurrentUser,
    refreshToken: doRefreshToken
  }
})