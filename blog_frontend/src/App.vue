<script setup lang="ts">
import { useUserStore } from '@/stores/user'
import { onMounted, ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Sidebar from '@/components/Sidebar.vue'
import BackgroundEffects from '@/components/BackgroundEffects.vue'
import MouseFollower from '@/components/MouseFollower.vue'
import ScrollProgress from '@/components/ScrollProgress.vue'
import LoadingAnimation from '@/components/LoadingAnimation.vue'
import { 
  NConfigProvider, 
  NMessageProvider, 
  NDialogProvider, 
  NNotificationProvider,
  NLoadingBarProvider,
  NGlobalStyle,
  NButton,
  NIcon,
  darkTheme,
  zhCN,
  dateZhCN
} from 'naive-ui'
import { CreateOutline } from '@vicons/ionicons5'
import { lightThemeOverrides, darkThemeOverrides } from '@/theme'

const userStore = useUserStore()
const route = useRoute()
const router = useRouter()
const theme = ref<any>(null)

const themeOverrides = computed(() => {
  return theme.value === darkTheme ? darkThemeOverrides : lightThemeOverrides
})

const showSidebar = computed(() => {
  const hiddenRoutes = ['/login', '/register']
  return !hiddenRoutes.includes(route.path)
})

const showFab = computed(() => {
  const hiddenRoutes = ['/login', '/register', '/post/edit']
  return userStore.isLoggedIn && !hiddenRoutes.some(r => route.path.startsWith(r))
})

const goToCreatePost = () => {
  router.push('/post/edit')
}

const sidebarCollapsed = ref(localStorage.getItem('sidebar-collapsed') === 'true')

// 监听侧边栏折叠状态变化
const updateSidebarState = () => {
  sidebarCollapsed.value = localStorage.getItem('sidebar-collapsed') === 'true'
}

const checkTheme = () => {
  const savedTheme = localStorage.getItem('theme')
  if (savedTheme === 'dark') {
    theme.value = darkTheme
    document.documentElement.classList.add('dark')
  } else {
    theme.value = null
    document.documentElement.classList.remove('dark')
  }
}

onMounted(() => {
  if (userStore.token) {
    userStore.getCurrentUser()
  }
  checkTheme()
  
  window.addEventListener('storage', (e) => {
    if (e.key === 'theme') {
      checkTheme()
    }
    if (e.key === 'sidebar-collapsed') {
      updateSidebarState()
    }
  })
  
  window.addEventListener('theme-changed', checkTheme)
  window.addEventListener('sidebar-toggled', updateSidebarState)
})
</script>

<template>
  <n-config-provider :theme="theme" :theme-overrides="themeOverrides" :locale="zhCN" :date-locale="dateZhCN">
    <n-global-style />
    <n-message-provider placement="top">
      <n-dialog-provider>
        <n-notification-provider placement="top-right">
          <n-loading-bar-provider>
            <!-- Loading Animation -->
            <loading-animation />
            
            <!-- Background Effects -->
            <background-effects />
            <mouse-follower />
            <scroll-progress />
            
            <div class="app-container">
              <Sidebar v-if="showSidebar" />
              <main class="main-content" :class="{ 'with-sidebar': showSidebar, 'sidebar-collapsed': sidebarCollapsed }">
                <router-view v-slot="{ Component }">
                  <transition name="page-transition" mode="out-in">
                    <component :is="Component" />
                  </transition>
                </router-view>
              </main>
              
              <!-- Floating Action Button -->
              <transition name="fab">
                <n-button 
                  v-if="showFab"
                  type="primary" 
                  circle 
                  size="large"
                  class="fab-button"
                  @click="goToCreatePost"
                >
                  <template #icon>
                    <n-icon :component="CreateOutline" :size="28" />
                  </template>
                </n-button>
              </transition>
            </div>
          </n-loading-bar-provider>
        </n-notification-provider>
      </n-dialog-provider>
    </n-message-provider>
  </n-config-provider>
</template>

<style scoped>
.app-container {
  min-height: 100vh;
  display: flex;
  background-color: var(--bg-color);
  color: var(--text-color);
  position: relative;
}

.main-content {
  flex: 1;
  width: 100%;
  min-height: 100vh;
  transition: margin-left 0.3s ease;
  overflow-x: hidden;
}

.main-content.with-sidebar {
  margin-left: 240px;
}

.main-content.with-sidebar.sidebar-collapsed {
  margin-left: 72px;
}

@media (max-width: 768px) {
  .main-content.with-sidebar,
  .main-content.with-sidebar.sidebar-collapsed {
    margin-left: 0;
  }
}

/* 页面过渡动画 */
.page-transition-enter-active {
  animation: pageSlideIn 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.page-transition-leave-active {
  animation: pageSlideOut 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

@keyframes pageSlideIn {
  from {
    opacity: 0;
    transform: translateY(20px) scale(0.98);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@keyframes pageSlideOut {
  from {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
  to {
    opacity: 0;
    transform: translateY(-10px) scale(0.99);
  }
}

/* Floating Action Button */
.fab-button {
  position: fixed;
  bottom: 32px;
  right: 32px;
  width: 64px;
  height: 64px;
  z-index: 999;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-hover) 100%);
  border: none;
}

.fab-button::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.2) 0%, transparent 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.fab-button:hover {
  transform: scale(1.1) rotate(90deg);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.3);
}

.fab-button:hover::before {
  opacity: 1;
}

.fab-button:active {
  transform: scale(1.05) rotate(90deg);
}

.fab-button :deep(.n-button__icon) {
  transition: transform 0.3s ease;
}

.fab-button:hover :deep(.n-button__icon) {
  transform: rotate(-90deg);
}

/* FAB 动画 */
.fab-enter-active,
.fab-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.fab-enter-from {
  opacity: 0;
  transform: scale(0) rotate(-180deg);
}

.fab-leave-to {
  opacity: 0;
  transform: scale(0) rotate(180deg);
}

/* 响应式 */
@media (max-width: 768px) {
  .fab-button {
    bottom: 24px;
    right: 24px;
    width: 56px;
    height: 56px;
  }
}
</style>