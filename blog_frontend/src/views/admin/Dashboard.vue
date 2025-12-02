<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { 
  NGrid, 
  NGridItem, 
  NCard, 
  NIcon, 
  NEmpty, 
  NSpin,
  NStatistic,
  NList,
  NListItem,
  NThing
} from 'naive-ui'
import { 
  DocumentTextOutline, 
  EyeOutline, 
  ChatbubbleOutline, 
  PersonOutline,
  TimeOutline,
  TrendingUpOutline
} from '@vicons/ionicons5'
import { postApi, commentApi, userApi } from '@/api'

const loading = ref(false)
const stats = ref({
  postCount: 0,
  viewCount: 0,
  commentCount: 0,
  userCount: 0
})

const recentPosts = ref<any[]>([])
const recentComments = ref<any[]>([])

const fetchStats = async () => {
  try {
    loading.value = true
    
    const postRes = await postApi.getAdminPosts({ page: 1, size: 5 })
    if (postRes.data) {
      stats.value.postCount = postRes.data.total || 0
      recentPosts.value = postRes.data.records || []
      stats.value.viewCount = recentPosts.value.reduce((sum: number, post: any) => sum + (post.viewCount || 0), 0)
    }
    
    const commentRes = await commentApi.getAdminComments({ page: 1, size: 5 })
    if (commentRes.data) {
      stats.value.commentCount = commentRes.data.total || 0
      recentComments.value = commentRes.data.records || []
    }
    
    try {
      const userRes = await userApi.getUsers()
      if (userRes.data) {
        stats.value.userCount = userRes.data.length || 0
      }
    } catch {
      stats.value.userCount = 0
    }
    
  } catch (error) {
    console.error('获取统计数据失败:', error)
  } finally {
    loading.value = false
  }
}

const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

onMounted(() => {
  fetchStats()
})
</script>

<template>
  <div class="admin-dashboard">
    <div class="dashboard-header">
      <h2>仪表盘</h2>
      <p>欢迎来到博客管理系统</p>
    </div>
    
    <n-spin :show="loading">
      <n-grid :x-gap="16" :y-gap="16" :cols="4" responsive="screen">
        <n-grid-item>
          <n-card class="stats-card" hoverable>
            <n-statistic label="文章总数" :value="stats.postCount">
              <template #prefix>
                <div class="stats-icon post-icon">
                  <n-icon :component="DocumentTextOutline" :size="28" />
                </div>
              </template>
              <template #suffix>
                <n-icon :component="TrendingUpOutline" :size="16" style="color: #67c23a" />
              </template>
            </n-statistic>
          </n-card>
        </n-grid-item>
        
        <n-grid-item>
          <n-card class="stats-card" hoverable>
            <n-statistic label="总浏览量" :value="stats.viewCount">
              <template #prefix>
                <div class="stats-icon view-icon">
                  <n-icon :component="EyeOutline" :size="28" />
                </div>
              </template>
            </n-statistic>
          </n-card>
        </n-grid-item>
        
        <n-grid-item>
          <n-card class="stats-card" hoverable>
            <n-statistic label="评论总数" :value="stats.commentCount">
              <template #prefix>
                <div class="stats-icon comment-icon">
                  <n-icon :component="ChatbubbleOutline" :size="28" />
                </div>
              </template>
            </n-statistic>
          </n-card>
        </n-grid-item>
        
        <n-grid-item>
          <n-card class="stats-card" hoverable>
            <n-statistic label="用户总数" :value="stats.userCount">
              <template #prefix>
                <div class="stats-icon user-icon">
                  <n-icon :component="PersonOutline" :size="28" />
                </div>
              </template>
            </n-statistic>
          </n-card>
        </n-grid-item>
      </n-grid>
      
      <n-grid :x-gap="16" :y-gap="16" :cols="2" responsive="screen" style="margin-top: 24px">
        <n-grid-item>
          <n-card title="最近文章" :segmented="{ content: true }">
            <template #header-extra>
              <n-icon :component="TimeOutline" />
            </template>
            
            <n-list v-if="recentPosts.length > 0" hoverable clickable>
              <n-list-item v-for="post in recentPosts" :key="post.id">
                <n-thing :title="post.title">
                  <template #description>
                    <span style="margin-right: 16px">{{ formatDate(post.createTime) }}</span>
                    <span>{{ post.viewCount }} 次浏览</span>
                  </template>
                </n-thing>
              </n-list-item>
            </n-list>
            
            <n-empty v-else description="暂无文章" style="padding: 40px 0" />
          </n-card>
        </n-grid-item>
        
        <n-grid-item>
          <n-card title="最近评论" :segmented="{ content: true }">
            <template #header-extra>
              <n-icon :component="ChatbubbleOutline" />
            </template>
            
            <n-list v-if="recentComments.length > 0" hoverable clickable>
              <n-list-item v-for="comment in recentComments" :key="comment.id">
                <n-thing>
                  <template #description>
                    {{ comment.content }}
                  </template>
                  <template #footer>
                    <span style="margin-right: 16px">{{ comment.user?.nickname || comment.user?.username || '匿名' }}</span>
                    <span>{{ formatDate(comment.createTime) }}</span>
                  </template>
                </n-thing>
              </n-list-item>
            </n-list>
            
            <n-empty v-else description="暂无评论" style="padding: 40px 0" />
          </n-card>
        </n-grid-item>
      </n-grid>
    </n-spin>
  </div>
</template>

<style scoped>
.admin-dashboard {
  width: 100%;
}

.dashboard-header {
  margin-bottom: 32px;
}

.dashboard-header h2 {
  margin: 0 0 8px 0;
  font-size: 28px;
  font-weight: 700;
  color: var(--text-color);
}

.dashboard-header p {
  margin: 0;
  font-size: 14px;
  color: var(--text-secondary);
}

.stats-card {
  transition: all 0.2s ease;
}

.stats-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px var(--shadow-md);
}

.stats-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  margin-bottom: 12px;
}

.post-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.view-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.comment-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.user-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}
</style>
