<script setup lang="ts">
import { ref, onMounted, h, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { 
  NButton, 
  NInput, 
  NIcon, 
  NDataTable, 
  NTag, 
  NEmpty, 
  NPagination, 
  NCard,
  NGrid,
  NGridItem,
  NStatistic,
  NSpace,
  NAvatar,
  NSkeleton,
  useMessage,
  useDialog
} from 'naive-ui'
import { 
  SearchOutline, 
  AddOutline, 
  CreateOutline, 
  TrashOutline, 
  EyeOutline,
  HeartOutline,
  ChatbubbleOutline,
  DocumentTextOutline,
  TrendingUpOutline,
  TimeOutline
} from '@vicons/ionicons5'

const router = useRouter()
const userStore = useUserStore()
const message = useMessage()
const dialog = useDialog()

const loading = ref(false)
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const viewMode = ref<'table' | 'card'>('card')

const posts = ref<any[]>([])
const total = ref(0)

// 统计数据
const stats = computed(() => {
  const totalViews = posts.value.reduce((sum, post) => sum + (post.viewCount || 0), 0)
  const totalLikes = posts.value.reduce((sum, post) => sum + (post.likeCount || 0), 0)
  const totalComments = posts.value.reduce((sum, post) => sum + (post.commentCount || 0), 0)
  const publishedCount = posts.value.filter(post => post.status === 1).length
  
  return {
    total: posts.value.length,
    published: publishedCount,
    draft: posts.value.length - publishedCount,
    totalViews,
    totalLikes,
    totalComments
  }
})

// 方法
const fetchMyPosts = async () => {
  try {
    loading.value = true
    const { postApi } = await import('@/api')
    const res = await postApi.getPosts({
      page: currentPage.value,
      size: pageSize.value,
      keyword: searchKeyword.value || undefined
    })
    
    if (res.data) {
      // 过滤出当前用户的文章
      const allPosts = res.data.records || []
      posts.value = allPosts.filter((post: any) => 
        post.author?.id === userStore.userInfo?.id
      )
      total.value = posts.value.length
    }
  } catch (error) {
    console.error('获取文章列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchMyPosts()
}

const handlePageChange = (page: number) => {
  currentPage.value = page
  fetchMyPosts()
}

const viewPost = (id: number) => {
  router.push(`/post/${id}`)
}

const editPost = (id: number) => {
  router.push(`/post/edit/${id}`)
}

const deletePost = async (id: number) => {
  dialog.warning({
    title: '提示',
    content: '确定要删除这篇文章吗？',
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        const { postApi } = await import('@/api')
        await postApi.deletePost(id)
        message.success('删除成功')
        fetchMyPosts()
      } catch (error: any) {
        message.error(error.message || '删除失败')
      }
    }
  })
}

const createPost = () => {
  router.push('/post/edit')
}

const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

const formatRelativeTime = (dateString: string) => {
  const date = new Date(dateString)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  
  if (days === 0) return '今天'
  if (days === 1) return '昨天'
  if (days < 7) return `${days}天前`
  if (days < 30) return `${Math.floor(days / 7)}周前`
  return formatDate(dateString)
}

const formatNumber = (num: number) => {
  if (num >= 10000) return (num / 10000).toFixed(1) + 'w'
  if (num >= 1000) return (num / 1000).toFixed(1) + 'k'
  return num.toString()
}

const getStatusText = (status: number) => {
  return status === 0 ? '草稿' : status === 1 ? '已发布' : '已下架'
}

const getStatusType = (status: number) => {
  return status === 0 ? 'info' : status === 1 ? 'success' : 'warning'
}

// 表格列配置
const columns = [
  {
    title: 'ID',
    key: 'id',
    width: 80
  },
  {
    title: '标题',
    key: 'title',
    minWidth: 200,
    ellipsis: {
      tooltip: true
    }
  },
  {
    title: '分类',
    key: 'category.name',
    width: 120,
    render(row: any) {
      return row.category?.name || '-'
    }
  },
  {
    title: '状态',
    key: 'status',
    width: 100,
    render(row: any) {
      return h(
        NTag,
        {
          type: getStatusType(row.status) as any,
          size: 'small'
        },
        { default: () => getStatusText(row.status) }
      )
    }
  },
  {
    title: '浏览量',
    key: 'viewCount',
    width: 100
  },
  {
    title: '创建时间',
    key: 'createTime',
    width: 120,
    render(row: any) {
      return formatDate(row.createTime)
    }
  },
  {
    title: '操作',
    key: 'actions',
    width: 250,
    render(row: any) {
      return [
        h(
          NButton,
          {
            type: 'primary',
            size: 'small',
            onClick: () => viewPost(row.id),
            style: 'margin-right: 6px'
          },
          { 
            default: () => '查看',
            icon: () => h(NIcon, null, { default: () => h(EyeOutline) })
          }
        ),
        h(
          NButton,
          {
            type: 'success',
            size: 'small',
            onClick: () => editPost(row.id),
            style: 'margin-right: 6px'
          },
          { 
            default: () => '编辑',
            icon: () => h(NIcon, null, { default: () => h(CreateOutline) })
          }
        ),
        h(
          NButton,
          {
            type: 'error',
            size: 'small',
            onClick: () => deletePost(row.id)
          },
          { 
            default: () => '删除',
            icon: () => h(NIcon, null, { default: () => h(TrashOutline) })
          }
        )
      ]
    }
  }
]



// 初始化
onMounted(() => {
  fetchMyPosts()
})
</script>

<template>
  <div class="my-posts-page">
    <!-- Hero Section -->
    <div class="hero-section">
      <div class="hero-content">
        <div class="hero-left">
          <n-avatar 
            :src="userStore.userInfo?.avatar" 
            :size="80"
            round
            :fallback-src="'https://07akioni.oss-cn-beijing.aliyuncs.com/07akioni.jpeg'"
          />
          <div class="hero-info">
            <h1 class="hero-title">我的博客</h1>
            <p class="hero-subtitle">{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</p>
          </div>
        </div>
        <n-button type="primary" size="large" @click="createPost" strong>
          <template #icon>
            <n-icon :component="AddOutline" />
          </template>
          创作新文章
        </n-button>
      </div>
    </div>

    <!-- Stats Section -->
    <div class="stats-section">
      <n-grid :cols="6" :x-gap="16" :y-gap="16" responsive="screen">
        <n-grid-item :span="6" :s-span="3" :m-span="2">
          <div class="stat-card">
            <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
              <n-icon :component="DocumentTextOutline" :size="24" />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.total }}</div>
              <div class="stat-label">文章总数</div>
            </div>
          </div>
        </n-grid-item>
        <n-grid-item :span="6" :s-span="3" :m-span="2">
          <div class="stat-card">
            <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
              <n-icon :component="CreateOutline" :size="24" />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.published }}</div>
              <div class="stat-label">已发布</div>
            </div>
          </div>
        </n-grid-item>
        <n-grid-item :span="6" :s-span="3" :m-span="2">
          <div class="stat-card">
            <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
              <n-icon :component="EyeOutline" :size="24" />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ formatNumber(stats.totalViews) }}</div>
              <div class="stat-label">总阅读</div>
            </div>
          </div>
        </n-grid-item>
        <n-grid-item :span="6" :s-span="3" :m-span="2">
          <div class="stat-card">
            <div class="stat-icon" style="background: linear-gradient(135deg, #fa709a 0%, #fee140 100%)">
              <n-icon :component="HeartOutline" :size="24" />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ formatNumber(stats.totalLikes) }}</div>
              <div class="stat-label">总点赞</div>
            </div>
          </div>
        </n-grid-item>
        <n-grid-item :span="6" :s-span="3" :m-span="2">
          <div class="stat-card">
            <div class="stat-icon" style="background: linear-gradient(135deg, #30cfd0 0%, #330867 100%)">
              <n-icon :component="ChatbubbleOutline" :size="24" />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ formatNumber(stats.totalComments) }}</div>
              <div class="stat-label">总评论</div>
            </div>
          </div>
        </n-grid-item>
        <n-grid-item :span="6" :s-span="3" :m-span="2">
          <div class="stat-card">
            <div class="stat-icon" style="background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%)">
              <n-icon :component="TrendingUpOutline" :size="24" />
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ stats.draft }}</div>
              <div class="stat-label">草稿箱</div>
            </div>
          </div>
        </n-grid-item>
      </n-grid>
    </div>

    <!-- Toolbar -->
    <div class="toolbar">
      <div class="search-bar">
        <n-input
          v-model:value="searchKeyword"
          placeholder="搜索我的文章..."
          size="large"
          clearable
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <n-icon :component="SearchOutline" />
          </template>
        </n-input>
      </div>
      <div class="view-toggle">
        <n-space>
          <n-button 
            :type="viewMode === 'card' ? 'primary' : 'default'"
            @click="viewMode = 'card'"
            ghost
          >
            卡片视图
          </n-button>
          <n-button 
            :type="viewMode === 'table' ? 'primary' : 'default'"
            @click="viewMode = 'table'"
            ghost
          >
            列表视图
          </n-button>
        </n-space>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading && posts.length === 0" class="posts-loading">
      <n-grid :cols="3" :x-gap="16" :y-gap="16" responsive="screen">
        <n-grid-item v-for="i in 6" :key="i">
          <div class="post-card-skeleton">
            <n-skeleton text style="width: 60%; height: 24px; margin-bottom: 12px" />
            <n-skeleton text :repeat="2" style="margin-bottom: 16px" />
            <div style="display: flex; gap: 12px">
              <n-skeleton text style="width: 60px; height: 24px; border-radius: 12px" />
              <n-skeleton text style="width: 80px; height: 24px; border-radius: 12px" />
            </div>
          </div>
        </n-grid-item>
      </n-grid>
    </div>

    <!-- Card View -->
    <div v-else-if="viewMode === 'card'" class="posts-grid">
      <div v-if="posts.length === 0" class="empty-state">
        <n-empty description="还没有文章，快去创建一篇吧！" size="large">
          <template #icon>
            <div class="empty-icon">✍️</div>
          </template>
          <template #extra>
            <n-button type="primary" size="large" @click="createPost">
              <template #icon>
                <n-icon :component="AddOutline" />
              </template>
              写第一篇文章
            </n-button>
          </template>
        </n-empty>
      </div>

      <n-grid v-else :cols="3" :x-gap="20" :y-gap="20" responsive="screen">
        <n-grid-item v-for="post in posts" :key="post.id">
          <div class="post-card" @click="viewPost(post.id)">
            <div class="post-card-header">
              <n-tag 
                :type="getStatusType(post.status)" 
                size="small" 
                :bordered="false"
                round
              >
                {{ getStatusText(post.status) }}
              </n-tag>
              <span class="post-date">{{ formatRelativeTime(post.createTime) }}</span>
            </div>

            <h3 class="post-card-title">{{ post.title }}</h3>
            
            <p class="post-card-summary">
              {{ post.summary || post.content?.substring(0, 100) + '...' || '暂无内容' }}
            </p>

            <div class="post-card-meta">
              <n-tag v-if="post.category" size="small" :bordered="false" round>
                {{ post.category.name }}
              </n-tag>
              <div class="post-card-stats">
                <span><n-icon :component="EyeOutline" :size="14" /> {{ formatNumber(post.viewCount || 0) }}</span>
                <span><n-icon :component="HeartOutline" :size="14" /> {{ formatNumber(post.likeCount || 0) }}</span>
              </div>
            </div>

            <div class="post-card-actions">
              <n-button size="small" @click.stop="viewPost(post.id)">
                <template #icon>
                  <n-icon :component="EyeOutline" />
                </template>
                查看
              </n-button>
              <n-button size="small" type="primary" @click.stop="editPost(post.id)">
                <template #icon>
                  <n-icon :component="CreateOutline" />
                </template>
                编辑
              </n-button>
              <n-button size="small" type="error" @click.stop="deletePost(post.id)">
                <template #icon>
                  <n-icon :component="TrashOutline" />
                </template>
                删除
              </n-button>
            </div>
          </div>
        </n-grid-item>
      </n-grid>
    </div>

    <!-- Table View -->
    <div v-else class="content-container">
      <n-data-table
        :columns="columns"
        :data="posts"
        :loading="loading"
        :pagination="false"
        :bordered="false"
        :single-line="false"
      />
      
      <div v-if="posts.length === 0 && !loading" class="empty-state">
        <n-empty description="还没有文章，快去创建一篇吧！" size="large">
          <template #extra>
            <n-button type="primary" size="large" @click="createPost">
              <template #icon>
                <n-icon :component="AddOutline" />
              </template>
              写第一篇文章
            </n-button>
          </template>
        </n-empty>
      </div>
    </div>

    <!-- Pagination -->
    <div class="pagination" v-if="total > pageSize">
      <n-pagination
        v-model:page="currentPage"
        :page-count="Math.ceil(total / pageSize)"
        :page-slot="7"
        show-size-picker
        :page-sizes="[10, 20, 30, 50]"
        @update:page="handlePageChange"
        @update:page-size="(size) => { pageSize = size; fetchMyPosts() }"
      />
    </div>
  </div>
</template>

<style scoped>
.my-posts-page {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0;
  min-height: 100vh;
  padding-bottom: 60px;
}

/* Hero Section */
.hero-section {
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-hover) 100%);
  padding: 48px 32px;
  margin-bottom: 32px;
  border-radius: 0 0 24px 24px;
  position: relative;
  overflow: hidden;
}

.hero-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg width="100" height="100" xmlns="http://www.w3.org/2000/svg"><circle cx="50" cy="50" r="2" fill="white" opacity="0.1"/></svg>');
  opacity: 0.3;
}

.hero-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1400px;
  margin: 0 auto;
  position: relative;
  z-index: 1;
}

.hero-left {
  display: flex;
  align-items: center;
  gap: 24px;
}

.hero-info {
  color: white;
}

.hero-title {
  font-size: 36px;
  font-weight: 800;
  margin: 0 0 8px 0;
  color: white;
}

.hero-subtitle {
  font-size: 16px;
  margin: 0;
  opacity: 0.9;
}

/* Stats Section */
.stats-section {
  padding: 0 32px;
  margin-bottom: 32px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 24px;
  background: var(--card-bg);
  border: 1px solid var(--border-color);
  border-radius: 16px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px var(--shadow-md);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-color);
  line-height: 1;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 13px;
  color: var(--text-secondary);
}

/* Toolbar */
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 32px;
  margin-bottom: 24px;
  gap: 16px;
}

.search-bar {
  flex: 1;
  max-width: 400px;
}

.view-toggle {
  flex-shrink: 0;
}

/* Posts Grid */
.posts-grid {
  padding: 0 32px;
}

.posts-loading {
  padding: 0 32px;
}

.post-card-skeleton {
  background: var(--card-bg);
  border: 1px solid var(--border-color);
  border-radius: 16px;
  padding: 24px;
}

.post-card {
  background: var(--card-bg);
  border: 1px solid var(--border-color);
  border-radius: 16px;
  padding: 24px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  height: 100%;
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;
}

.post-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: var(--primary-color);
  transform: scaleY(0);
  transition: transform 0.3s ease;
}

.post-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px var(--shadow-md);
  border-color: var(--primary-color);
}

.post-card:hover::before {
  transform: scaleY(1);
}

.post-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.post-date {
  font-size: 13px;
  color: var(--text-tertiary);
}

.post-card-title {
  font-size: 20px;
  font-weight: 700;
  color: var(--text-color);
  margin: 0 0 12px 0;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  transition: color 0.2s ease;
}

.post-card:hover .post-card-title {
  color: var(--primary-color);
}

.post-card-summary {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.6;
  margin: 0 0 16px 0;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  flex: 1;
}

.post-card-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-top: 16px;
  border-top: 1px solid var(--border-color);
}

.post-card-stats {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: var(--text-tertiary);
}

.post-card-stats span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.post-card-actions {
  display: flex;
  gap: 8px;
}

.post-card-actions .n-button {
  flex: 1;
}

/* Table View */
.content-container {
  background-color: var(--card-bg);
  border: 1px solid var(--border-color);
  border-radius: 16px;
  padding: 24px;
  margin: 0 32px;
}

/* Empty State */
.empty-state {
  padding: 80px 20px;
  text-align: center;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 16px;
}

/* Pagination */
.pagination {
  display: flex;
  justify-content: center;
  margin-top: 32px;
  padding: 0 32px;
}

/* Animations */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.post-card {
  animation: fadeInUp 0.5s ease-out;
}

.stat-card {
  animation: fadeInUp 0.5s ease-out;
}

.stat-card:nth-child(1) { animation-delay: 0.05s; }
.stat-card:nth-child(2) { animation-delay: 0.1s; }
.stat-card:nth-child(3) { animation-delay: 0.15s; }
.stat-card:nth-child(4) { animation-delay: 0.2s; }
.stat-card:nth-child(5) { animation-delay: 0.25s; }
.stat-card:nth-child(6) { animation-delay: 0.3s; }

/* Responsive */
@media (max-width: 1024px) {
  .hero-title {
    font-size: 28px;
  }
}

@media (max-width: 768px) {
  .my-posts-page {
    padding-bottom: 40px;
  }

  .hero-section {
    padding: 32px 20px;
    border-radius: 0 0 16px 16px;
  }

  .hero-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 20px;
  }

  .hero-left {
    gap: 16px;
  }

  .hero-title {
    font-size: 24px;
  }

  .hero-subtitle {
    font-size: 14px;
  }

  .stats-section,
  .toolbar,
  .posts-grid,
  .posts-loading,
  .content-container,
  .pagination {
    padding: 0 16px;
  }

  .stats-section {
    margin-bottom: 24px;
  }

  .stat-card {
    padding: 16px;
  }

  .stat-icon {
    width: 48px;
    height: 48px;
  }

  .stat-value {
    font-size: 24px;
  }

  .toolbar {
    flex-direction: column;
    align-items: stretch;
  }

  .search-bar {
    max-width: 100%;
  }

  .view-toggle {
    display: flex;
    justify-content: center;
  }

  .post-card {
    padding: 20px;
  }

  .post-card-title {
    font-size: 18px;
  }

  .content-container {
    padding: 16px;
    overflow-x: auto;
  }
}
</style>
