<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { usePostStore } from '@/stores/post'
import { useTagStore } from '@/stores/category'
import { 
  NCard, 
  NIcon, 
  NEmpty, 
  NTag, 
  NSkeleton 
} from 'naive-ui'
import { 
  PricetagOutline, 
  CalendarOutline, 
  PersonOutline, 
  EyeOutline, 
  FolderOutline 
} from '@vicons/ionicons5'

const route = useRoute()
const router = useRouter()
const postStore = usePostStore()
const tagStore = useTagStore()

const tagId = computed(() => Number(route.params.id))
const loading = ref(false)

const posts = computed(() => postStore.posts)
const tag = computed(() => tagStore.currentTag)

const fetchData = async () => {
  try {
    loading.value = true
    await Promise.all([
      tagStore.fetchTagById(tagId.value),
      postStore.fetchPostsByTag(tagId.value)
    ])
  } catch (error) {
    console.error('获取数据失败:', error)
  } finally {
    loading.value = false
  }
}

const goToPostDetail = (id: number) => {
  router.push(`/post/${id}`)
}

const goToCategory = (id: number) => {
  router.push(`/category/${id}`)
}

const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

const truncateContent = (content: string, length: number = 150) => {
  if (content.length <= length) return content
  return content.substring(0, length) + '...'
}

watch(tagId, () => {
  fetchData()
})

onMounted(() => {
  fetchData()
})
</script>

<template>
  <div class="tag-page">
    <div class="page-header">
      <div class="header-icon">
        <n-icon :component="PricetagOutline" :size="32" />
      </div>
      <div class="header-content">
        <h1 class="page-title">{{ tag?.name || '加载中...' }}</h1>
      </div>
    </div>
    
    <div class="posts-container">
      <template v-if="loading">
        <div v-for="i in 3" :key="i" class="post-card skeleton">
          <n-skeleton text style="width: 60%; height: 28px; margin-bottom: 12px" />
          <n-skeleton text :repeat="2" style="margin-bottom: 12px" />
          <n-skeleton text style="width: 40%" />
        </div>
      </template>

      <template v-else>
        <div v-if="posts.length === 0" class="empty-state">
          <n-empty description="该标签下暂无文章" size="large" />
        </div>
        
        <div 
          v-else 
          v-for="post in posts" 
          :key="post.id" 
          class="post-card"
          @click="goToPostDetail(post.id)"
        >
          <h2 class="post-title">{{ post.title }}</h2>
          <p class="post-summary">{{ truncateContent(post.summary || post.content) }}</p>
          
          <div class="post-footer">
            <div class="post-meta">
              <n-icon :component="CalendarOutline" :size="14" />
              <span>{{ formatDate(post.createTime) }}</span>
              <span class="dot">·</span>
              <span>{{ post.author?.nickname || post.author?.username || '匿名' }}</span>
              <span v-if="post.category" class="category-badge">{{ post.category.name }}</span>
            </div>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<style scoped>
.tag-page {
  max-width: 900px;
  margin: 0 auto;
  padding: 24px 32px;
  min-height: 100vh;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 32px;
  padding-bottom: 24px;
  border-bottom: 1px solid var(--border-color);
}

.header-icon {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--bg-secondary);
  border-radius: 8px;
  color: var(--primary-color);
  flex-shrink: 0;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-color);
  margin: 0;
}

.posts-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.post-card {
  background-color: var(--card-bg);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  padding: 24px;
  cursor: pointer;
  transition: all 0.15s ease;
}

.post-card:hover {
  box-shadow: 0 4px 12px var(--shadow-sm);
  transform: translateY(-2px);
  border-color: var(--text-tertiary);
}

.post-card.skeleton {
  cursor: default;
}

.post-card.skeleton:hover {
  transform: none;
  box-shadow: none;
}

.post-title {
  font-size: 20px;
  font-weight: 600;
  color: var(--text-color);
  margin-bottom: 8px;
  line-height: 1.4;
}

.post-summary {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.6;
  margin-bottom: 16px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.post-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.post-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: var(--text-tertiary);
}

.dot {
  margin: 0 4px;
}

.category-badge {
  padding: 2px 8px;
  background-color: var(--bg-secondary);
  border-radius: 4px;
  font-size: 12px;
  color: var(--text-secondary);
}

.empty-state {
  padding: 80px 0;
  text-align: center;
}

@media (max-width: 768px) {
  .tag-page {
    padding: 24px 16px;
  }

  .page-title {
    font-size: 24px;
  }

  .post-card {
    padding: 16px;
  }

  .post-title {
    font-size: 18px;
  }
}
</style>
