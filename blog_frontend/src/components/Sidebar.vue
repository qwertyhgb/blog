<script setup lang="ts">
import { ref, computed, h } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { 
  NIcon, 
  NAvatar, 
  NButton,
  NDropdown,
  NDivider
} from 'naive-ui'
import { 
  HomeOutline,
  CreateOutline,
  BookmarksOutline,
  PricetagsOutline,
  PersonOutline,
  LogOutOutline,
  MoonOutline,
  SunnyOutline,
  GridOutline,
  ChevronBackOutline,
  ChevronForwardOutline
} from '@vicons/ionicons5'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const isDark = ref(localStorage.getItem('theme') === 'dark')
const isLoggedIn = computed(() => userStore.isLoggedIn)
const userInfo = computed(() => userStore.userInfo)
const isAdmin = computed(() => userStore.isAdmin)
const collapsed = ref(localStorage.getItem('sidebar-collapsed') === 'true')

const menuItems = computed(() => {
  const items = [
    { label: '首页', key: '/', icon: HomeOutline, divider: false },
    { label: '分类', key: '/category', icon: BookmarksOutline, divider: false },
    { label: '标签', key: '/tag', icon: PricetagsOutline, divider: true },
  ]
  
  if (isLoggedIn.value) {
    items.push({ label: '我的文章', key: '/my-posts', icon: CreateOutline, divider: false })
  }
  
  if (isAdmin.value) {
    items.push({ label: '管理后台', key: '/admin', icon: GridOutline, divider: false })
  }
  
  return items
})

const toggleDark = () => {
  isDark.value = !isDark.value
  const html = document.documentElement
  if (isDark.value) {
    html.classList.add('dark')
    localStorage.setItem('theme', 'dark')
  } else {
    html.classList.remove('dark')
    localStorage.setItem('theme', 'light')
  }
  window.dispatchEvent(new Event('theme-changed'))
}

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}

const navigateTo = (path: string) => {
  router.push(path)
}

const userOptions = [
  {
    label: '个人资料',
    key: 'profile',
    icon: () => h(NIcon, null, { default: () => h(PersonOutline) })
  },
  {
    label: '退出登录',
    key: 'logout',
    icon: () => h(NIcon, null, { default: () => h(LogOutOutline) })
  }
]

const handleUserSelect = (key: string) => {
  if (key === 'profile') {
    router.push('/profile')
  } else if (key === 'logout') {
    handleLogout()
  }
}

const toggleSidebar = () => {
  collapsed.value = !collapsed.value
  localStorage.setItem('sidebar-collapsed', String(collapsed.value))
  window.dispatchEvent(new Event('sidebar-toggled'))
}
</script>

<template>
  <aside class="sidebar" :class="{ collapsed }">
    <div class="sidebar-header">
      <div class="logo" @click="navigateTo('/')">
        <div class="logo-icon-wrapper">
          <span class="logo-icon">✍️</span>
          <div class="logo-icon-bg"></div>
        </div>
        <div class="logo-text-wrapper" v-if="!collapsed">
          <span class="logo-text">我的博客</span>
          <span class="logo-subtitle">My Blog</span>
        </div>
      </div>
      <n-button text class="collapse-btn" @click="toggleSidebar">
        <n-icon :component="collapsed ? ChevronForwardOutline : ChevronBackOutline" :size="20" />
      </n-button>
    </div>

    <div class="sidebar-content">
      <div class="menu-section">
        <template v-for="item in menuItems" :key="item.key">
          <div 
            class="menu-item"
            :class="{ active: route.path === item.key || route.path.startsWith(item.key + '/') }"
            @click="navigateTo(item.key)"
            :title="collapsed ? item.label : ''"
          >
            <n-icon :component="item.icon" :size="18" />
            <span v-if="!collapsed">{{ item.label }}</span>
          </div>
          <n-divider v-if="item.divider" style="margin: 12px 0" />
        </template>
      </div>

      <div class="action-section" v-if="!isLoggedIn">
        <n-button 
          type="primary" 
          :block="!collapsed"
          :circle="collapsed"
          size="large"
          @click="navigateTo('/login')"
          strong
          :title="collapsed ? '登录' : ''"
        >
          <template #icon v-if="collapsed">
            <n-icon :component="PersonOutline" />
          </template>
          <span v-if="!collapsed">登录</span>
        </n-button>
      </div>
    </div>

    <div class="sidebar-version">
      <span>v1.0.0</span>
    </div>

    <div class="sidebar-footer">
      <div class="footer-actions">
        <n-button quaternary circle @click="toggleDark" size="small">
          <template #icon>
            <n-icon :component="isDark ? SunnyOutline : MoonOutline" :size="18" />
          </template>
        </n-button>
        
        <n-dropdown v-if="isLoggedIn" :options="userOptions" @select="handleUserSelect" trigger="click">
          <div class="user-avatar">
            <n-avatar 
              round 
              size="small" 
              :src="userInfo?.avatar"
              :fallback-src="'https://07akioni.oss-cn-beijing.aliyuncs.com/07akioni.jpeg'"
            />
          </div>
        </n-dropdown>
      </div>
    </div>
  </aside>
</template>

<style scoped>
.sidebar {
  position: fixed;
  left: 0;
  top: 0;
  width: 240px;
  height: 100vh;
  background-color: var(--sidebar-bg);
  border-right: 1px solid var(--border-color);
  display: flex;
  flex-direction: column;
  z-index: 100;
  transition: width 0.3s ease, background-color 0.2s ease;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.05);
}

.sidebar::before {
  content: '';
  position: absolute;
  top: 0;
  right: 0;
  width: 1px;
  height: 100%;
  background: linear-gradient(180deg, 
    transparent 0%, 
    var(--primary-color) 20%, 
    var(--primary-color) 80%, 
    transparent 100%
  );
  opacity: 0.1;
}

.sidebar.collapsed {
  width: 72px;
}

.sidebar-header {
  padding: 20px 16px;
  border-bottom: 1px solid var(--border-color);
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  min-height: 80px;
  background: linear-gradient(180deg, var(--bg-secondary) 0%, transparent 100%);
  position: relative;
}

.sidebar-header::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 16px;
  right: 16px;
  height: 1px;
  background: linear-gradient(90deg, transparent 0%, var(--primary-color) 50%, transparent 100%);
  opacity: 0.3;
}

.sidebar.collapsed .sidebar-header {
  flex-direction: column;
  padding: 16px 8px;
  gap: 8px;
}

.collapse-btn {
  flex-shrink: 0;
  opacity: 0.6;
  transition: opacity 0.2s ease;
  padding: 4px;
}

.collapse-btn:hover {
  opacity: 1;
  background-color: var(--bg-hover);
  border-radius: 4px;
}

.sidebar.collapsed .collapse-btn {
  margin: 0 auto;
}

.sidebar.collapsed .logo {
  padding: 8px;
  justify-content: center;
}

.sidebar.collapsed .logo-icon-wrapper {
  width: 36px;
  height: 36px;
}

.sidebar.collapsed .logo-icon {
  font-size: 20px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  padding: 12px;
  border-radius: 12px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  flex: 1;
  min-width: 0;
  position: relative;
  overflow: hidden;
}

.logo::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-hover) 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
  border-radius: 12px;
}

.logo:hover::before {
  opacity: 0.1;
}

.logo:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.logo-icon-wrapper {
  position: relative;
  width: 40px;
  height: 40px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1;
}

.logo-icon-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 10px;
  opacity: 0.15;
  transition: all 0.3s ease;
}

.logo:hover .logo-icon-bg {
  opacity: 0.25;
  transform: scale(1.1) rotate(5deg);
}

.logo-icon {
  font-size: 24px;
  position: relative;
  z-index: 1;
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-3px);
  }
}

.logo-text-wrapper {
  display: flex;
  flex-direction: column;
  gap: 2px;
  flex: 1;
  min-width: 0;
  position: relative;
  z-index: 1;
}

.logo-text {
  font-size: 18px;
  font-weight: 700;
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-hover) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  letter-spacing: 0.5px;
  transition: all 0.3s ease;
}

.logo:hover .logo-text {
  letter-spacing: 1px;
}

.logo-subtitle {
  font-size: 11px;
  font-weight: 500;
  color: var(--text-tertiary);
  text-transform: uppercase;
  letter-spacing: 1px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  opacity: 0.7;
  transition: opacity 0.3s ease;
}

.logo:hover .logo-subtitle {
  opacity: 1;
}

.sidebar-content {
  flex: 1;
  padding: 20px 16px;
  overflow-y: auto;
  overflow-x: hidden;
}

.sidebar-content::-webkit-scrollbar {
  width: 6px;
}

.sidebar-content::-webkit-scrollbar-track {
  background: transparent;
}

.sidebar-content::-webkit-scrollbar-thumb {
  background: var(--border-color);
  border-radius: 3px;
  transition: background 0.3s ease;
}

.sidebar-content::-webkit-scrollbar-thumb:hover {
  background: var(--primary-color);
}

.menu-section {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.sidebar.collapsed .menu-section {
  padding: 0 8px;
}

.menu-section :deep(.n-divider) {
  margin: 16px 0;
  background: linear-gradient(90deg, transparent 0%, var(--border-color) 50%, transparent 100%);
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 14px;
  border-radius: 10px;
  cursor: pointer;
  color: var(--text-secondary);
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  white-space: nowrap;
  position: relative;
  overflow: hidden;
}

.menu-item::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 0;
  background: linear-gradient(180deg, var(--primary-color) 0%, var(--primary-hover) 100%);
  border-radius: 0 3px 3px 0;
  transition: height 0.3s ease;
}

.menu-item::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-hover) 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
  border-radius: 10px;
}

.menu-item > * {
  position: relative;
  z-index: 1;
}

.sidebar.collapsed .menu-item {
  justify-content: center;
  padding: 10px;
}

.menu-item:hover {
  background-color: var(--bg-hover);
  color: var(--text-color);
  transform: translateX(4px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.menu-item:hover::before {
  height: 60%;
}

.menu-item.active {
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-hover) 100%);
  color: white;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transform: translateX(4px);
}

.menu-item.active::before {
  height: 80%;
  background: white;
}

.menu-item.active::after {
  opacity: 1;
}

.action-section {
  margin-top: 16px;
  display: flex;
  justify-content: center;
  position: relative;
}

.action-section::before {
  content: '';
  position: absolute;
  top: -8px;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent 0%, var(--border-color) 50%, transparent 100%);
}

.sidebar.collapsed .action-section {
  padding: 0 8px;
}

.action-section :deep(.n-button) {
  border-radius: 12px;
  font-weight: 600;
  letter-spacing: 0.5px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.action-section :deep(.n-button:hover) {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
}

.action-section :deep(.n-button:active) {
  transform: translateY(0);
}

.sidebar-footer {
  padding: 16px;
  border-top: 1px solid var(--border-color);
  background: linear-gradient(0deg, var(--bg-secondary) 0%, transparent 100%);
  position: relative;
}

.sidebar-footer::before {
  content: '';
  position: absolute;
  top: 0;
  left: 16px;
  right: 16px;
  height: 1px;
  background: linear-gradient(90deg, transparent 0%, var(--primary-color) 50%, transparent 100%);
  opacity: 0.3;
}

.sidebar.collapsed .sidebar-footer {
  padding: 12px 8px;
}

.footer-actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.sidebar.collapsed .footer-actions {
  flex-direction: column;
  gap: 12px;
}

.footer-actions :deep(.n-button) {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
}

.footer-actions :deep(.n-button::before) {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-hover) 100%);
  opacity: 0;
  border-radius: 50%;
  transition: opacity 0.3s ease;
}

.footer-actions :deep(.n-button:hover) {
  transform: scale(1.1) rotate(10deg);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.footer-actions :deep(.n-button:hover::before) {
  opacity: 0.1;
}

.user-avatar {
  cursor: pointer;
  position: relative;
  transition: all 0.3s ease;
}

.user-avatar::before {
  content: '';
  position: absolute;
  top: -4px;
  left: -4px;
  right: -4px;
  bottom: -4px;
  border: 2px solid var(--primary-color);
  border-radius: 50%;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.user-avatar:hover {
  transform: scale(1.1);
}

.user-avatar:hover::before {
  opacity: 0.5;
  animation: pulse-ring 1.5s ease-out infinite;
}

@keyframes pulse-ring {
  0% {
    transform: scale(1);
    opacity: 0.5;
  }
  100% {
    transform: scale(1.2);
    opacity: 0;
  }
}

.sidebar-version {
  padding: 12px 16px;
  text-align: center;
  font-size: 11px;
  font-weight: 600;
  color: var(--text-tertiary);
  border-top: 1px solid var(--border-color);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.sidebar-version::before {
  content: '';
  position: absolute;
  top: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 40px;
  height: 1px;
  background: linear-gradient(90deg, transparent 0%, var(--primary-color) 50%, transparent 100%);
  opacity: 0.5;
}

.sidebar-version span {
  display: inline-block;
  padding: 4px 12px;
  background: var(--bg-secondary);
  border-radius: 12px;
  transition: all 0.3s ease;
  letter-spacing: 0.5px;
}

.sidebar-version:hover span {
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-hover) 100%);
  color: white;
  transform: scale(1.05);
}

.sidebar.collapsed .sidebar-version {
  display: none;
}

/* Animations */
@keyframes slideInLeft {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.menu-item {
  animation: slideInLeft 0.4s ease-out;
}

.menu-item:nth-child(1) { animation-delay: 0.05s; }
.menu-item:nth-child(2) { animation-delay: 0.1s; }
.menu-item:nth-child(3) { animation-delay: 0.15s; }
.menu-item:nth-child(4) { animation-delay: 0.2s; }
.menu-item:nth-child(5) { animation-delay: 0.25s; }
.menu-item:nth-child(6) { animation-delay: 0.3s; }

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.action-section {
  animation: fadeIn 0.5s ease-out 0.4s both;
}

.sidebar-footer {
  animation: fadeIn 0.5s ease-out 0.5s both;
}

/* Hover Effects */
.menu-item:active {
  transform: translateX(2px) scale(0.98);
}

.collapse-btn:active {
  transform: scale(0.9);
}

/* Focus Styles */
.menu-item:focus-visible {
  outline: 2px solid var(--primary-color);
  outline-offset: 2px;
}

/* Responsive */
@media (max-width: 768px) {
  .sidebar {
    transform: translateX(-100%);
  }

  .sidebar.collapsed {
    width: 240px;
  }
}
</style>
