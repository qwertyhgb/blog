<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { 
  Menu, 
  Document, 
  Setting, 
  User, 
  ArrowDown,
  House,
  FolderOpened,
  PriceTag,
  ChatDotRound
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const isCollapse = ref(false)
const activeMenu = computed(() => route.path)

const userInfo = computed(() => userStore.userInfo)

const menuItems = [
  {
    index: '/admin/dashboard',
    title: '仪表盘',
    icon: House
  },
  {
    index: '/admin/posts',
    title: '文章管理',
    icon: Document
  },
  {
    index: '/admin/categories',
    title: '分类管理',
    icon: FolderOpened
  },
  {
    index: '/admin/tags',
    title: '标签管理',
    icon: PriceTag
  },
  {
    index: '/admin/comments',
    title: '评论管理',
    icon: ChatDotRound
  },
  {
    index: '/admin/users',
    title: '用户管理',
    icon: User
  },
  {
    index: '/admin/settings',
    title: '系统设置',
    icon: Setting
  }
]

const toggleSidebar = () => {
  isCollapse.value = !isCollapse.value
}

const handleLogout = () => {
  userStore.logout()
}

const goToProfile = () => {
  router.push('/profile')
}

const handleCommand = (command: string) => {
  if (command === 'profile') {
    goToProfile()
  } else if (command === 'logout') {
    handleLogout()
  }
}
</script>

<template>
  <div class="admin-layout">
    <el-container>
      <el-aside :width="isCollapse ? '64px' : '200px'" class="admin-sidebar">
        <div class="admin-logo">
          <span v-if="!isCollapse">博客管理</span>
          <span v-else>管</span>
        </div>
        
        <el-menu
          :default-active="activeMenu"
          class="admin-menu"
          :collapse="isCollapse"
          router
        >
          <el-menu-item
            v-for="item in menuItems"
            :key="item.index"
            :index="item.index"
          >
            <el-icon><component :is="item.icon" /></el-icon>
            <template #title>{{ item.title }}</template>
          </el-menu-item>
        </el-menu>
      </el-aside>
      
      <el-container>
        <el-header class="admin-header">
          <div class="header-left">
            <el-button :icon="Menu" @click="toggleSidebar" />
          </div>
          
          <div class="header-right">
            <el-dropdown @command="handleCommand">
              <span class="user-info">
                <el-avatar :size="32" :src="userInfo?.avatar">
                  {{ userInfo?.nickname ? userInfo.nickname.charAt(0) : 'U' }}
                </el-avatar>
                <span class="username">{{ userInfo?.nickname || userInfo?.username || '管理员' }}</span>
                <el-icon class="el-icon--right"><arrow-down /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                  <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>
        
        <el-main class="admin-main">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<style scoped>
.admin-layout {
  height: 100vh;
}

.admin-sidebar {
  background-color: #304156;
  transition: width 0.3s;
}

.admin-logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.admin-menu {
  border-right: none;
  background-color: #304156;
}

.admin-menu .el-menu-item {
  color: #bfcbd9;
}

.admin-menu .el-menu-item:hover {
  background-color: #263445;
  color: #fff;
}

.admin-menu .el-menu-item.is-active {
  background-color: #409eff;
  color: #fff;
}

.admin-header {
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
}

.header-right {
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

.admin-main {
  background-color: #f0f2f5;
  padding: 20px;
}
</style>