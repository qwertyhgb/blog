<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { usePostStore } from '@/stores/post'
import { useCommentStore } from '@/stores/comment'
import { useUserStore } from '@/stores/user'
import { 
  Document, 
  View, 
  ChatDotRound, 
  User,
  TrendCharts,
  Timer
} from '@element-plus/icons-vue'

const postStore = usePostStore()
const commentStore = useCommentStore()
const userStore = useUserStore()

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
    
    // 获取文章统计
    await postStore.fetchPosts({ page: 1, size: 1 })
    stats.value.postCount = postStore.total || 0
    
    // 获取评论统计
    await commentStore.fetchCommentsByPostId(1) // 这里应该有一个专门的API来获取所有评论
    stats.value.commentCount = commentStore.comments.length || 0
    
    // 获取用户统计
    // 这里应该有一个专门的API来获取用户统计
    stats.value.userCount = 10 // 模拟数据
    
    // 获取最近文章
    await postStore.fetchPosts({ page: 1, size: 5 })
    recentPosts.value = postStore.posts || []
    
    // 获取最近评论
    // 这里应该有一个专门的API来获取最近评论
    recentComments.value = [] // 模拟数据
    
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
  <div class="admin-dashboard" v-loading="loading">
    <div class="dashboard-header">
      <h2>仪表盘</h2>
      <p>欢迎来到博客管理系统</p>
    </div>
    
    <div class="stats-cards">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="stats-card">
            <div class="stats-content">
              <div class="stats-icon post-icon">
                <el-icon><Document /></el-icon>
              </div>
              <div class="stats-info">
                <div class="stats-number">{{ stats.postCount }}</div>
                <div class="stats-label">文章总数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="6">
          <el-card class="stats-card">
            <div class="stats-content">
              <div class="stats-icon view-icon">
                <el-icon><View /></el-icon>
              </div>
              <div class="stats-info">
                <div class="stats-number">{{ stats.viewCount }}</div>
                <div class="stats-label">总浏览量</div>
              </div>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="6">
          <el-card class="stats-card">
            <div class="stats-content">
              <div class="stats-icon comment-icon">
                <el-icon><ChatDotRound /></el-icon>
              </div>
              <div class="stats-info">
                <div class="stats-number">{{ stats.commentCount }}</div>
                <div class="stats-label">评论总数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="6">
          <el-card class="stats-card">
            <div class="stats-content">
              <div class="stats-icon user-icon">
                <el-icon><User /></el-icon>
              </div>
              <div class="stats-info">
                <div class="stats-number">{{ stats.userCount }}</div>
                <div class="stats-label">用户总数</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
    
    <el-row :gutter="20" class="dashboard-content">
      <el-col :span="12">
        <el-card class="recent-posts">
          <template #header>
            <div class="card-header">
              <span>最近文章</span>
              <el-icon><Timer /></el-icon>
            </div>
          </template>
          
          <div v-if="recentPosts.length === 0" class="empty-state">
            <el-empty description="暂无文章" />
          </div>
          
          <div v-else class="post-list">
            <div class="post-item" v-for="post in recentPosts" :key="post.id">
              <div class="post-title">{{ post.title }}</div>
              <div class="post-meta">
                <span>{{ formatDate(post.createTime) }}</span>
                <span>{{ post.viewCount }} 次浏览</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card class="recent-comments">
          <template #header>
            <div class="card-header">
              <span>最近评论</span>
              <el-icon><ChatDotRound /></el-icon>
            </div>
          </template>
          
          <div v-if="recentComments.length === 0" class="empty-state">
            <el-empty description="暂无评论" />
          </div>
          
          <div v-else class="comment-list">
            <div class="comment-item" v-for="comment in recentComments" :key="comment.id">
              <div class="comment-content">{{ comment.content }}</div>
              <div class="comment-meta">
                <span>{{ comment.user.nickname || comment.user.username }}</span>
                <span>{{ formatDate(comment.createTime) }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.admin-dashboard {
  width: 100%;
}

.dashboard-header {
  margin-bottom: 20px;
}

.dashboard-header h2 {
  margin: 0 0 10px 0;
  font-size: 24px;
  color: #303133;
}

.dashboard-header p {
  margin: 0;
  color: #909399;
}

.stats-cards {
  margin-bottom: 20px;
}

.stats-card {
  height: 120px;
}

.stats-content {
  display: flex;
  align-items: center;
  height: 100%;
}

.stats-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
  font-size: 24px;
  color: #fff;
}

.post-icon {
  background-color: #409eff;
}

.view-icon {
  background-color: #67c23a;
}

.comment-icon {
  background-color: #e6a23c;
}

.user-icon {
  background-color: #f56c6c;
}

.stats-info {
  flex: 1;
}

.stats-number {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  line-height: 1;
  margin-bottom: 5px;
}

.stats-label {
  font-size: 14px;
  color: #909399;
}

.dashboard-content {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header span {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.card-header .el-icon {
  color: #909399;
}

.recent-posts, .recent-comments {
  height: 400px;
}

.post-list, .comment-list {
  height: 320px;
  overflow-y: auto;
}

.post-item {
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.post-item:last-child {
  border-bottom: none;
}

.post-title {
  font-size: 14px;
  color: #303133;
  margin-bottom: 5px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.post-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #909399;
}

.comment-item {
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-content {
  font-size: 14px;
  color: #303133;
  margin-bottom: 5px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.comment-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #909399;
}

.empty-state {
  height: 320px;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>