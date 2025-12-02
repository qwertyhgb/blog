<script setup lang="ts">
import { ref, onMounted, h } from 'vue'
import { useCategoryStore } from '@/stores/category'
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

const categoryStore = useCategoryStore()
const message = useMessage()
const dialog = useDialog()

const loading = ref(false)
const showModal = ref(false)
const isEdit = ref(false)
const currentCategoryId = ref<number | null>(null)

const categoryFormRef = ref<FormInst | null>(null)
const categoryForm = ref({
  name: '',
  description: ''
})

const categoryRules = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
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
    title: '分类名称',
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
                onClick: () => deleteCategory(row.id)
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
const fetchCategories = async () => {
  try {
    loading.value = true
    await categoryStore.fetchCategories()
  } catch (error) {
    console.error('获取分类列表失败:', error)
    message.error('获取分类列表失败')
  } finally {
    loading.value = false
  }
}

const showCreateDialog = () => {
  isEdit.value = false
  currentCategoryId.value = null
  categoryForm.value = {
    name: '',
    description: ''
  }
  showModal.value = true
}

const showEditDialog = (category: any) => {
  isEdit.value = true
  currentCategoryId.value = category.id
  categoryForm.value = {
    name: category.name,
    description: category.description || ''
  }
  showModal.value = true
}

const submitCategory = async () => {
  if (!categoryFormRef.value) return
  
  categoryFormRef.value.validate(async (errors) => {
    if (!errors) {
      try {
        if (isEdit.value && currentCategoryId.value) {
          await categoryStore.updateCategory(currentCategoryId.value, categoryForm.value)
          message.success('更新成功')
        } else {
          await categoryStore.createCategory(categoryForm.value)
          message.success('创建成功')
        }
        
        showModal.value = false
        fetchCategories()
      } catch (error: any) {
        message.error(error.message || (isEdit.value ? '更新失败' : '创建失败'))
      }
    }
  })
}

const deleteCategory = async (id: number) => {
  dialog.warning({
    title: '提示',
    content: '确定要删除这个分类吗？',
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        await categoryStore.deleteCategory(id)
        message.success('删除成功')
        fetchCategories()
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
  fetchCategories()
})
</script>

<template>
  <div class="category-management">
    <div class="page-header">
      <h2>分类管理</h2>
      <n-button type="primary" @click="showCreateDialog">
        <template #icon>
          <n-icon :component="AddOutline" />
        </template>
        新建分类
      </n-button>
    </div>
    
    <div class="category-table">
      <n-data-table
        :columns="columns"
        :data="categoryStore.categories"
        :loading="loading"
        :bordered="false"
        :single-line="false"
      />
    </div>
    
    <n-modal v-model:show="showModal">
      <n-card
        style="width: 500px"
        :title="isEdit ? '编辑分类' : '新建分类'"
        :bordered="false"
        size="huge"
        role="dialog"
        aria-modal="true"
      >
        <n-form
          ref="categoryFormRef"
          :model="categoryForm"
          :rules="categoryRules"
          label-placement="left"
          label-width="80"
        >
          <n-form-item label="分类名称" path="name">
            <n-input v-model:value="categoryForm.name" placeholder="请输入分类名称" />
          </n-form-item>
          
          <n-form-item label="描述" path="description">
            <n-input
              v-model:value="categoryForm.description"
              type="textarea"
              :rows="3"
              placeholder="请输入分类描述（可选）"
            />
          </n-form-item>
        </n-form>
        
        <template #footer>
          <n-space justify="end">
            <n-button @click="cancelDialog">取消</n-button>
            <n-button type="primary" @click="submitCategory">确定</n-button>
          </n-space>
        </template>
      </n-card>
    </n-modal>
  </div>
</template>

<style scoped>
.category-management {
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

.category-table {
  background-color: var(--n-color);
  border-radius: 8px;
  padding: 20px;
}
</style>