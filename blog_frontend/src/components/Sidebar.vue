<script setup lang="ts">
// 导入Vue组合式API函数
import { ref, computed, h } from "vue";
// 导入Vue Router相关函数
import { useRouter, useRoute } from "vue-router";
// 导入用户状态管理store
import { useUserStore } from "@/stores/user";
// 导入Naive UI组件
import { NIcon, NAvatar, NButton, NDropdown, NDivider } from "naive-ui";
// 导入图标组件
import {
  HomeOutline, // 首页图标
  CreateOutline, // 创建图标
  BookmarksOutline, // 书签/分类图标
  PricetagsOutline, // 标签图标
  PersonOutline, // 个人图标
  LogOutOutline, // 退出登录图标
  MoonOutline, // 月亮图标（暗色模式）
  SunnyOutline, // 太阳图标（亮色模式）
  GridOutline, // 网格图标（管理后台）
  ChevronBackOutline, // 左箭头图标
  ChevronForwardOutline, // 右箭头图标
} from "@vicons/ionicons5";

// 初始化路由
const router = useRouter();
const route = useRoute();
// 初始化用户状态管理store
const userStore = useUserStore();

// 主题状态，从本地存储获取初始值
const isDark = ref(localStorage.getItem("theme") === "dark");
// 计算属性：是否已登录
const isLoggedIn = computed(() => userStore.isLoggedIn);
// 计算属性：用户信息
const userInfo = computed(() => userStore.userInfo);
// 计算属性：是否为管理员
const isAdmin = computed(() => userStore.isAdmin);
// 侧边栏折叠状态，从本地存储获取初始值
const collapsed = ref(localStorage.getItem("sidebar-collapsed") === "true");

// 计算属性：菜单项列表
const menuItems = computed(() => {
  const items = [
    { label: "首页", key: "/", icon: HomeOutline, divider: false },
    { label: "分类", key: "/category", icon: BookmarksOutline, divider: false },
    { label: "标签", key: "/tag", icon: PricetagsOutline, divider: true },
  ];

  // 如果已登录，添加"我的文章"菜单项
  if (isLoggedIn.value) {
    items.push({ label: "我的文章", key: "/my-posts", icon: CreateOutline, divider: false });
  }

  // 如果是管理员，添加"管理后台"菜单项
  if (isAdmin.value) {
    items.push({ label: "管理后台", key: "/admin", icon: GridOutline, divider: false });
  }

  return items;
});

// 切换主题方法
const toggleDark = () => {
  isDark.value = !isDark.value;
  const html = document.documentElement;
  if (isDark.value) {
    // 切换到暗色主题
    html.classList.add("dark");
    localStorage.setItem("theme", "dark");
  } else {
    // 切换到亮色主题
    html.classList.remove("dark");
    localStorage.setItem("theme", "light");
  }
  // 触发主题变更事件
  window.dispatchEvent(new Event("theme-changed"));
};

// 处理用户退出登录方法
const handleLogout = () => {
  userStore.logout();
  router.push("/login");
};

// 导航到指定路径方法
const navigateTo = (path: string) => {
  router.push(path);
};

// 用户下拉菜单选项
const userOptions = [
  {
    label: "个人资料",
    key: "profile",
    icon: () => h(NIcon, null, { default: () => h(PersonOutline) }),
  },
  {
    label: "退出登录",
    key: "logout",
    icon: () => h(NIcon, null, { default: () => h(LogOutOutline) }),
  },
];

// 处理用户下拉菜单选择方法
const handleUserSelect = (key: string) => {
  if (key === "profile") {
    router.push("/profile");
  } else if (key === "logout") {
    handleLogout();
  }
};

// 切换侧边栏折叠状态方法
const toggleSidebar = () => {
  collapsed.value = !collapsed.value;
  localStorage.setItem("sidebar-collapsed", String(collapsed.value));
  // 触发侧边栏状态变更事件
  window.dispatchEvent(new Event("sidebar-toggled"));
};
</script>

<template>
  <!-- 侧边栏容器，根据折叠状态添加相应类名 -->
  <aside class="sidebar" :class="{ collapsed }">
    <!-- 侧边栏头部：包含logo和折叠按钮 -->
    <div class="sidebar-header">
      <!-- Logo区域，点击可导航到首页 -->
      <div class="logo" @click="navigateTo('/')">
        <!-- Logo图标容器 -->
        <div class="logo-icon-wrapper">
          <!-- Logo图标 -->
          <span class="logo-icon">✍️</span>
          <!-- Logo背景效果 -->
          <div class="logo-icon-bg"></div>
        </div>
        <!-- Logo文字容器，仅在非折叠状态下显示 -->
        <div class="logo-text-wrapper" v-if="!collapsed">
          <!-- Logo主标题 -->
          <span class="logo-text">我的博客</span>
          <!-- Logo副标题 -->
          <span class="logo-subtitle">My Blog</span>
        </div>
      </div>
      <!-- 折叠/展开按钮 -->
      <n-button text class="collapse-btn" @click="toggleSidebar">
        <n-icon :component="collapsed ? ChevronForwardOutline : ChevronBackOutline" :size="20" />
      </n-button>
    </div>

    <!-- 侧边栏内容区域：包含菜单和操作按钮 -->
    <div class="sidebar-content">
      <!-- 菜单区域 -->
      <div class="menu-section">
        <!-- 遍历菜单项，生成菜单 -->
        <template v-for="item in menuItems" :key="item.key">
          <!-- 菜单项，根据当前路由判断是否激活 -->
          <div
            class="menu-item"
            :class="{ active: route.path === item.key || route.path.startsWith(item.key + '/') }"
            @click="navigateTo(item.key)"
            :title="collapsed ? item.label : ''"
          >
            <!-- 菜单项图标 -->
            <n-icon :component="item.icon" :size="18" />
            <!-- 菜单项文本，仅在非折叠状态下显示 -->
            <span v-if="!collapsed">{{ item.label }}</span>
          </div>
          <!-- 分割线，根据菜单项配置显示 -->
          <n-divider v-if="item.divider" style="margin: 12px 0" />
        </template>
      </div>

      <!-- 操作区域：未登录时显示登录按钮 -->
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
          <!-- 登录按钮图标，仅在折叠状态下显示 -->
          <template #icon v-if="collapsed">
            <n-icon :component="PersonOutline" />
          </template>
          <!-- 登录按钮文本，仅在非折叠状态下显示 -->
          <span v-if="!collapsed">登录</span>
        </n-button>
      </div>
    </div>

    <!-- 版本信息区域 -->
    <div class="sidebar-version">
      <span>v1.0.0</span>
    </div>

    <!-- 侧边栏底部：包含主题切换和用户头像 -->
    <div class="sidebar-footer">
      <!-- 底部操作按钮组 -->
      <div class="footer-actions">
        <!-- 主题切换按钮 -->
        <n-button quaternary circle @click="toggleDark" size="small">
          <template #icon>
            <!-- 根据当前主题显示相应图标 -->
            <n-icon :component="isDark ? SunnyOutline : MoonOutline" :size="18" />
          </template>
        </n-button>

        <!-- 用户头像下拉菜单，仅在已登录时显示 -->
        <n-dropdown
          v-if="isLoggedIn"
          :options="userOptions"
          @select="handleUserSelect"
          trigger="click"
        >
          <!-- 用户头像容器 -->
          <div class="user-avatar">
            <!-- 用户头像组件 -->
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
  width: 260px;
  height: 100vh;
  background: linear-gradient(
    135deg,
    rgba(255, 255, 255, 0.95) 0%,
    rgba(250, 250, 255, 0.98) 100%
  );
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-right: 1px solid rgba(139, 92, 246, 0.1);
  display: flex;
  flex-direction: column;
  z-index: 100;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 
    4px 0 24px rgba(139, 92, 246, 0.08),
    0 0 0 1px rgba(255, 255, 255, 0.1) inset;
}

:root.dark .sidebar {
  background: linear-gradient(
    135deg,
    rgba(17, 24, 39, 0.95) 0%,
    rgba(31, 41, 55, 0.98) 100%
  );
  border-right: 1px solid rgba(139, 92, 246, 0.2);
  box-shadow: 
    4px 0 32px rgba(0, 0, 0, 0.5),
    0 0 0 1px rgba(139, 92, 246, 0.1) inset;
}

.sidebar::before {
  content: "";
  position: absolute;
  top: 0;
  right: 0;
  width: 2px;
  height: 100%;
  background: linear-gradient(
    180deg,
    transparent 0%,
    rgba(139, 92, 246, 0.5) 20%,
    rgba(219, 39, 119, 0.5) 50%,
    rgba(139, 92, 246, 0.5) 80%,
    transparent 100%
  );
  opacity: 0.3;
  animation: shimmer 3s ease-in-out infinite;
}

@keyframes shimmer {
  0%, 100% { opacity: 0.3; }
  50% { opacity: 0.6; }
}

.sidebar.collapsed {
  width: 72px;
}

.sidebar-header {
  padding: 24px 20px;
  border-bottom: 1px solid rgba(139, 92, 246, 0.1);
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  min-height: 88px;
  background: linear-gradient(
    180deg,
    rgba(139, 92, 246, 0.05) 0%,
    transparent 100%
  );
  position: relative;
  overflow: hidden;
}

.sidebar-header::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 100%;
  background: radial-gradient(
    circle at 30% 50%,
    rgba(139, 92, 246, 0.15) 0%,
    transparent 60%
  );
  pointer-events: none;
}

.sidebar-header::after {
  content: "";
  position: absolute;
  bottom: 0;
  left: 20px;
  right: 20px;
  height: 2px;
  background: linear-gradient(
    90deg,
    transparent 0%,
    rgba(139, 92, 246, 0.6) 50%,
    transparent 100%
  );
  opacity: 0.5;
}

.sidebar.collapsed .sidebar-header {
  flex-direction: column;
  padding: 20px 12px;
  gap: 12px;
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
  gap: 14px;
  cursor: pointer;
  padding: 14px 16px;
  border-radius: 16px;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  flex: 1;
  min-width: 0;
  position: relative;
  overflow: hidden;
  background: linear-gradient(
    135deg,
    rgba(139, 92, 246, 0.05) 0%,
    rgba(219, 39, 119, 0.05) 100%
  );
}

.logo::before {
  content: "";
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: conic-gradient(
    from 0deg,
    transparent 0deg,
    rgba(139, 92, 246, 0.2) 90deg,
    transparent 180deg
  );
  opacity: 0;
  transition: all 0.6s ease;
  animation: rotate 8s linear infinite;
}

@keyframes rotate {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.logo:hover::before {
  opacity: 1;
}

.logo::after {
  content: "";
  position: absolute;
  inset: 2px;
  background: linear-gradient(
    135deg,
    rgba(255, 255, 255, 0.9) 0%,
    rgba(250, 250, 255, 0.95) 100%
  );
  border-radius: 14px;
  z-index: 0;
}

:root.dark .logo::after {
  background: linear-gradient(
    135deg,
    rgba(17, 24, 39, 0.9) 0%,
    rgba(31, 41, 55, 0.95) 100%
  );
}

.logo:hover {
  transform: translateY(-3px) scale(1.02);
  box-shadow: 
    0 8px 24px rgba(139, 92, 246, 0.25),
    0 0 0 1px rgba(139, 92, 246, 0.1) inset;
}

.logo-icon-wrapper {
  position: relative;
  width: 48px;
  height: 48px;
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
  background: linear-gradient(135deg, #8b5cf6 0%, #db2777 100%);
  border-radius: 14px;
  opacity: 0.15;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 
    0 4px 12px rgba(139, 92, 246, 0.3),
    0 0 0 1px rgba(139, 92, 246, 0.1) inset;
}

.logo:hover .logo-icon-bg {
  opacity: 0.25;
  transform: scale(1.1) rotate(8deg);
  box-shadow: 
    0 8px 24px rgba(139, 92, 246, 0.4),
    0 0 20px rgba(139, 92, 246, 0.3),
    0 0 0 1px rgba(139, 92, 246, 0.2) inset;
}

.logo-icon {
  font-size: 28px;
  position: relative;
  z-index: 1;
  filter: drop-shadow(0 2px 4px rgba(139, 92, 246, 0.3));
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-4px) rotate(5deg);
  }
}

.logo-text-wrapper {
  display: flex;
  flex-direction: column;
  gap: 3px;
  flex: 1;
  min-width: 0;
  position: relative;
  z-index: 1;
}

.logo-text {
  font-size: 20px;
  font-weight: 800;
  background: linear-gradient(135deg, #8b5cf6 0%, #db2777 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  letter-spacing: 0.5px;
  transition: all 0.4s ease;
  filter: drop-shadow(0 1px 2px rgba(139, 92, 246, 0.2));
}

.logo:hover .logo-text {
  letter-spacing: 1.5px;
  filter: drop-shadow(0 2px 8px rgba(139, 92, 246, 0.4));
}

.logo-subtitle {
  font-size: 11px;
  font-weight: 600;
  color: var(--text-tertiary);
  text-transform: uppercase;
  letter-spacing: 1.5px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  opacity: 0.6;
  transition: all 0.4s ease;
}

.logo:hover .logo-subtitle {
  opacity: 1;
  letter-spacing: 2px;
  color: #8b5cf6;
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
  gap: 14px;
  padding: 12px 16px;
  border-radius: 14px;
  cursor: pointer;
  color: var(--text-secondary);
  font-size: 15px;
  font-weight: 600;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  white-space: nowrap;
  position: relative;
  overflow: hidden;
  background: transparent;
}

.menu-item::before {
  content: "";
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 0;
  background: linear-gradient(180deg, #8b5cf6 0%, #db2777 100%);
  border-radius: 0 4px 4px 0;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 0 12px rgba(139, 92, 246, 0.6);
}

.menu-item::after {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, #8b5cf6 0%, #db2777 100%);
  opacity: 0;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 14px;
}

.menu-item > * {
  position: relative;
  z-index: 1;
}

.sidebar.collapsed .menu-item {
  justify-content: center;
  padding: 12px;
}

.menu-item:hover {
  background: linear-gradient(
    135deg,
    rgba(139, 92, 246, 0.1) 0%,
    rgba(219, 39, 119, 0.1) 100%
  );
  color: var(--text-color);
  transform: translateX(6px) scale(1.02);
  box-shadow: 
    0 4px 16px rgba(139, 92, 246, 0.15),
    0 0 0 1px rgba(139, 92, 246, 0.1) inset;
}

.menu-item:hover::before {
  height: 70%;
}

.menu-item.active {
  background: linear-gradient(135deg, #8b5cf6 0%, #db2777 100%);
  color: white;
  font-weight: 700;
  box-shadow: 
    0 6px 20px rgba(139, 92, 246, 0.35),
    0 0 0 1px rgba(255, 255, 255, 0.1) inset;
  transform: translateX(6px) scale(1.02);
}

.menu-item.active::before {
  height: 85%;
  background: white;
  box-shadow: 0 0 16px rgba(255, 255, 255, 0.8);
}

.menu-item.active::after {
  opacity: 1;
}

.menu-item.active :deep(.n-icon) {
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.2));
}

.action-section {
  margin-top: 16px;
  display: flex;
  justify-content: center;
  position: relative;
}

.action-section::before {
  content: "";
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
  padding: 20px;
  border-top: 1px solid rgba(139, 92, 246, 0.1);
  background: linear-gradient(
    0deg,
    rgba(139, 92, 246, 0.05) 0%,
    transparent 100%
  );
  position: relative;
  overflow: hidden;
}

.sidebar-footer::before {
  content: "";
  position: absolute;
  top: 0;
  left: 20px;
  right: 20px;
  height: 2px;
  background: linear-gradient(
    90deg,
    transparent 0%,
    rgba(139, 92, 246, 0.6) 50%,
    transparent 100%
  );
  opacity: 0.5;
}

.sidebar-footer::after {
  content: "";
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 100%;
  background: radial-gradient(
    circle at 50% 100%,
    rgba(139, 92, 246, 0.1) 0%,
    transparent 60%
  );
  pointer-events: none;
}

.sidebar.collapsed .sidebar-footer {
  padding: 16px 12px;
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
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  background: linear-gradient(
    135deg,
    rgba(139, 92, 246, 0.05) 0%,
    rgba(219, 39, 119, 0.05) 100%
  );
}

.footer-actions :deep(.n-button::before) {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, #8b5cf6 0%, #db2777 100%);
  opacity: 0;
  border-radius: 50%;
  transition: all 0.4s ease;
}

.footer-actions :deep(.n-button:hover) {
  transform: scale(1.15) rotate(15deg);
  box-shadow: 
    0 6px 20px rgba(139, 92, 246, 0.3),
    0 0 0 1px rgba(139, 92, 246, 0.1) inset;
}

.footer-actions :deep(.n-button:hover::before) {
  opacity: 0.15;
}

.user-avatar {
  cursor: pointer;
  position: relative;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.user-avatar::before {
  content: "";
  position: absolute;
  top: -4px;
  left: -4px;
  right: -4px;
  bottom: -4px;
  border: 2px solid transparent;
  background: linear-gradient(135deg, #8b5cf6, #db2777) border-box;
  -webkit-mask: 
    linear-gradient(#fff 0 0) padding-box, 
    linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  border-radius: 50%;
  opacity: 0;
  transition: all 0.4s ease;
}

.user-avatar:hover {
  transform: scale(1.15);
}

.user-avatar:hover::before {
  opacity: 1;
  animation: pulse-ring 1.5s ease-out infinite;
}

@keyframes pulse-ring {
  0% {
    transform: scale(1);
    opacity: 1;
  }
  100% {
    transform: scale(1.3);
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
  content: "";
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

.menu-item:nth-child(1) {
  animation-delay: 0.05s;
}
.menu-item:nth-child(2) {
  animation-delay: 0.1s;
}
.menu-item:nth-child(3) {
  animation-delay: 0.15s;
}
.menu-item:nth-child(4) {
  animation-delay: 0.2s;
}
.menu-item:nth-child(5) {
  animation-delay: 0.25s;
}
.menu-item:nth-child(6) {
  animation-delay: 0.3s;
}

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
