<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useCommentStore } from '@/stores/comment'
import { usePostStore } from '@/stores/post'
import { Check, Close, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const commentStore = useCommentStore()
const postStore = usePostStore()

const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const statusFilter = ref('all') // all, pending, approved, rejected

const comments = ref<any[]>([])
const total = ref(0)
const posts = ref<any[]>([])

// 方法
const fetchComments = async () => {
  try {
    loading.value = true
    // 这里应该有一个专门的API来获取所有评论，而不是按文章ID获取
    // 暂时使用模拟数据
    comments.value = []
    total.value = 0
  } catch (error) {
    console.error('获取评论列表失败:', error)
  } finally {
    loading.value = false
  }
}

const fetchPosts = async () => {
  try {
    await postStore.fetchPosts({ page: 1, size: 1000 })
    posts.value = postStore.posts
  } catch (error) {
    console.error('获取文章列表失败:', error)
  }
}

const handlePageChange = (page: number) => {
  currentPage.value = page
  fetchComments()
}

const approveComment = async (id: number) => {
  try {
    await commentStore.approveComment(id)
    ElMessage.success('审核通过')
    fetchComments()
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  }
}

const rejectComment = async (id: number) => {
  try {
    await commentStore.rejectComment(id)
    ElMessage.success('审核拒绝')
    fetchComments()
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  }
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
    fetchComments()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

const getPostTitle = (postId: number) => {
  const post = posts.value.find(p => p.id === postId)
  return post ? post.title : '未知文章'
}

const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const getStatusText = (status: string) => {
  switch (status) {
    case 'pending':
      return '待审核'
    case 'approved':
      return '已通过'
    case 'rejected':
      return '已拒绝'
    default:
      return '未知'
  }
}

const getStatusType = (status: string) => {
  switch (status) {
    case 'pending':
      return 'warning'
    case 'approved':
      return 'success'
    case 'rejected':
      return 'danger'
    default:
      return 'info'
  }
}

// 初始化
onMounted(async () => {
  await Promise.all([
    fetchComments(),
    fetchPosts()
  ])
})
</script>

<template>
  <div class="comment-management">
    <div class="page-header">
      <h2>评论管理</h2>
      <el-select v-model="statusFilter" placeholder="筛选状态" style="width: 120px" @change="fetchComments">
        <el-option label="全部" value="all" />
        <el-option label="待审核" value="pending" />
        <el-option label="已通过" value="approved" />
        <el-option label="已拒绝" value="rejected" />
      </el-select>
    </div>
    
    <div class="comment-table">
      <el-table
        :data="comments"
        v-loading="loading"
        style="width: 100%"
        border
      >
        <el-table-column prop="id" label="ID" width="80" />
        
        <el-table-column prop="content" label="评论内容" min-width="200">
          <template #default="{ row }">
            <div class="comment-content">{{ row.content }}</div>
          </template>
        </el-table-column>
        
        <el-table-column label="文章" min-width="150">
          <template #default="{ row }">
            {{ getPostTitle(row.postId) }}
          </template>
        </el-table-column>
        
        <el-table-column label="用户" width="120">
          <template #default="{ row }">
            {{ row.user.nickname || row.user.username }}
          </template>
        </el-table-column>
        
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="createTime" label="创建时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 'pending'"
              type="success"
              size="small"
              :icon="Check"
              @click="approveComment(row.id)"
            >
              通过
            </el-button>
            <el-button
              v-if="row.status === 'pending'"
              type="warning"
              size="small"
              :icon="Close"
              @click="rejectComment(row.id)"
            >
              拒绝
            </el-button>
            <el-button
              type="danger"
              size="small"
              :icon="Delete"
              @click="deleteComment(row.id)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination">
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
  </div>
</template>

<style scoped>
.comment-management {
  width: 100%;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  font-size: 20px;
  color: #303133;
}

.comment-table {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.comment-content {
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>