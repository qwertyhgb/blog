<script setup lang="ts">
import { ref, onMounted, h } from 'vue'
import { commentApi } from '@/api'
import { 
  NButton, 
  NDataTable, 
  NPagination, 
  NSelect, 
  NTag, 
  NSpace,
  useMessage,
  useDialog,
  type DataTableColumns
} from 'naive-ui'
import { CheckmarkOutline, CloseOutline, TrashOutline } from '@vicons/ionicons5'

const message = useMessage()
const dialog = useDialog()

const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const statusFilter = ref<number | null>(null)

const comments = ref<any[]>([])
const total = ref(0)

const statusOptions = [
  { label: '全部', value: 'all' },
  { label: '待审核', value: 0 },
  { label: '已通过', value: 1 },
  { label: '已拒绝', value: 2 }
]

const columns: DataTableColumns = [
  {
    title: 'ID',
    key: 'id',
    width: 80
  },
  {
    title: '评论内容',
    key: 'content',
    minWidth: 200,
    ellipsis: {
      tooltip: true
    }
  },
  {
    title: '文章ID',
    key: 'postId',
    width: 100
  },
  {
    title: '用户',
    key: 'user',
    width: 120,
    render(row: any) {
      return row.user?.nickname || row.user?.username || '未知'
    }
  },
  {
    title: '状态',
    key: 'status',
    width: 100,
    render(row: any) {
      return h(
        NTag,
        { type: getStatusType(row.status) },
        { default: () => getStatusText(row.status) }
      )
    }
  },
  {
    title: '创建时间',
    key: 'createTime',
    width: 160,
    render(row: any) {
      return formatDate(row.createTime)
    }
  },
  {
    title: '操作',
    key: 'actions',
    width: 200,
    render(row: any) {
      const actions = []
      
      if (row.status === 0) {
        actions.push(
          h(
            NButton,
            {
              size: 'small',
              type: 'success',
              secondary: true,
              onClick: () => approveComment(row.id)
            },
            { icon: () => h(CheckmarkOutline), default: () => '通过' }
          ),
          h(
            NButton,
            {
              size: 'small',
              type: 'warning',
              secondary: true,
              onClick: () => rejectComment(row.id)
            },
            { icon: () => h(CloseOutline), default: () => '拒绝' }
          )
        )
      }
      
      actions.push(
        h(
          NButton,
          {
            size: 'small',
            type: 'error',
            secondary: true,
            onClick: () => deleteComment(row.id)
          },
          { icon: () => h(TrashOutline), default: () => '删除' }
        )
      )
      
      return h(NSpace, { size: 8 }, { default: () => actions })
    }
  }
]

// 方法
const fetchComments = async () => {
  try {
    loading.value = true
    const res = await commentApi.getAdminComments({
      page: currentPage.value,
      size: pageSize.value,
      status: statusFilter.value === null ? undefined : statusFilter.value
    })
    if (res.data) {
      comments.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('获取评论列表失败:', error)
    comments.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

const handleStatusChange = (val: any) => {
  if (val === 'all') {
    statusFilter.value = null
  } else {
    statusFilter.value = val
  }
  currentPage.value = 1
  fetchComments()
}

const handlePageChange = (page: number) => {
  currentPage.value = page
  fetchComments()
}

const approveComment = async (id: number) => {
  try {
    await commentApi.approveComment(id)
    message.success('审核通过')
    fetchComments()
  } catch (error: any) {
    message.error(error.message || '操作失败')
  }
}

const rejectComment = async (id: number) => {
  try {
    await commentApi.rejectComment(id)
    message.success('审核拒绝')
    fetchComments()
  } catch (error: any) {
    message.error(error.message || '操作失败')
  }
}

const deleteComment = async (id: number) => {
  dialog.warning({
    title: '提示',
    content: '确定要删除这条评论吗？',
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        await commentApi.deleteComment(id)
        message.success('删除成功')
        fetchComments()
      } catch (error: any) {
        message.error(error.message || '删除失败')
      }
    }
  })
}

const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const getStatusText = (status: number) => {
  switch (status) {
    case 0:
      return '待审核'
    case 1:
      return '已通过'
    case 2:
      return '已拒绝'
    default:
      return '未知'
  }
}

const getStatusType = (status: number) => {
  switch (status) {
    case 0:
      return 'warning'
    case 1:
      return 'success'
    case 2:
      return 'error'
    default:
      return 'default'
  }
}

// 初始化
onMounted(() => {
  fetchComments()
})
</script>

<template>
  <div class="comment-management">
    <div class="page-header">
      <h2>评论管理</h2>
      <n-select
        placeholder="筛选状态"
        style="width: 120px"
        :options="statusOptions"
        @update:value="handleStatusChange"
      />
    </div>
    
    <div class="comment-table">
      <n-data-table
        :columns="columns"
        :data="comments"
        :loading="loading"
        :bordered="false"
        :single-line="false"
      />
      
      <div class="pagination">
        <n-pagination
          v-model:page="currentPage"
          :page-count="Math.ceil(total / pageSize)"
          :page-size="pageSize"
          @update:page="handlePageChange"
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
  color: var(--n-text-color);
}

.comment-table {
  background-color: var(--n-color);
  border-radius: 8px;
  padding: 20px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>