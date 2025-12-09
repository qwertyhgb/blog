// 导入Pinia状态管理库
import { defineStore } from 'pinia'
// 导入Vue组合式API函数
import { ref, computed } from 'vue'
// 导入认证API
import { authApi } from '@/api'
// 导入路由实例
import router from '@/router'
// 导入用户类型定义
import type { User } from '@/types'

// 定义用户状态管理store
export const useUserStore = defineStore('user', () => {
  // 状态定义
  // 用户访问令牌，从本地存储中读取
  const token = ref<string | null>(localStorage.getItem('token'))
  // 刷新令牌，从本地存储中读取
  const refreshTokenValue = ref<string | null>(localStorage.getItem('refreshToken'))
  // 用户信息
  const userInfo = ref<User | null>(null)

  // 计算属性
  // 是否已登录，通过判断token是否存在
  const isLoggedIn = computed(() => !!token.value)
  // 是否为管理员，通过判断用户角色
  const isAdmin = computed(() => userInfo.value?.role === 'ADMIN')

  // 方法
  // 用户登出方法
  const logout = () => {
    // 清空状态
    token.value = null
    refreshTokenValue.value = null
    userInfo.value = null
    // 清空本地存储
    localStorage.removeItem('token')
    localStorage.removeItem('refreshToken')
    // 跳转到登录页面
    router.push('/login')
  }

  // 获取当前用户信息方法
  const getCurrentUser = async () => {
    try {
      // 调用API获取当前用户信息
      const res = await authApi.getCurrentUser()
      // 更新用户信息状态
      userInfo.value = res.data
      return Promise.resolve(res)
    } catch (error) {
      // 不在这里执行登出，让请求拦截器处理token刷新
      return Promise.reject(error)
    }
  }

  // 用户登录方法
  const login = async (loginForm: { username: string; password: string }) => {
    try {
      // 调用登录API
      const res = await authApi.login(loginForm)
      // 更新token和刷新令牌
      token.value = res.data.token
      refreshTokenValue.value = res.data.refreshToken
      
      // 保存token到本地存储
      if (token.value) {
        localStorage.setItem('token', token.value)
      }
      // 保存刷新令牌到本地存储
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

  // 用户注册方法
  const register = async (registerForm: { username: string; password: string; nickname: string; email: string }) => {
    try {
      // 调用注册API
      const res = await authApi.register(registerForm)
      return Promise.resolve(res)
    } catch (error) {
      return Promise.reject(error)
    }
  }

  // 刷新访问令牌方法
  const doRefreshToken = async () => {
    try {
      // 获取当前刷新令牌
      const currentRefreshToken = refreshTokenValue.value || localStorage.getItem('refreshToken')
      if (!currentRefreshToken) {
        throw new Error('No refresh token')
      }
      
      // 调用刷新令牌API
      const res = await authApi.refreshToken(currentRefreshToken)
      // 更新访问令牌
      token.value = res.data.token
      // 保存新的访问令牌到本地存储
      if (token.value) {
        localStorage.setItem('token', token.value)
      }
      return Promise.resolve(res)
    } catch (error) {
      // 刷新令牌失败，执行登出
      logout()
      return Promise.reject(error)
    }
  }

  // 不在这里初始化获取用户信息，让路由守卫处理

  // 返回状态和方法
  return {
    token,
    refreshTokenValue,
    userInfo,
    user: userInfo, // 兼容性别名
    isLoggedIn,
    isAdmin,
    login,
    register,
    logout,
    getCurrentUser,
    refreshToken: doRefreshToken
  }
})