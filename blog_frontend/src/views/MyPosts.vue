<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { usePostStore } from '@/stores/post'
import { useUserStore } from '@/stores/user'
import { Search, Plus, Edit, Delete, View } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const postStore = usePostStore()
const userStore = useUserStore()

const loading = ref(false)
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(10)

const posts = ref<any[]>([])
const total = ref(0)

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
  try {
    await ElMessageBox.confirm('确定要删除这篇文章吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const { postApi } = await import('@/api')
    await postApi.deletePost(id)
    ElMessage.success('删除成功')
    fetchMyPosts()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

const createPost = () => {
  router.push('/post/edit')
}

const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

const getStatusText = (status: number) => {
  return status === 0 ? '草稿' : status === 1 ? '已发布' : '已下架'
}

const getStatusType = (status: number) => {
  return status === 0 ? 'info' : status === 1 ? 'success' : 'warning'
}

// 初始化
onMounted(() => {
  fetchMyPosts()
})
</script>

<template>
  <div class="my-posts">
    <div class="page-header">
      <h2>我的文章</h2>
      <el-button type="primary" :icon="Plus" @click="createPost">写文章</el-button>
    </div>
    
    <div class="search-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索我的文章"
        class="search-input"
        @keyup.enter="handleSearch"
      >
        <template #append>
          <el-button :icon="Search" @click="handleSearch" />
        </template>
      </el-input>
    </div>
    
    <div class="post-table">
      <el-table
        :data="posts"
        v-loading="loading"
        style="width: 100%"
        border
      >
        <el-table-column prop="id" label="ID" width="80" />
        
        <el-table-column prop="title" label="标题" min-width="200">
          <template #default="{ row }">
            <div class="post-title">{{ row.title }}</div>
          </template>
        </el-table-column>
        
        <el-table-column prop="category" label="分类" width="120">
          <template #default="{ row }">
            {{ row.category?.name || '-' }}
          </template>
        </el-table-column>
        
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="viewCount" label="浏览量" width="100" />
        
        <el-table-column prop="createTime" label="创建时间" width="120">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              :icon="View"
              @click="viewPost(row.id)"
            >
              查看
            </el-button>
            <el-button
              type="success"
              size="small"
              :icon="Edit"
              @click="editPost(row.id)"
            >
              编辑
            </el-button>
            <el-button
              type="danger"
              size="small"
              :icon="Delete"
              @click="deletePost(row.id)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div v-if="posts.length === 0 && !loading" class="empty-state">
        <el-empty description="还没有文章，快去创建一篇吧！">
          <el-button type="primary" @click="createPost">写第一篇文章</el-button>
        </el-empty>
      </div>
      
      <div class="pagination" v-if="total > pageSize">
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
.my-posts {
  width: 100%;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e4e7ed;
}

.page-header h2 {
  margin: 0;
  font-size: 20px;
  color: #303133;
}

.search-bar {
  margin-bottom: 20px;
}

.search-input {
  width: 300px;
}

.post-table {
  background-color: #fff;
}

.post-title {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.empty-state {
  padding: 40px 0;
  text-align: center;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
