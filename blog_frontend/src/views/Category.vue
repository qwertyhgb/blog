<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { usePostStore } from '@/stores/post'
import { useCategoryStore } from '@/stores/category'
import { Calendar, View, User, PriceTag, Folder } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const postStore = usePostStore()
const categoryStore = useCategoryStore()

const categoryId = computed(() => Number(route.params.id))
const loading = ref(false)

const posts = computed(() => postStore.posts)
const category = computed(() => categoryStore.currentCategory)

const fetchData = async () => {
  try {
    loading.value = true
    await Promise.all([
      categoryStore.fetchCategoryById(categoryId.value),
      postStore.fetchPostsByCategory(categoryId.value)
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

const goToTag = (id: number) => {
  router.push(`/tag/${id}`)
}

const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

const truncateContent = (content: string, length: number = 150) => {
  if (content.length <= length) return content
  return content.substring(0, length) + '...'
}

watch(categoryId, () => {
  fetchData()
})

onMounted(() => {
  fetchData()
})
</script>

<template>
  <div class="category-page" v-loading="loading">
    <div class="page-header">
      <h1>
        <el-icon><Folder /></el-icon>
        分类: {{ category?.name || '加载中...' }}
      </h1>
      <p v-if="category?.description">{{ category.description }}</p>
    </div>
    
    <div class="post-list">
      <div v-if="posts.length === 0" class="empty-state">
        <el-empty description="该分类下暂无文章" />
      </div>
      
      <div v-else class="post-item" v-for="post in posts" :key="post.id" @click="goToPostDetail(post.id)">
        <h2 class="post-title">{{ post.title }}</h2>
        
        <div class="post-meta">
          <span class="meta-item">
            <el-icon><Calendar /></el-icon>
            {{ formatDate(post.createTime) }}
          </span>
          <span class="meta-item">
            <el-icon><User /></el-icon>
            {{ post.author?.nickname || post.author?.username || '匿名' }}
          </span>
          <span class="meta-item">
            <el-icon><View /></el-icon>
            {{ post.viewCount }} 次浏览
          </span>
        </div>
        
        <div class="post-summary">
          {{ truncateContent(post.summary || post.content) }}
        </div>
        
        <div class="post-tags">
          <span 
            class="tag-item" 
            v-for="tag in post.tags" 
            :key="tag.id"
            @click.stop="goToTag(tag.id)"
          >
            <el-icon><PriceTag /></el-icon>
            {{ tag.name }}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.category-page {
  width: 100%;
}

.page-header {
  margin-bottom: 30px;
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.page-header h1 {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 24px;
  color: #303133;
  margin-bottom: 10px;
}

.page-header p {
  color: #909399;
  margin: 0;
}

.post-list {
  margin-bottom: 20px;
}

.post-item {
  padding: 20px;
  margin-bottom: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
}

.post-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.15);
}

.post-title {
  font-size: 22px;
  margin-bottom: 10px;
  color: #303133;
}

.post-meta {
  display: flex;
  margin-bottom: 15px;
  font-size: 14px;
  color: #909399;
}

.meta-item {
  display: flex;
  align-items: center;
  margin-right: 20px;
}

.meta-item .el-icon {
  margin-right: 5px;
}

.post-summary {
  margin-bottom: 15px;
  line-height: 1.6;
  color: #606266;
}

.post-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-item {
  display: inline-flex;
  align-items: center;
  padding: 4px 8px;
  background-color: #f0f2f5;
  border-radius: 4px;
  font-size: 12px;
  color: #606266;
  cursor: pointer;
  transition: background-color 0.3s;
}

.tag-item:hover {
  background-color: #e6f7ff;
  color: #409eff;
}

.tag-item .el-icon {
  margin-right: 4px;
}

.empty-state {
  padding: 40px 0;
  text-align: center;
}
</style>
