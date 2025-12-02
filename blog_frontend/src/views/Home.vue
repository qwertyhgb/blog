<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { usePostStore } from '@/stores/post'
import { useCategoryStore } from '@/stores/category'
import { useUserStore } from '@/stores/user'
import { 
  NSkeleton, 
  NTag, 
  NIcon, 
  NInput,
  NPagination,
  NSpace,
  NButton,
  NAvatar,
  NStatistic,
  NGrid,
  NGridItem,
  NCard,
  NBadge,
  NEmpty
} from 'naive-ui'
import { 
  TimeOutline,
  SearchOutline,
  CreateOutline,
  EyeOutline,
  HeartOutline,
  ChatbubbleOutline,
  TrendingUpOutline,
  FlameOutline
} from '@vicons/ionicons5'
import EmptyState from '@/components/EmptyState.vue'
import PostCardSkeleton from '@/components/PostCardSkeleton.vue'
import BackTop from '@/components/BackTop.vue'

const route = useRoute()
const router = useRouter()
const postStore = usePostStore()
const categoryStore = useCategoryStore()
const userStore = useUserStore()

const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')
const selectedCategory = ref<number | null>(null)
const sortBy = ref<'latest' | 'popular' | 'trending'>('latest')

const posts = computed(() => postStore.posts)
const categories = computed(() => categoryStore.categories)
const isLoggedIn = computed(() => userStore.isLoggedIn)

// 热门文章（按浏览量排序）
const popularPosts = computed(() => {
  return [...posts.value]
    .sort((a, b) => (b.viewCount || 0) - (a.viewCount || 0))
    .slice(0, 3)
})

const fetchPosts = async (page: number) => {
  try {
    loading.value = true
    await postStore.fetchPosts({
      page,
      size: pageSize.value,
      keyword: searchKeyword.value
    })
  } catch (error) {
    console.error('获取文章列表失败:', error)
  } finally {
    loading.value = false
  }
}

const filteredPosts = computed(() => {
  let result = [...posts.value]
  
  // 分类筛选
  if (selectedCategory.value) {
    result = result.filter(post => post.category?.id === selectedCategory.value)
  }
  
  // 排序
  if (sortBy.value === 'popular') {
    result.sort((a, b) => (b.viewCount || 0) - (a.viewCount || 0))
  } else if (sortBy.value === 'trending') {
    result.sort((a, b) => (b.likeCount || 0) - (a.likeCount || 0))
  }
  
  return result
})

const selectCategory = (categoryId: number | null) => {
  selectedCategory.value = categoryId
}

const changeSortBy = (sort: 'latest' | 'popular' | 'trending') => {
  sortBy.value = sort
}

const goToPostDetail = (id: number) => {
  router.push(`/post/${id}`)
}

const goToCategory = (id: number) => {
  router.push(`/category/${id}`)
}

// 预加载文章详情（鼠标悬停时）
const preloadPost = (id: number) => {
  // 可以在这里预加载文章数据
  postStore.fetchPostById(id).catch(() => {
    // 静默失败，不影响用户体验
  })
}

const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  
  if (days === 0) return '今天'
  if (days === 1) return '昨天'
  if (days < 7) return `${days}天前`
  if (days < 30) return `${Math.floor(days / 7)}周前`
  if (days < 365) return `${Math.floor(days / 30)}个月前`
  return `${Math.floor(days / 365)}年前`
}

const getReadingTime = (content: string) => {
  const wordsPerMinute = 300
  const words = content.length
  const minutes = Math.ceil(words / wordsPerMinute)
  return `${minutes} 分钟`
}

const formatNumber = (num: number) => {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + 'w'
  }
  if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'k'
  }
  return num.toString()
}

const handleSearch = () => {
  currentPage.value = 1
  fetchPosts(1)
}

const handlePageChange = (page: number) => {
  currentPage.value = page
  fetchPosts(page)
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

// 搜索防抖
let searchTimeout: NodeJS.Timeout | null = null
watch(searchKeyword, (newVal) => {
  if (searchTimeout) clearTimeout(searchTimeout)
  searchTimeout = setTimeout(() => {
    if (newVal !== searchKeyword.value) return
    currentPage.value = 1
    fetchPosts(1)
  }, 500)
})

// 监听分类和排序变化
watch([selectedCategory, sortBy], () => {
  // 客户端筛选，不需要重新请求
})

// 键盘快捷键
const handleKeydown = (e: KeyboardEvent) => {
  // Ctrl/Cmd + K 聚焦搜索框
  if ((e.ctrlKey || e.metaKey) && e.key === 'k') {
    e.preventDefault()
    const searchInput = document.querySelector('.hero-search input') as HTMLInputElement
    searchInput?.focus()
  }
  
  // Ctrl/Cmd + N 创建新文章（仅登录用户）
  if ((e.ctrlKey || e.metaKey) && e.key === 'n' && isLoggedIn.value) {
    e.preventDefault()
    router.push('/post/edit')
  }
}

onMounted(async () => {
  await Promise.all([
    fetchPosts(currentPage.value),
    categoryStore.fetchCategories()
  ])
  
  // 添加键盘事件监听
  window.addEventListener('keydown', handleKeydown)
})

// 清理
import { onUnmounted } from 'vue'
onUnmounted(() => {
  window.removeEventListener('keydown', handleKeydown)
  if (searchTimeout) clearTimeout(searchTimeout)
})
</script>

<template>
  <div class="home-page">
    <!-- Hero Section -->
    <div class="hero-section">
      <div class="hero-content">
        <h1 class="hero-title">发现精彩内容</h1>
        <p class="hero-subtitle">探索 {{ postStore.total }} 篇优质文章，开启你的阅读之旅</p>
        
        <div class="hero-search">
          <n-input
            v-model:value="searchKeyword"
            placeholder="搜索你感兴趣的内容... (Ctrl+K)"
            size="large"
            round
            @keyup.enter="handleSearch"
            clearable
          >
            <template #prefix>
              <n-icon :component="SearchOutline" :size="20" />
            </template>
          </n-input>
          <n-button 
            type="primary" 
            size="large" 
            round
            @click="handleSearch"
            strong
          >
            搜索
          </n-button>
        </div>
        
        <div class="keyboard-hint" v-if="isLoggedIn">
          <span class="hint-item">
            <kbd>Ctrl</kbd> + <kbd>K</kbd> 搜索
          </span>
          <span class="hint-item">
            <kbd>Ctrl</kbd> + <kbd>N</kbd> 写文章
          </span>
        </div>
      </div>
    </div>

    <!-- Stats Section -->
    <div class="stats-section" v-if="posts.length > 0">
      <n-grid :cols="3" :x-gap="16" responsive="screen">
        <n-grid-item>
          <div class="stat-card">
            <n-icon :component="CreateOutline" :size="24" class="stat-icon" />
            <div class="stat-content">
              <div class="stat-value">{{ postStore.total }}</div>
              <div class="stat-label">文章总数</div>
            </div>
          </div>
        </n-grid-item>
        <n-grid-item>
          <div class="stat-card">
            <n-icon :component="EyeOutline" :size="24" class="stat-icon" />
            <div class="stat-content">
              <div class="stat-value">{{ formatNumber(posts.reduce((sum, p) => sum + (p.viewCount || 0), 0)) }}</div>
              <div class="stat-label">总阅读量</div>
            </div>
          </div>
        </n-grid-item>
        <n-grid-item>
          <div class="stat-card">
            <n-icon :component="HeartOutline" :size="24" class="stat-icon" />
            <div class="stat-content">
              <div class="stat-value">{{ formatNumber(posts.reduce((sum, p) => sum + (p.likeCount || 0), 0)) }}</div>
              <div class="stat-label">总点赞数</div>
            </div>
          </div>
        </n-grid-item>
      </n-grid>
    </div>

    <!-- Hot Posts Section -->
    <div class="hot-posts-section" v-if="popularPosts.length > 0 && !searchKeyword">
      <div class="section-header">
        <n-icon :component="FlameOutline" :size="20" class="section-icon" />
        <h2 class="section-title">热门推荐</h2>
      </div>
      <n-grid :cols="3" :x-gap="16" :y-gap="16" responsive="screen">
        <n-grid-item v-for="(post, index) in popularPosts" :key="post.id">
          <div class="hot-post-card" @click="goToPostDetail(post.id)">
            <n-badge :value="index + 1" :type="index === 0 ? 'error' : index === 1 ? 'warning' : 'info'">
              <div class="hot-post-content">
                <h3 class="hot-post-title">{{ post.title }}</h3>
                <div class="hot-post-stats">
                  <span><n-icon :component="EyeOutline" :size="14" /> {{ formatNumber(post.viewCount || 0) }}</span>
                  <span><n-icon :component="HeartOutline" :size="14" /> {{ formatNumber(post.likeCount || 0) }}</span>
                </div>
              </div>
            </n-badge>
          </div>
        </n-grid-item>
      </n-grid>
    </div>

    <!-- Filter Section -->
    <div class="filter-section">
      <div class="filter-left">
        <n-space>
          <n-tag 
            :type="selectedCategory === null ? 'primary' : 'default'"
            :bordered="false"
            round
            class="filter-tag"
            @click="selectCategory(null)"
            checkable
            :checked="selectedCategory === null"
          >
            全部
          </n-tag>
          <n-tag 
            v-for="category in categories" 
            :key="category.id"
            :type="selectedCategory === category.id ? 'primary' : 'default'"
            :bordered="false"
            round
            class="filter-tag"
            @click="selectCategory(category.id)"
            checkable
            :checked="selectedCategory === category.id"
          >
            {{ category.name }}
          </n-tag>
        </n-space>
      </div>
      <div class="filter-right">
        <n-space>
          <n-button 
            :type="sortBy === 'latest' ? 'primary' : 'default'"
            size="small"
            @click="changeSortBy('latest')"
            ghost
          >
            最新
          </n-button>
          <n-button 
            :type="sortBy === 'popular' ? 'primary' : 'default'"
            size="small"
            @click="changeSortBy('popular')"
            ghost
          >
            <template #icon>
              <n-icon :component="TrendingUpOutline" />
            </template>
            热门
          </n-button>
          <n-button 
            :type="sortBy === 'trending' ? 'primary' : 'default'"
            size="small"
            @click="changeSortBy('trending')"
            ghost
          >
            <template #icon>
              <n-icon :component="FlameOutline" />
            </template>
            趋势
          </n-button>
        </n-space>
      </div>
    </div>

    <div class="posts-container">
      <template v-if="loading && posts.length === 0">
        <post-card-skeleton v-for="i in 5" :key="i" />
      </template>

      <template v-else>
        <empty-state 
          v-if="filteredPosts.length === 0 && !searchKeyword"
          description="还没有文章，快去创建第一篇吧！"
          :show-action="isLoggedIn"
          action-text="写文章"
          :action-icon="CreateOutline"
          @action="router.push('/post/edit')"
        />

        <empty-state 
          v-else-if="filteredPosts.length === 0 && searchKeyword"
          description="没有找到相关文章，试试其他关键词吧"
          :show-action="false"
        />

        <div 
          v-else 
          v-for="post in filteredPosts" 
          :key="post.id" 
          class="post-card"
          @click="goToPostDetail(post.id)"
          @mouseenter="preloadPost(post.id)"
          tabindex="0"
          @keydown.enter="goToPostDetail(post.id)"
        >
          <div class="post-header">
            <div class="post-author">
              <n-avatar 
                :src="post.author?.avatar" 
                :fallback-src="'https://07akioni.oss-cn-beijing.aliyuncs.com/07akioni.jpeg'"
                round 
                size="small"
              />
              <span class="author-name">{{ post.author?.nickname || post.author?.username || '匿名' }}</span>
              <span class="post-date">{{ formatDate(post.createTime) }}</span>
            </div>
            <n-tag 
              v-if="post.category" 
              size="small" 
              :bordered="false" 
              round
              class="category-badge-new"
            >
              {{ post.category.name }}
            </n-tag>
          </div>

          <h2 class="post-title">{{ post.title }}</h2>
          <p class="post-summary">{{ post.summary || post.content.substring(0, 150) + '...' }}</p>
          
          <div class="post-footer">
            <div class="post-stats">
              <span class="stat-item">
                <n-icon :component="EyeOutline" :size="16" />
                {{ formatNumber(post.viewCount || 0) }}
              </span>
              <span class="stat-item">
                <n-icon :component="HeartOutline" :size="16" />
                {{ formatNumber(post.likeCount || 0) }}
              </span>
              <span class="stat-item">
                <n-icon :component="ChatbubbleOutline" :size="16" />
                {{ formatNumber(post.commentCount || 0) }}
              </span>
              <span class="stat-item">
                <n-icon :component="TimeOutline" :size="16" />
                {{ getReadingTime(post.content) }}
              </span>
            </div>
            <div class="post-tags">
              <n-tag 
                v-for="tag in post.tags?.slice(0, 3)" 
                :key="tag.id"
                size="small"
                :bordered="false"
                round
              >
                {{ tag.name }}
              </n-tag>
            </div>
          </div>
        </div>
      </template>
    </div>

    <div class="pagination-container" v-if="postStore.total > pageSize">
      <n-pagination
        v-model:page="currentPage"
        :page-count="Math.ceil(postStore.total / pageSize)"
        :page-slot="7"
        @update:page="handlePageChange"
        show-size-picker
        :page-sizes="[10, 20, 30, 50]"
        @update:page-size="(size) => { pageSize = size; fetchPosts(1) }"
      />
    </div>

    <!-- Quick Actions -->
    <div class="quick-actions" v-if="isLoggedIn">
      <n-button 
        type="primary" 
        circle 
        size="large"
        @click="router.push('/post/edit')"
        class="fab-button"
      >
        <template #icon>
          <n-icon :component="CreateOutline" :size="24" />
        </template>
      </n-button>
    </div>

    <!-- Back to Top -->
    <back-top />
  </div>
</template>

<style scoped>
.home-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0;
  min-height: 100vh;
  padding-bottom: 80px;
}

/* Hero Section */
.hero-section {
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-hover) 100%);
  padding: 60px 32px;
  margin-bottom: 48px;
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
  max-width: 800px;
  margin: 0 auto;
  text-align: center;
  position: relative;
  z-index: 1;
}

.hero-title {
  font-size: 48px;
  font-weight: 800;
  color: white;
  margin-bottom: 12px;
  letter-spacing: -1px;
}

.hero-subtitle {
  font-size: 18px;
  color: rgba(255, 255, 255, 0.9);
  margin-bottom: 32px;
}

.hero-search {
  display: flex;
  gap: 12px;
  max-width: 600px;
  margin: 0 auto;
}

.hero-search :deep(.n-input) {
  flex: 1;
  background: rgba(255, 255, 255, 0.95);
}

.keyboard-hint {
  display: flex;
  gap: 16px;
  justify-content: center;
  margin-top: 16px;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.8);
}

.hint-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

kbd {
  display: inline-block;
  padding: 2px 6px;
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 4px;
  font-family: var(--font-mono);
  font-size: 12px;
  font-weight: 600;
  color: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* Stats Section */
.stats-section {
  padding: 0 32px;
  margin-bottom: 48px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 24px;
  background: var(--card-bg);
  border: 1px solid var(--border-color);
  border-radius: 12px;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px var(--shadow-md);
}

.stat-icon {
  color: var(--primary-color);
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

/* Hot Posts Section */
.hot-posts-section {
  padding: 0 32px;
  margin-bottom: 48px;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 20px;
}

.section-icon {
  color: #ff6b35;
}

.section-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-color);
  margin: 0;
}

.hot-post-card {
  background: var(--card-bg);
  border: 1px solid var(--border-color);
  border-radius: 12px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  height: 100%;
}

.hot-post-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px var(--shadow-md);
  border-color: var(--primary-color);
}

.hot-post-content {
  padding-left: 8px;
}

.hot-post-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-color);
  margin-bottom: 12px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.hot-post-stats {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: var(--text-tertiary);
}

.hot-post-stats span {
  display: flex;
  align-items: center;
  gap: 4px;
}

/* Filter Section */
.filter-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 32px;
  margin-bottom: 24px;
  gap: 16px;
  flex-wrap: wrap;
}

.filter-left {
  flex: 1;
  overflow-x: auto;
}

.filter-right {
  flex-shrink: 0;
}

.filter-tag {
  cursor: pointer;
  transition: all 0.2s ease;
}

.filter-tag:hover {
  transform: translateY(-2px);
}

/* Posts Container */
.posts-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding: 0 32px;
}

.post-card {
  background-color: var(--card-bg);
  border: 1px solid var(--border-color);
  border-radius: 12px;
  padding: 28px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
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
  box-shadow: 0 8px 24px var(--shadow-md);
  transform: translateY(-4px);
  border-color: var(--primary-color);
}

.post-card:hover::before {
  transform: scaleY(1);
}

.post-card.skeleton {
  cursor: default;
}

.post-card.skeleton:hover {
  transform: none;
  box-shadow: none;
}

.post-card.skeleton::before {
  display: none;
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.post-author {
  display: flex;
  align-items: center;
  gap: 10px;
}

.author-name {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-color);
}

.post-date {
  font-size: 13px;
  color: var(--text-tertiary);
}

.post-date::before {
  content: '·';
  margin: 0 6px;
}

.category-badge-new {
  background: linear-gradient(135deg, var(--primary-color), var(--primary-hover));
  color: white;
  font-weight: 500;
}

.post-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-color);
  margin-bottom: 12px;
  line-height: 1.4;
  transition: color 0.2s ease;
}

.post-card:hover .post-title {
  color: var(--primary-color);
}

.post-summary {
  font-size: 15px;
  color: var(--text-secondary);
  line-height: 1.7;
  margin-bottom: 20px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.post-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
  padding-top: 16px;
  border-top: 1px solid var(--border-color);
}

.post-stats {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: var(--text-tertiary);
  transition: color 0.2s ease;
}

.stat-item:hover {
  color: var(--primary-color);
}

.post-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}



/* Pagination */
.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 48px;
  padding: 32px;
}

/* Quick Actions */
.quick-actions {
  position: fixed;
  bottom: 32px;
  right: 32px;
  z-index: 99;
}

.fab-button {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  transition: all 0.3s ease;
}

.fab-button:hover {
  transform: scale(1.1) rotate(90deg);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.25);
}

/* Responsive */
@media (max-width: 1024px) {
  .hero-title {
    font-size: 36px;
  }

  .hero-subtitle {
    font-size: 16px;
  }
}

@media (max-width: 768px) {
  .home-page {
    padding-bottom: 60px;
  }

  .hero-section {
    padding: 40px 20px;
    border-radius: 0 0 16px 16px;
  }

  .hero-title {
    font-size: 28px;
  }

  .hero-subtitle {
    font-size: 14px;
    margin-bottom: 24px;
  }

  .hero-search {
    flex-direction: column;
  }

  .stats-section,
  .hot-posts-section,
  .filter-section,
  .posts-container {
    padding: 0 16px;
  }

  .stats-section {
    margin-bottom: 32px;
  }

  .stat-card {
    padding: 16px;
  }

  .stat-value {
    font-size: 24px;
  }

  .hot-posts-section {
    margin-bottom: 32px;
  }

  .section-title {
    font-size: 20px;
  }

  .filter-section {
    flex-direction: column;
    align-items: stretch;
  }

  .filter-left,
  .filter-right {
    width: 100%;
  }

  .filter-right {
    display: flex;
    justify-content: center;
  }

  .post-card {
    padding: 20px;
  }

  .post-title {
    font-size: 20px;
  }

  .post-summary {
    font-size: 14px;
  }

  .post-stats {
    gap: 12px;
  }

  .quick-actions {
    bottom: 20px;
    right: 20px;
  }

  .pagination-container {
    padding: 24px 16px;
  }
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

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

.post-card {
  animation: fadeInUp 0.5s ease-out;
}

.post-card:nth-child(1) { animation-delay: 0.05s; }
.post-card:nth-child(2) { animation-delay: 0.1s; }
.post-card:nth-child(3) { animation-delay: 0.15s; }
.post-card:nth-child(4) { animation-delay: 0.2s; }
.post-card:nth-child(5) { animation-delay: 0.25s; }

.stat-card {
  animation: slideInLeft 0.6s ease-out;
}

.stat-card:nth-child(1) { animation-delay: 0.1s; }
.stat-card:nth-child(2) { animation-delay: 0.2s; }
.stat-card:nth-child(3) { animation-delay: 0.3s; }

.hot-post-card {
  animation: fadeInUp 0.5s ease-out;
}

.hot-post-card:nth-child(1) { animation-delay: 0.1s; }
.hot-post-card:nth-child(2) { animation-delay: 0.2s; }
.hot-post-card:nth-child(3) { animation-delay: 0.3s; }

.fab-button:active {
  animation: pulse 0.3s ease;
}

/* Smooth Scrolling */
html {
  scroll-behavior: smooth;
}

/* Selection Style */
::selection {
  background-color: var(--primary-color);
  color: white;
}

/* Focus Styles */
.post-card:focus-visible {
  outline: 2px solid var(--primary-color);
  outline-offset: 4px;
}

/* Loading State */
.posts-container.loading {
  opacity: 0.6;
  pointer-events: none;
}
</style>