<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useCategoryStore } from '@/stores/category'
import { Search, Menu, User, ArrowDown } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const categoryStore = useCategoryStore()

const searchKeyword = ref('')
const showUserMenu = ref(false)

// 计算属性
const isLoggedIn = computed(() => userStore.isLoggedIn)
const isAdmin = computed(() => userStore.isAdmin)
const userInfo = computed(() => userStore.userInfo)

// 方法
const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push({
      path: '/',
      query: { keyword: searchKeyword.value.trim() }
    })
  }
}

const handleLogout = () => {
  userStore.logout()
  showUserMenu.value = false
}

const goToProfile = () => {
  router.push('/profile')
  showUserMenu.value = false
}

const goToAdmin = () => {
  router.push('/admin')
  showUserMenu.value = false
}

// 处理下拉菜单命令
const handleCommand = (command: string) => {
  if (command === 'profile') {
    goToProfile()
  } else if (command === 'admin') {
    goToAdmin()
  } else if (command === 'logout') {
    handleLogout()
  }
}

// 初始化分类数据
categoryStore.fetchCategories()
</script>

<template>
  <header class="header">
    <div class="header-container">
      <div class="logo">
        <router-link to="/">个人博客</router-link>
      </div>
      
      <nav class="nav-menu">
        <router-link to="/" class="nav-item">首页</router-link>
        <el-dropdown v-if="categoryStore.categories.length > 0">
          <span class="nav-item">
            分类
            <el-icon class="el-icon--right"><arrow-down /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item v-for="category in categoryStore.categories" :key="category.id">
                <router-link :to="`/category/${category.id}`">{{ category.name }}</router-link>
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        <router-link to="/tag" class="nav-item">标签</router-link>
      </nav>
      
      <div class="search-box">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索文章"
          class="search-input"
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button :icon="Search" @click="handleSearch" />
          </template>
        </el-input>
      </div>
      
      <div class="user-menu">
        <template v-if="isLoggedIn">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" :src="userInfo.avatar">
                {{ userInfo.nickname ? userInfo.nickname.charAt(0) : 'U' }}
              </el-avatar>
              <span class="username">{{ userInfo.nickname || userInfo.username }}</span>
              <el-icon class="el-icon--right"><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item v-if="isAdmin" command="admin">管理后台</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <router-link to="/login" class="login-btn">登录</router-link>
          <router-link to="/register" class="register-btn">注册</router-link>
        </template>
      </div>
    </div>
  </header>
</template>



<style scoped>
.header {
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-container {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  padding: 0 20px;
  height: 60px;
}

.logo a {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
  text-decoration: none;
}

.nav-menu {
  display: flex;
  margin-left: 40px;
}

.nav-item {
  margin-right: 20px;
  color: #333;
  text-decoration: none;
  font-size: 16px;
  display: flex;
  align-items: center;
  cursor: pointer;
}

.nav-item:hover {
  color: #409eff;
}

.search-box {
  flex: 1;
  max-width: 400px;
  margin: 0 20px;
}

.search-input {
  width: 100%;
}

.user-menu {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.username {
  margin: 0 8px;
  font-size: 14px;
}

.login-btn, .register-btn {
  margin-left: 10px;
  padding: 6px 12px;
  border-radius: 4px;
  text-decoration: none;
  font-size: 14px;
}

.login-btn {
  color: #409eff;
  border: 1px solid #409eff;
}

.register-btn {
  color: #fff;
  background-color: #409eff;
}

.login-btn:hover {
  background-color: rgba(64, 158, 255, 0.1);
}

.register-btn:hover {
  background-color: #66b1ff;
}
</style>