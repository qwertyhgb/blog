<script setup lang="ts">
import { ref, onMounted, h } from 'vue'
import { useTagStore } from '@/stores/category'
import { 
  NButton, 
  NDataTable, 
  NModal, 
  NForm, 
  NFormItem, 
  NInput, 
  NCard,
  NSpace,
  useMessage,
  useDialog,
  type FormInst,
  type DataTableColumns
} from 'naive-ui'
import { AddOutline, CreateOutline, TrashOutline } from '@vicons/ionicons5'

const tagStore = useTagStore()
const message = useMessage()
const dialog = useDialog()

const loading = ref(false)
const showModal = ref(false)
const isEdit = ref(false)
const currentTagId = ref<number | null>(null)

const tagFormRef = ref<FormInst | null>(null)
const tagForm = ref({
  name: '',
  description: ''
})

const tagRules = {
  name: [
    { required: true, message: '请输入标签名称', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  description: [
    { max: 200, message: '描述最多200个字符', trigger: 'blur' }
  ]
}

const columns: DataTableColumns = [
  {
    title: 'ID',
    key: 'id',
    width: 80
  },
  {
    title: '标签名称',
    key: 'name',
    minWidth: 150
  },
  {
    title: '描述',
    key: 'description',
    minWidth: 200
  },
  {
    title: '文章数量',
    key: 'postCount',
    width: 120
  },
  {
    title: '操作',
    key: 'actions',
    width: 150,
    render(row: any) {
      return h(
        NSpace,
        { size: 8 },
        {
          default: () => [
            h(
              NButton,
              {
                size: 'small',
                type: 'primary',
                secondary: true,
                onClick: () => showEditDialog(row)
              },
              { icon: () => h(CreateOutline), default: () => '编辑' }
            ),
            h(
              NButton,
              {
                size: 'small',
                type: 'error',
                secondary: true,
                onClick: () => deleteTag(row.id)
              },
              { icon: () => h(TrashOutline), default: () => '删除' }
            )
          ]
        }
      )
    }
  }
]

// 方法
const fetchTags = async () => {
  try {
    loading.value = true
    await tagStore.fetchTags()
  } catch (error) {
    console.error('获取标签列表失败:', error)
    message.error('获取标签列表失败')
  } finally {
    loading.value = false
  }
}

const showCreateDialog = () => {
  isEdit.value = false
  currentTagId.value = null
  tagForm.value = {
    name: '',
    description: ''
  }
  showModal.value = true
}

const showEditDialog = (tag: any) => {
  isEdit.value = true
  currentTagId.value = tag.id
  tagForm.value = {
    name: tag.name,
    description: tag.description || ''
  }
  showModal.value = true
}

const submitTag = async () => {
  if (!tagFormRef.value) return
  
  tagFormRef.value.validate(async (errors) => {
    if (!errors) {
      try {
        if (isEdit.value && currentTagId.value) {
          await tagStore.updateTag(currentTagId.value, tagForm.value)
          message.success('更新成功')
        } else {
          await tagStore.createTag(tagForm.value)
          message.success('创建成功')
        }
        
        showModal.value = false
        fetchTags()
      } catch (error: any) {
        message.error(error.message || (isEdit.value ? '更新失败' : '创建失败'))
      }
    }
  })
}

const deleteTag = async (id: number) => {
  dialog.warning({
    title: '提示',
    content: '确定要删除这个标签吗？',
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        await tagStore.deleteTag(id)
        message.success('删除成功')
        fetchTags()
      } catch (error: any) {
        message.error(error.message || '删除失败')
      }
    }
  })
}

const cancelDialog = () => {
  showModal.value = false
}

// 初始化
onMounted(() => {
  fetchTags()
})
</script>

<template>
  <div class="tag-management">
    <div class="page-header">
      <h2>标签管理</h2>
      <n-button type="primary" @click="showCreateDialog">
        <template #icon>
          <n-icon :component="AddOutline" />
        </template>
        新建标签
      </n-button>
    </div>
    
    <div class="tag-table">
      <n-data-table
        :columns="columns"
        :data="tagStore.tags"
        :loading="loading"
        :bordered="false"
        :single-line="false"
      />
    </div>
    
    <n-modal v-model:show="showModal">
      <n-card
        style="width: 500px"
        :title="isEdit ? '编辑标签' : '新建标签'"
        :bordered="false"
        size="huge"
        role="dialog"
        aria-modal="true"
      >
        <n-form
          ref="tagFormRef"
          :model="tagForm"
          :rules="tagRules"
          label-placement="left"
          label-width="80"
        >
          <n-form-item label="标签名称" path="name">
            <n-input v-model:value="tagForm.name" placeholder="请输入标签名称" />
          </n-form-item>
          
          <n-form-item label="描述" path="description">
            <n-input
              v-model:value="tagForm.description"
              type="textarea"
              :rows="3"
              placeholder="请输入标签描述（可选）"
            />
          </n-form-item>
        </n-form>
        
        <template #footer>
          <n-space justify="end">
            <n-button @click="cancelDialog">取消</n-button>
            <n-button type="primary" @click="submitTag">确定</n-button>
          </n-space>
        </template>
      </n-card>
    </n-modal>
  </div>
</template>

<style scoped>
.tag-management {
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

.tag-table {
  background-color: var(--n-color);
  border-radius: 8px;
  padding: 20px;
}
</style>