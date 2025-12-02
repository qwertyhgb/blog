<script setup lang="ts">
import { ref, computed, h } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { 
  NLayout, 
  NLayoutSider, 
  NLayoutHeader, 
  NLayoutContent, 
  NMenu, 
  NButton, 
  NDropdown, 
  NAvatar, 
  NIcon,
  type MenuOption
} from 'naive-ui'
import { 
  HomeOutline,
  DocumentTextOutline,
  FolderOpenOutline,
  PricetagOutline,
  ChatbubbleOutline,
  PersonOutline,
  SettingsOutline,
  MenuOutline,
  ChevronDownOutline,
  LogOutOutline
} from '@vicons/ionicons5'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const collapsed = ref(false)
const activeKey = computed(() => route.path)

const userInfo = computed(() => userStore.userInfo)

function renderIcon(icon: any) {
  return () => h(NIcon, null, { default: () => h(icon) })
}

const menuOptions: MenuOption[] = [
  {
    label: '仪表盘',
    key: '/admin/dashboard',
    icon: renderIcon(HomeOutline)
  },
  {
    label: '文章管理',
    key: '/admin/posts',
    icon: renderIcon(DocumentTextOutline)
  },
  {
    label: '分类管理',
    key: '/admin/categories',
    icon: renderIcon(FolderOpenOutline)
  },
  {
    label: '标签管理',
    key: '/admin/tags',
    icon: renderIcon(PricetagOutline)
  },
  {
    label: '评论管理',
    key: '/admin/comments',
    icon: renderIcon(ChatbubbleOutline)
  },
  {
    label: '用户管理',
    key: '/admin/users',
    icon: renderIcon(PersonOutline)
  },
  {
    label: '系统设置',
    key: '/admin/settings',
    icon: renderIcon(SettingsOutline)
  }
]

const userOptions = [
  {
    label: '个人中心',
    key: 'profile',
    icon: renderIcon(PersonOutline)
  },
  {
    label: '退出登录',
    key: 'logout',
    icon: renderIcon(LogOutOutline)
  }
]

const handleMenuUpdate = (key: string) => {
  router.push(key)
}

const handleUserSelect = (key: string) => {
  if (key === 'profile') {
    router.push('/profile')
  } else if (key === 'logout') {
    userStore.logout()
    router.push('/login')
  }
}
</script>

<template>
  <n-layout has-sider class="admin-layout">
    <n-layout-sider
      collapse-mode="width"
      :collapsed-width="64"
      :width="240"
      :collapsed="collapsed"
      show-trigger="bar"
      @collapse="collapsed = true"
      @expand="collapsed = false"
      class="admin-sider"
      :native-scrollbar="false"
    >
      <div class="admin-logo">
        <span v-if="!collapsed">📊 博客管理</span>
        <span v-else>📊</span>
      </div>
      <n-menu
        :collapsed="collapsed"
        :collapsed-width="64"
        :collapsed-icon-size="22"
        :options="menuOptions"
        :value="activeKey"
        @update:value="handleMenuUpdate"
      />
    </n-layout-sider>
    
    <n-layout>
      <n-layout-header bordered class="admin-header">
        <div class="header-left">
          <n-button quaternary circle @click="collapsed = !collapsed">
            <template #icon>
              <n-icon :component="MenuOutline" />
            </template>
          </n-button>
        </div>
        
        <div class="header-right">
          <n-dropdown :options="userOptions" @select="handleUserSelect">
            <div class="user-info">
              <n-avatar 
                round 
                size="small" 
                :src="userInfo?.avatar"
                :fallback-src="'https://07akioni.oss-cn-beijing.aliyuncs.com/07akioni.jpeg'"
              />
              <span class="username">{{ userInfo?.nickname || userInfo?.username || '管理员' }}</span>
              <n-icon :component="ChevronDownOutline" />
            </div>
          </n-dropdown>
        </div>
      </n-layout-header>
      
      <n-layout-content class="admin-content">
        <div class="content-wrapper">
          <router-view />
        </div>
      </n-layout-content>
    </n-layout>
  </n-layout>
</template>

<style scoped>
.admin-layout {
  height: 100vh;
}

.admin-sider {
  background-color: var(--sidebar-bg);
}

.admin-logo {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: 700;
  color: var(--primary-color);
}

.admin-header {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  background-color: var(--card-bg);
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
  gap: 8px;
  padding: 8px 12px;
  border-radius: 8px;
  transition: background-color 0.2s ease;
}

.user-info:hover {
  background-color: var(--bg-hover);
}

.username {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-color);
}

.admin-content {
  background-color: var(--bg-secondary);
  padding: 0;
  min-height: calc(100vh - 64px);
  overflow-y: auto;
}

.content-wrapper {
  background-color: var(--card-bg);
  padding: 32px;
  min-height: calc(100vh - 64px);
}
</style>