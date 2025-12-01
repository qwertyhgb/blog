<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { usePostStore } from '@/stores/post'
import { useCategoryStore, useTagStore } from '@/stores/category'
import { Calendar, View, User, PriceTag, Folder, Edit } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const postStore = usePostStore()
const categoryStore = useCategoryStore()
const tagStore = useTagStore()
const userStore = useUserStore()

const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)

// 计算属性
const posts = computed(() => postStore.posts)
const total = computed(() => postStore.total)
const categories = computed(() => categoryStore.categories)
const tags = computed(() => tagStore.tags)
const searchKeyword = computed(() => route.query.keyword as string)
const isLoggedIn = computed(() => userStore.isLoggedIn)

// 方法
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

const handlePageChange = (page: number) => {
  currentPage.value = page
  fetchPosts(page)
}

const goToPostDetail = (id: number) => {
  router.push(`/post/${id}`)
}

const goToCategory = (id: number) => {
  router.push(`/category/${id}`)
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

const goToCreatePost = () => {
  router.push('/post/edit')
}

// 初始化
onMounted(async () => {
  await Promise.all([
    fetchPosts(currentPage.value),
    categoryStore.fetchCategories(),
    tagStore.fetchTags()
  ])
})
</script>

<template>
  <div class="home">
    <div class="home-container">
      <div class="main-content">
        <div class="search-result" v-if="searchKeyword">
          <h2>搜索结果: "{{ searchKeyword }}"</h2>
        </div>
        
        <div class="post-list">
          <el-skeleton :loading="loading" animated :count="3">
            <template #template>
              <div class="post-item">
                <el-skeleton-item variant="h3" style="width: 60%; margin-bottom: 10px" />
                <div style="display: flex; margin-bottom: 15px">
                  <el-skeleton-item variant="text" style="width: 100px; margin-right: 20px" />
                  <el-skeleton-item variant="text" style="width: 100px; margin-right: 20px" />
                  <el-skeleton-item variant="text" style="width: 100px" />
                </div>
                <el-skeleton-item variant="p" style="width: 100%" />
                <el-skeleton-item variant="p" style="width: 100%" />
                <el-skeleton-item variant="p" style="width: 60%" />
                <div style="display: flex; margin-top: 15px">
                  <el-skeleton-item variant="text" style="width: 80px; margin-right: 10px" />
                  <el-skeleton-item variant="text" style="width: 80px" />
                </div>
              </div>
            </template>
            <template #default>
              <div v-if="posts.length === 0" class="empty-state">
                <el-empty description="暂无文章" />
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
                  <span v-if="post.category" class="category-tag" @click.stop="goToCategory(post.category.id)">
                    <el-icon><Folder /></el-icon>
                    {{ post.category.name }}
                  </span>
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
            </template>
          </el-skeleton>
        </div>
        
        <div class="pagination" v-if="total > 0">
          <el-pagination
            background
            layout="prev, pager, next"
            :total="total"
            :page-size="pageSize"
            :current-page="currentPage"
            @current-change="handlePageChange"
          />
        </div>
      </div>
      
      <div class="sidebar">
        <div class="sidebar-section">
          <h3>分类</h3>
          <div class="category-list">
            <div 
              class="category-item" 
              v-for="category in categories" 
              :key="category.id"
              @click="goToCategory(category.id)"
            >
              {{ category.name }}
              <span class="post-count">({{ category.postCount || 0 }})</span>
            </div>
          </div>
        </div>
        
        <div class="sidebar-section">
          <h3>标签</h3>
          <div class="tag-cloud">
            <span 
              class="tag-cloud-item" 
              v-for="tag in tags" 
              :key="tag.id"
              @click="goToTag(tag.id)"
            >
              {{ tag.name }}
            </span>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 浮动写文章按钮 -->
    <el-button
      v-if="isLoggedIn"
      type="primary"
      :icon="Edit"
      circle
      size="large"
      class="floating-write-btn"
      @click="goToCreatePost"
      title="写文章"
    />
  </div>
</template>

<style scoped>
.home {
  width: 100%;
}

.home-container {
  display: flex;
  gap: 20px;
}

.main-content {
  flex: 1;
}

.search-result {
  margin-bottom: 20px;
  padding: 10px;
  background-color: #f0f9ff;
  border-left: 4px solid #409eff;
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

.category-tag, .tag-item {
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

.category-tag:hover, .tag-item:hover {
  background-color: #e6f7ff;
  color: #409eff;
}

.category-tag {
  background-color: #e6f7ff;
  color: #409eff;
}

.category-tag .el-icon, .tag-item .el-icon {
  margin-right: 4px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.sidebar {
  width: 300px;
}

.sidebar-section {
  padding: 20px;
  margin-bottom: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.sidebar-section h3 {
  margin-bottom: 15px;
  font-size: 18px;
  color: #303133;
  border-bottom: 1px solid #e4e7ed;
  padding-bottom: 10px;
}

.category-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.category-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 12px;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.category-item:hover {
  background-color: #f0f2f5;
}

.post-count {
  color: #909399;
  font-size: 12px;
}

.tag-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.tag-cloud-item {
  padding: 6px 12px;
  background-color: #f0f2f5;
  border-radius: 16px;
  font-size: 14px;
  color: #606266;
  cursor: pointer;
  transition: background-color 0.3s, color 0.3s;
}

.tag-cloud-item:hover {
  background-color: #409eff;
  color: #fff;
}

.empty-state {
  padding: 40px 0;
  text-align: center;
}

.floating-write-btn {
  position: fixed;
  bottom: 40px;
  right: 40px;
  width: 56px;
  height: 56px;
  font-size: 24px;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
  z-index: 999;
  transition: all 0.3s;
}

.floating-write-btn:hover {
  transform: translateY(-4px);
  box-shadow: 0 6px 20px rgba(64, 158, 255, 0.6);
}
</style>