<script setup lang="ts">
// 导入用户状态管理store
import { useUserStore } from "@/stores/user";
// 导入Vue组合式API函数
import { onMounted, ref, computed } from "vue";
// 导入Vue Router相关函数
import { useRoute, useRouter } from "vue-router";
// 导入侧边栏组件
import Sidebar from "@/components/Sidebar.vue";
// 导入背景效果组件
import BackgroundEffects from "@/components/BackgroundEffects.vue";
// 导入鼠标跟随效果组件
import MouseFollower from "@/components/MouseFollower.vue";
// 导入滚动进度条组件
import ScrollProgress from "@/components/ScrollProgress.vue";
// 导入加载动画组件
import LoadingAnimation from "@/components/LoadingAnimation.vue";
// 导入Naive UI组件
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
  dateZhCN,
} from "naive-ui";
// 导入创建图标
import { CreateOutline } from "@vicons/ionicons5";
// 导入主题配置
import { lightThemeOverrides, darkThemeOverrides } from "@/theme";

// 初始化用户状态管理store
const userStore = useUserStore();
// 获取当前路由信息
const route = useRoute();
// 获取路由实例
const router = useRouter();
// 主题状态，默认为null（浅色主题）
const theme = ref<any>(null);

// 计算当前主题的覆盖样式
const themeOverrides = computed(() => {
  return theme.value === darkTheme ? darkThemeOverrides : lightThemeOverrides;
});

// 计算是否显示侧边栏（登录和注册页面不显示）
const showSidebar = computed(() => {
  const hiddenRoutes = ["/login", "/register"];
  return !hiddenRoutes.includes(route.path);
});

// 计算是否显示浮动操作按钮（已登录且不在特定页面时显示）
const showFab = computed(() => {
  const hiddenRoutes = ["/login", "/register", "/post/edit"];
  return userStore.isLoggedIn && !hiddenRoutes.some((r) => route.path.startsWith(r));
});

// 跳转到创建文章页面
const goToCreatePost = () => {
  router.push("/post/edit");
};

// 侧边栏折叠状态，从本地存储中读取
const sidebarCollapsed = ref(localStorage.getItem("sidebar-collapsed") === "true");

// 监听侧边栏折叠状态变化
const updateSidebarState = () => {
  sidebarCollapsed.value = localStorage.getItem("sidebar-collapsed") === "true";
};

// 检查并应用主题设置
const checkTheme = () => {
  const savedTheme = localStorage.getItem("theme");
  if (savedTheme === "dark") {
    theme.value = darkTheme;
    document.documentElement.classList.add("dark");
  } else {
    theme.value = null;
    document.documentElement.classList.remove("dark");
  }
};

// 组件挂载时执行的初始化操作
onMounted(() => {
  // 如果用户已登录，获取当前用户信息
  if (userStore.token) {
    userStore.getCurrentUser();
  }
  // 检查并应用主题设置
  checkTheme();

  // 监听本地存储变化，同步主题和侧边栏状态
  window.addEventListener("storage", (e) => {
    if (e.key === "theme") {
      checkTheme();
    }
    if (e.key === "sidebar-collapsed") {
      updateSidebarState();
    }
  });

  // 监听自定义事件，同步主题和侧边栏状态
  window.addEventListener("theme-changed", checkTheme);
  window.addEventListener("sidebar-toggled", updateSidebarState);
});
</script>

<template>
  <!-- 配置提供者，设置主题、语言等全局配置 -->
  <n-config-provider
    :theme="theme"
    :theme-overrides="themeOverrides"
    :locale="zhCN"
    :date-locale="dateZhCN"
  >
    <!-- 全局样式 -->
    <n-global-style />
    <!-- 消息提示提供者 -->
    <n-message-provider placement="top">
      <!-- 对话框提供者 -->
      <n-dialog-provider>
        <!-- 通知提供者 -->
        <n-notification-provider placement="top-right">
          <!-- 加载进度条提供者 -->
          <n-loading-bar-provider>
            <!-- 加载动画组件 -->
            <loading-animation />

            <!-- 背景效果组件 -->
            <background-effects />
            <mouse-follower />
            <scroll-progress />

            <!-- 应用容器 -->
            <div class="app-container">
              <!-- 侧边栏组件 -->
              <Sidebar v-if="showSidebar" />
              <!-- 主内容区域 -->
              <main
                class="main-content"
                :class="{ 'with-sidebar': showSidebar, 'sidebar-collapsed': sidebarCollapsed }"
              >
                <!-- 路由视图，带过渡动画 -->
                <router-view v-slot="{ Component }">
                  <transition name="page-transition" mode="out-in">
                    <component :is="Component" />
                  </transition>
                </router-view>
              </main>

              <!-- 浮动操作按钮 -->
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
/* 应用容器样式 */
.app-container {
  min-height: 100vh;
  display: flex;
  background: var(--gradient-bg);
  color: var(--text-color);
  position: relative;
}

/* 主内容区域样式 */
.main-content {
  flex: 1;
  width: 100%;
  min-height: 100vh;
  transition: margin-left 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  overflow-x: hidden;
  position: relative;
  z-index: 1;
}

/* 有侧边栏时的主内容区域样式 */
.main-content.with-sidebar {
  margin-left: 260px;
}

/* 侧边栏折叠时的主内容区域样式 */
.main-content.with-sidebar.sidebar-collapsed {
  margin-left: 72px;
}

/* 响应式设计：移动端时取消左边距 */
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

/* 页面进入动画 */
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

/* 页面离开动画 */
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

/* 浮动操作按钮样式 */
.fab-button {
  position: fixed;
  bottom: 32px;
  right: 32px;
  width: 68px;
  height: 68px;
  z-index: 999;
  box-shadow: 
    0 8px 32px rgba(139, 92, 246, 0.4),
    0 0 0 1px rgba(255, 255, 255, 0.1) inset;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  background: linear-gradient(135deg, #8b5cf6 0%, #db2777 100%);
  border: none;
}

/* 浮动按钮的渐变覆盖层 */
.fab-button::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.3) 0%, transparent 100%);
  opacity: 0;
  transition: opacity 0.4s ease;
}

/* 浮动按钮悬停效果 */
.fab-button:hover {
  transform: scale(1.15) rotate(90deg);
  box-shadow: 
    0 12px 48px rgba(139, 92, 246, 0.6),
    0 0 32px rgba(219, 39, 119, 0.4),
    0 0 0 1px rgba(255, 255, 255, 0.2) inset;
}

/* 浮动按钮悬停时的覆盖层效果 */
.fab-button:hover::before {
  opacity: 1;
}

/* 浮动按钮按下效果 */
.fab-button:active {
  transform: scale(1.05) rotate(90deg);
}

/* 浮动按钮图标旋转效果 */
.fab-button :deep(.n-button__icon) {
  transition: transform 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.2));
}

/* 浮动按钮悬停时图标反向旋转 */
.fab-button:hover :deep(.n-button__icon) {
  transform: rotate(-90deg) scale(1.1);
}

/* 浮动按钮进入和离开动画 */
.fab-enter-active,
.fab-leave-active {
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

/* 浮动按钮进入初始状态 */
.fab-enter-from {
  opacity: 0;
  transform: scale(0) rotate(-180deg);
}

/* 浮动按钮离开最终状态 */
.fab-leave-to {
  opacity: 0;
  transform: scale(0) rotate(180deg);
}

/* 响应式设计：移动端浮动按钮尺寸调整 */
@media (max-width: 768px) {
  .fab-button {
    bottom: 24px;
    right: 24px;
    width: 60px;
    height: 60px;
  }
}
</style>
