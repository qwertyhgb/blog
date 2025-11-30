<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { usePostStore } from '@/stores/post'
import { useCommentStore } from '@/stores/comment'
import { useUserStore } from '@/stores/user'
import { Calendar, View, User, Tag, Folder, Edit, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'
import 'highlight.js/styles/github.css'

const route = useRoute()
const router = useRouter()
const postStore = usePostStore()
const commentStore = useCommentStore()
const userStore = useUserStore()

const postId = computed(() => Number(route.params.id))
const post = computed(() => postStore.currentPost)
const comments = computed(() => commentStore.comments)
const isLoggedIn = computed(() => userStore.isLoggedIn)
const isAdmin = computed(() => userStore.isAdmin)

const loading = ref(false)
const commentLoading = ref(false)
const submittingComment = ref(false)
const showCommentForm = ref(false)
const commentFormRef = ref()

const commentForm = ref({
  content: ''
})

const commentRules = {
  content: [
    { required: true, message: '请输入评论内容', trigger: 'blur' },
    { min: 5, max: 500, message: '长度在 5 到 500 个字符', trigger: 'blur' }
  ]
}

// 初始化Markdown渲染器
const md = new MarkdownIt({
  highlight: function (str, lang) {
    if (lang && hljs.getLanguage(lang)) {
      try {
        return hljs.highlight(str, { language: lang }).value
      } catch (__) {}
    }
    return ''
  }
})

// 计算渲染后的HTML内容
const renderedContent = computed(() => {
  return post.value ? md.render(post.value.content) : ''
})

// 方法
const fetchPost = async () => {
  try {
    loading.value = true
    await postStore.fetchPostById(postId.value)
    await commentStore.fetchCommentsByPostId(postId.value)
  } catch (error: any) {
    ElMessage.error(error.message || '获取文章详情失败')
    router.push('/')
  } finally {
    loading.value = false
  }
}

const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const goToCategory = (id: number) => {
  router.push(`/category/${id}`)
}

const goToTag = (id: number) => {
  router.push(`/tag/${id}`)
}

const editPost = () => {
  router.push(`/post/edit/${postId.value}`)
}

const deletePost = async () => {
  try {
    await ElMessageBox.confirm('确定要删除这篇文章吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await postStore.deletePost(postId.value)
    ElMessage.success('删除成功')
    router.push('/')
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

const toggleCommentForm = () => {
  showCommentForm.value = !showCommentForm.value
}

const submitComment = async () => {
  if (!commentFormRef.value) return
  
  await commentFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      try {
        submittingComment.value = true
        await commentStore.createComment({
          postId: postId.value,
          content: commentForm.value.content
        })
        ElMessage.success('评论成功')
        commentForm.value.content = ''
        showCommentForm.value = false
        await commentStore.fetchCommentsByPostId(postId.value)
      } catch (error: any) {
        ElMessage.error(error.message || '评论失败')
      } finally {
        submittingComment.value = false
      }
    }
  })
}

const deleteComment = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这条评论吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await commentStore.deleteComment(id)
    ElMessage.success('删除成功')
    await commentStore.fetchCommentsByPostId(postId.value)
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

// 初始化
onMounted(() => {
  fetchPost()
})
</script>

<template>
  <div class="post-detail" v-loading="loading">
    <div v-if="post" class="post-container">
      <div class="post-header">
        <h1 class="post-title">{{ post.title }}</h1>
        
        <div class="post-meta">
          <span class="meta-item">
            <el-icon><Calendar /></el-icon>
            {{ formatDate(post.createTime) }}
          </span>
          <span class="meta-item">
            <el-icon><User /></el-icon>
            {{ post.author }}
          </span>
          <span class="meta-item">
            <el-icon><View /></el-icon>
            {{ post.viewCount }} 次浏览
          </span>
        </div>
        
        <div class="post-tags">
          <span class="category-tag" @click="goToCategory(post.category.id)">
            <el-icon><Folder /></el-icon>
            {{ post.category.name }}
          </span>
          <span 
            class="tag-item" 
            v-for="tag in post.tags" 
            :key="tag.id"
            @click="goToTag(tag.id)"
          >
            <el-icon><Tag /></el-icon>
            {{ tag.name }}
          </span>
        </div>
        
        <div class="post-actions" v-if="isAdmin">
          <el-button type="primary" :icon="Edit" @click="editPost">编辑</el-button>
          <el-button type="danger" :icon="Delete" @click="deletePost">删除</el-button>
        </div>
      </div>
      
      <div class="post-content">
        <div v-html="renderedContent" class="markdown-body"></div>
      </div>
      
      <div class="post-comments">
        <h3>评论 ({{ comments.length }})</h3>
        
        <div class="comment-form-toggle" v-if="isLoggedIn">
          <el-button @click="toggleCommentForm">
            {{ showCommentForm ? '取消评论' : '发表评论' }}
          </el-button>
        </div>
        
        <div class="comment-form" v-if="showCommentForm && isLoggedIn">
          <el-form
            ref="commentFormRef"
            :model="commentForm"
            :rules="commentRules"
            label-width="0"
          >
            <el-form-item prop="content">
              <el-input
                v-model="commentForm.content"
                type="textarea"
                :rows="4"
                placeholder="请输入评论内容..."
              />
            </el-form-item>
            
            <el-form-item>
              <el-button
                type="primary"
                :loading="submittingComment"
                @click="submitComment"
              >
                提交评论
              </el-button>
            </el-form-item>
          </el-form>
        </div>
        
        <div class="comment-list" v-loading="commentLoading">
          <div v-if="comments.length === 0" class="empty-comments">
            <el-empty description="暂无评论" />
          </div>
          
          <div v-else class="comment-item" v-for="comment in comments" :key="comment.id">
            <div class="comment-header">
              <div class="comment-user">
                <el-avatar :size="32" :src="comment.user.avatar">
                  {{ comment.user.nickname ? comment.user.nickname.charAt(0) : 'U' }}
                </el-avatar>
                <span class="username">{{ comment.user.nickname || comment.user.username }}</span>
              </div>
              <div class="comment-time">{{ formatDate(comment.createTime) }}</div>
            </div>
            
            <div class="comment-content">{{ comment.content }}</div>
            
            <div class="comment-actions" v-if="isAdmin || (isLoggedIn && userStore.userInfo.id === comment.user.id)">
              <el-button type="danger" size="small" @click="deleteComment(comment.id)">删除</el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.post-detail {
  width: 100%;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.post-container {
  max-width: 800px;
  margin: 0 auto;
}

.post-header {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e4e7ed;
}

.post-title {
  font-size: 28px;
  margin-bottom: 15px;
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

.post-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 15px;
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

.post-actions {
  margin-top: 15px;
}

.post-content {
  margin-bottom: 40px;
  line-height: 1.8;
}

.markdown-body {
  font-size: 16px;
}

.markdown-body h1,
.markdown-body h2,
.markdown-body h3,
.markdown-body h4,
.markdown-body h5,
.markdown-body h6 {
  margin-top: 24px;
  margin-bottom: 16px;
  font-weight: 600;
  line-height: 1.25;
}

.markdown-body h1 {
  font-size: 2em;
  border-bottom: 1px solid #eaecef;
  padding-bottom: 0.3em;
}

.markdown-body h2 {
  font-size: 1.5em;
  border-bottom: 1px solid #eaecef;
  padding-bottom: 0.3em;
}

.markdown-body h3 {
  font-size: 1.25em;
}

.markdown-body p {
  margin-bottom: 16px;
}

.markdown-body code {
  padding: 0.2em 0.4em;
  margin: 0;
  font-size: 85%;
  background-color: rgba(27, 31, 35, 0.05);
  border-radius: 3px;
}

.markdown-body pre {
  padding: 16px;
  overflow: auto;
  font-size: 85%;
  line-height: 1.45;
  background-color: #f6f8fa;
  border-radius: 6px;
  margin-bottom: 16px;
}

.markdown-body pre code {
  display: inline;
  max-width: auto;
  padding: 0;
  margin: 0;
  overflow: visible;
  line-height: inherit;
  word-wrap: normal;
  background-color: transparent;
  border: 0;
}

.markdown-body blockquote {
  padding: 0 1em;
  color: #6a737d;
  border-left: 0.25em solid #dfe2e5;
  margin-bottom: 16px;
}

.markdown-body ul,
.markdown-body ol {
  padding-left: 2em;
  margin-bottom: 16px;
}

.markdown-body li {
  margin-bottom: 0.25em;
}

.markdown-body table {
  border-collapse: collapse;
  border-spacing: 0;
  width: 100%;
  margin-bottom: 16px;
}

.markdown-body table th,
.markdown-body table td {
  padding: 6px 13px;
  border: 1px solid #dfe2e5;
}

.markdown-body table th {
  background-color: #f6f8fa;
  font-weight: 600;
}

.post-comments {
  border-top: 1px solid #e4e7ed;
  padding-top: 20px;
}

.post-comments h3 {
  margin-bottom: 20px;
  font-size: 20px;
  color: #303133;
}

.comment-form-toggle {
  margin-bottom: 20px;
}

.comment-form {
  margin-bottom: 30px;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.comment-list {
  margin-top: 20px;
}

.comment-item {
  padding: 15px 0;
  border-bottom: 1px solid #f0f0f0;
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.comment-user {
  display: flex;
  align-items: center;
}

.username {
  margin-left: 10px;
  font-weight: 500;
}

.comment-time {
  font-size: 12px;
  color: #909399;
}

.comment-content {
  margin-bottom: 10px;
  line-height: 1.6;
}

.comment-actions {
  text-align: right;
}

.empty-comments {
  padding: 20px 0;
  text-align: center;
}
</style>