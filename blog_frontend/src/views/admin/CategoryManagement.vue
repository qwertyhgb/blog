<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useCategoryStore } from '@/stores/category'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const categoryStore = useCategoryStore()

const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const currentCategoryId = ref<number | null>(null)

const categoryFormRef = ref()
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

// 方法
const fetchCategories = async () => {
  try {
    loading.value = true
    await categoryStore.fetchCategories()
  } catch (error) {
    console.error('获取分类列表失败:', error)
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
  dialogVisible.value = true
}

const showEditDialog = (category: any) => {
  isEdit.value = true
  currentCategoryId.value = category.id
  categoryForm.value = {
    name: category.name,
    description: category.description || ''
  }
  dialogVisible.value = true
}

const submitCategory = async () => {
  if (!categoryFormRef.value) return
  
  await categoryFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      try {
        if (isEdit.value && currentCategoryId.value) {
          await categoryStore.updateCategory(currentCategoryId.value, categoryForm.value)
          ElMessage.success('更新成功')
        } else {
          await categoryStore.createCategory(categoryForm.value)
          ElMessage.success('创建成功')
        }
        
        dialogVisible.value = false
        fetchCategories()
      } catch (error: any) {
        ElMessage.error(error.message || (isEdit.value ? '更新失败' : '创建失败'))
      }
    }
  })
}

const deleteCategory = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这个分类吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await categoryStore.deleteCategory(id)
    ElMessage.success('删除成功')
    fetchCategories()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

const cancelDialog = () => {
  dialogVisible.value = false
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
      <el-button type="primary" :icon="Plus" @click="showCreateDialog">新建分类</el-button>
    </div>
    
    <div class="category-table">
      <el-table
        :data="categoryStore.categories"
        v-loading="loading"
        style="width: 100%"
        border
      >
        <el-table-column prop="id" label="ID" width="80" />
        
        <el-table-column prop="name" label="分类名称" min-width="150" />
        
        <el-table-column prop="description" label="描述" min-width="200" />
        
        <el-table-column prop="postCount" label="文章数量" width="120" />
        
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button
              type="success"
              size="small"
              :icon="Edit"
              @click="showEditDialog(row)"
            >
              编辑
            </el-button>
            <el-button
              type="danger"
              size="small"
              :icon="Delete"
              @click="deleteCategory(row.id)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    
    <el-dialog
      :title="isEdit ? '编辑分类' : '新建分类'"
      v-model="dialogVisible"
      width="500px"
    >
      <el-form
        ref="categoryFormRef"
        :model="categoryForm"
        :rules="categoryRules"
        label-width="80px"
      >
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="categoryForm.name" placeholder="请输入分类名称" />
        </el-form-item>
        
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="categoryForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入分类描述（可选）"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="cancelDialog">取消</el-button>
          <el-button type="primary" @click="submitCategory">确定</el-button>
        </span>
      </template>
    </el-dialog>
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
  color: #303133;
}

.category-table {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 20px;
}
</style>