<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { 
  NTabs, 
  NTabPane, 
  NForm, 
  NFormItem, 
  NInput, 
  NButton, 
  NUpload, 
  NIcon,
  NDescriptions,
  NDescriptionsItem,
  NTag,
  NSpace,
  NAlert,
  NGrid,
  NGridItem,
  NStatistic,
  NTimeline,
  NTimelineItem,
  NDivider,
  NSwitch,
  NSelect,
  useMessage,
  type FormInst,
  type UploadFileInfo
} from 'naive-ui'
import { 
  AddOutline, 
  CreateOutline, 
  EyeOutline, 
  HeartOutline, 
  ChatbubbleOutline 
} from '@vicons/ionicons5'

const router = useRouter()
const userStore = useUserStore()
const message = useMessage()

const loading = ref(false)
const profileFormRef = ref<FormInst | null>(null)

const profileForm = ref({
  nickname: '',
  email: '',
  avatar: '',
  bio: ''
})

const stats = ref({
  postCount: 0,
  totalViews: 0,
  totalLikes: 0,
  totalComments: 0
})

const recentActivities = ref<any[]>([])

const settings = ref({
  emailNotification: true,
  darkMode: false,
  language: 'zh-CN'
})

const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const profileRules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email' as const, message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' }
  ]
}

// 初始化表单数据
const initForm = () => {
  if (userStore.userInfo) {
    profileForm.value = {
      nickname: userStore.userInfo.nickname || '',
      email: userStore.userInfo.email || '',
      avatar: userStore.userInfo.avatar || '',
      bio: userStore.userInfo.bio || ''
    }
  }
}

// 获取统计数据
const fetchStats = async () => {
  try {
    const { postApi } = await import('@/api')
    const res = await postApi.getPosts({ page: 1, size: 1000 })
    
    if (res.data) {
      const userPosts = (res.data.records || []).filter((post: any) => 
        post.author?.id === userStore.userInfo?.id
      )
      
      stats.value.postCount = userPosts.length
      stats.value.totalViews = userPosts.reduce((sum: number, post: any) => sum + (post.viewCount || 0), 0)
      stats.value.totalLikes = userPosts.reduce((sum: number, post: any) => sum + (post.likeCount || 0), 0)
      stats.value.totalComments = userPosts.reduce((sum: number, post: any) => sum + (post.commentCount || 0), 0)
      
      // 生成最近活动
      recentActivities.value = userPosts
        .slice(0, 5)
        .map((post: any) => ({
          id: post.id,
          type: 'success',
          title: '发布文章',
          content: post.title,
          time: post.createTime
        }))
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  
  if (days === 0) return '今天'
  if (days === 1) return '昨天'
  if (days < 7) return `${days}天前`
  return date.toLocaleDateString('zh-CN')
}

// 更新个人信息
const updateProfile = (e: MouseEvent) => {
  e.preventDefault()
  profileFormRef.value?.validate(async (errors) => {
    if (!errors) {
      try {
        loading.value = true
        const { userApi } = await import('@/api')
        await userApi.updateUser(userStore.userInfo!.id, profileForm.value)
        // Refresh user info by re-fetching
        message.success('更新成功，请重新登录以查看更改')
      } catch (error: any) {
        message.error(error.message || '更新失败')
      } finally {
        loading.value = false
      }
    }
  })
}

// 修改密码
const changePassword = async () => {
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    message.error('两次输入的密码不一致')
    return
  }
  
  try {
    loading.value = true
    const { userApi } = await import('@/api')
    await userApi.changePassword(userStore.userInfo!.id, {
      oldPassword: passwordForm.value.oldPassword,
      newPassword: passwordForm.value.newPassword
    })
    message.success('密码修改成功，请重新登录')
    
    // 清空表单
    passwordForm.value = {
      oldPassword: '',
      newPassword: '',
      confirmPassword: ''
    }
    
    // 3秒后自动退出登录
    setTimeout(() => {
      userStore.logout()
      router.push('/login')
    }, 3000)
  } catch (error: any) {
    message.error(error.message || '密码修改失败')
  } finally {
    loading.value = false
  }
}


// 头像上传相关
const uploadHeaders = computed(() => ({
  Authorization: `Bearer ${userStore.token}`
}))

const handleAvatarSuccess = ({ file: _file, event }: { file: UploadFileInfo, event?: ProgressEvent }) => {
  const response = (event?.target as XMLHttpRequest).response
  const res = JSON.parse(response)
  
  if (res.code === 200) {
    profileForm.value.avatar = res.data
    message.success('头像上传成功')
  } else {
    message.error(res.message || '头像上传失败')
  }
}

const beforeAvatarUpload = async (data: { file: UploadFileInfo, fileList: UploadFileInfo[] }) => {
  const rawFile = data.file.file
  if (!rawFile) return false
  
  const isJPGOrPNG = rawFile.type === 'image/jpeg' || rawFile.type === 'image/png'
  const isLt2M = rawFile.size / 1024 / 1024 < 2

  if (!isJPGOrPNG) {
    message.error('头像必须是 JPG 或 PNG 格式!')
    return false
  }
  if (!isLt2M) {
    message.error('头像大小不能超过 2MB!')
    return false
  }
  return true
}

const handleThemeChange = (value: boolean) => {
  const html = document.documentElement
  if (value) {
    html.classList.add('dark')
    localStorage.setItem('theme', 'dark')
  } else {
    html.classList.remove('dark')
    localStorage.setItem('theme', 'light')
  }
  window.dispatchEvent(new Event('theme-changed'))
  message.success('主题已切换')
}

onMounted(() => {
  initForm()
  fetchStats()
  
  // 从localStorage加载设置
  const savedTheme = localStorage.getItem('theme')
  settings.value.darkMode = savedTheme === 'dark'
})
</script>

<template>
  <div class="profile-page">
    <div class="page-header">
      <h1 class="page-title">个人中心</h1>
    </div>
    
    <div class="profile-container">
      <n-tabs type="segment" animated size="large">
        <n-tab-pane name="overview" tab="概览">
          <div class="overview-section">
            <n-grid :cols="4" :x-gap="16" :y-gap="16" responsive="screen">
              <n-grid-item :span="4" :s-span="2" :m-span="1">
                <n-statistic label="文章总数" :value="stats.postCount">
                  <template #prefix>
                    <n-icon :component="CreateOutline" />
                  </template>
                </n-statistic>
              </n-grid-item>
              <n-grid-item :span="4" :s-span="2" :m-span="1">
                <n-statistic label="总浏览量" :value="stats.totalViews">
                  <template #prefix>
                    <n-icon :component="EyeOutline" />
                  </template>
                </n-statistic>
              </n-grid-item>
              <n-grid-item :span="4" :s-span="2" :m-span="1">
                <n-statistic label="总点赞" :value="stats.totalLikes">
                  <template #prefix>
                    <n-icon :component="HeartOutline" />
                  </template>
                </n-statistic>
              </n-grid-item>
              <n-grid-item :span="4" :s-span="2" :m-span="1">
                <n-statistic label="总评论" :value="stats.totalComments">
                  <template #prefix>
                    <n-icon :component="ChatbubbleOutline" />
                  </template>
                </n-statistic>
              </n-grid-item>
            </n-grid>

            <n-divider />

            <h3 style="margin-bottom: 16px">最近活动</h3>
            <n-timeline>
              <n-timeline-item
                v-for="activity in recentActivities"
                :key="activity.id"
                :type="activity.type"
                :title="activity.title"
                :time="formatDate(activity.time)"
              >
                {{ activity.content }}
              </n-timeline-item>
              <n-timeline-item v-if="recentActivities.length === 0" type="info">
                暂无活动记录
              </n-timeline-item>
            </n-timeline>
          </div>
        </n-tab-pane>

        <n-tab-pane name="basic" tab="基本信息">
          <n-alert type="info" style="margin-bottom: 24px">
            修改个人信息后需要重新登录才能看到更改
          </n-alert>
          
          <n-form
            ref="profileFormRef"
            :model="profileForm"
            :rules="profileRules"
            label-placement="top"
            size="large"
          >
            <n-form-item label="用户名">
              <n-input :value="userStore.userInfo?.username" disabled placeholder="用户名不可修改" />
            </n-form-item>
            
            <n-form-item path="nickname" label="昵称">
              <n-input v-model:value="profileForm.nickname" placeholder="请输入昵称" />
            </n-form-item>
            
            <n-form-item path="email" label="邮箱">
              <n-input v-model:value="profileForm.email" placeholder="请输入邮箱" />
            </n-form-item>

            <n-form-item path="bio" label="个人简介">
              <n-input
                v-model:value="profileForm.bio"
                type="textarea"
                :rows="3"
                placeholder="介绍一下自己..."
                maxlength="200"
                show-count
              />
            </n-form-item>
            
            <n-form-item path="avatar" label="头像">
              <div class="avatar-section">
                <div v-if="profileForm.avatar" class="avatar-preview">
                  <img :src="profileForm.avatar" class="avatar" />
                </div>
                <div class="avatar-actions">
                  <n-upload
                    action="http://localhost:8080/api/upload"
                    :show-file-list="false"
                    :headers="uploadHeaders"
                    @finish="handleAvatarSuccess"
                    @before-upload="beforeAvatarUpload"
                    name="file"
                  >
                    <n-button secondary>
                      <template #icon>
                        <n-icon :component="AddOutline" />
                      </template>
                      {{ profileForm.avatar ? '更换头像' : '上传头像' }}
                    </n-button>
                  </n-upload>
                  <span class="avatar-tip">支持 JPG/PNG，不超过 2MB</span>
                </div>
              </div>
            </n-form-item>
            
            <n-form-item>
              <n-space>
                <n-button type="primary" size="large" :loading="loading" @click="updateProfile">
                  保存修改
                </n-button>
                <n-button size="large" @click="initForm">
                  重置
                </n-button>
              </n-space>
            </n-form-item>
          </n-form>
        </n-tab-pane>
        
        <n-tab-pane name="password" tab="修改密码">
          <n-alert type="info" style="margin-bottom: 24px">
            修改密码后需要重新登录
          </n-alert>
          
          <n-form
            :model="passwordForm"
            :rules="passwordRules"
            label-placement="top"
            size="large"
          >
            <n-form-item path="oldPassword" label="原密码">
              <n-input
                v-model:value="passwordForm.oldPassword"
                type="password"
                show-password-on="click"
                placeholder="请输入原密码"
              />
            </n-form-item>
            
            <n-form-item path="newPassword" label="新密码">
              <n-input
                v-model:value="passwordForm.newPassword"
                type="password"
                show-password-on="click"
                placeholder="请输入新密码"
              />
            </n-form-item>
            
            <n-form-item path="confirmPassword" label="确认密码">
              <n-input
                v-model:value="passwordForm.confirmPassword"
                type="password"
                show-password-on="click"
                placeholder="请再次输入新密码"
              />
            </n-form-item>
            
            <n-form-item>
              <n-button type="primary" size="large" :loading="loading" @click="changePassword">
                修改密码
              </n-button>
            </n-form-item>
          </n-form>
        </n-tab-pane>
        
        <n-tab-pane name="settings" tab="偏好设置">
          <div class="settings-section">
            <n-form label-placement="left" label-width="120" size="large">
              <n-form-item label="深色模式">
                <n-switch 
                  v-model:value="settings.darkMode" 
                  @update:value="handleThemeChange"
                >
                  <template #checked>开启</template>
                  <template #unchecked>关闭</template>
                </n-switch>
              </n-form-item>

              <n-form-item label="邮件通知">
                <n-switch v-model:value="settings.emailNotification">
                  <template #checked>开启</template>
                  <template #unchecked>关闭</template>
                </n-switch>
              </n-form-item>

              <n-form-item label="语言">
                <n-select
                  v-model:value="settings.language"
                  :options="[
                    { label: '简体中文', value: 'zh-CN' },
                    { label: 'English', value: 'en-US' }
                  ]"
                  style="width: 200px"
                />
              </n-form-item>
            </n-form>
          </div>
        </n-tab-pane>

        <n-tab-pane name="account" tab="账号信息">
          <n-descriptions bordered :column="1" size="large">
            <n-descriptions-item label="用户ID">
              {{ userStore.userInfo?.id }}
            </n-descriptions-item>
            <n-descriptions-item label="用户名">
              {{ userStore.userInfo?.username }}
            </n-descriptions-item>
            <n-descriptions-item label="角色">
              <n-tag :type="userStore.isAdmin ? 'error' : 'success'">
                {{ userStore.isAdmin ? '管理员' : '普通用户' }}
              </n-tag>
            </n-descriptions-item>
            <n-descriptions-item label="注册时间">
              {{ userStore.userInfo?.createTime }}
            </n-descriptions-item>
          </n-descriptions>
        </n-tab-pane>
      </n-tabs>
    </div>
  </div>
</template>

<style scoped>
.profile-page {
  max-width: 900px;
  margin: 0 auto;
  padding: 24px 32px;
  min-height: 100vh;
}

.profile-container {
  background-color: var(--card-bg);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  padding: 32px;
}

.page-header {
  margin-bottom: 32px;
}

.page-title {
  font-size: 32px;
  font-weight: 700;
  color: var(--text-color);
  margin: 0;
}

.avatar-section {
  display: flex;
  align-items: center;
  gap: 24px;
}

.avatar-preview {
  width: 80px;
  height: 80px;
  border: 1px solid var(--border-color);
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
}

.avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.avatar-tip {
  font-size: 12px;
  color: var(--text-secondary);
}

.overview-section {
  padding: 24px 0;
}

.overview-section h3 {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-color);
  margin-top: 24px;
}

.settings-section {
  padding: 24px 0;
  max-width: 600px;
}

@media (max-width: 768px) {
  .profile-page {
    padding: 24px 16px;
  }

  .profile-container {
    padding: 24px 16px;
  }

  .page-title {
    font-size: 24px;
  }

  .overview-section,
  .settings-section {
    padding: 16px 0;
  }
}
</style>
