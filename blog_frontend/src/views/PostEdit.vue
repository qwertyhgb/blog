<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { usePostStore } from '@/stores/post'
import { useCategoryStore, useTagStore } from '@/stores/category'
import { ArrowLeft, Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const postStore = usePostStore()
const categoryStore = useCategoryStore()
const tagStore = useTagStore()

const postId = computed(() => Number(route.params.id))
const isEdit = computed(() => !!postId.value)
const categories = computed(() => categoryStore.categories)
const tags = computed(() => tagStore.tags)

const loading = ref(false)
const submitting = ref(false)
const postFormRef = ref()

const postForm = reactive({
  title: '',
  content: '',
  summary: '',
  categoryId: null as number | null,
  tagIds: [] as number[]
})

const postRules = {
  title: [
    { required: true, message: '请输入文章标题', trigger: 'blur' },
    { min: 5, max: 100, message: '长度在 5 到 100 个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入文章内容', trigger: 'blur' },
    { min: 10, message: '内容至少10个字符', trigger: 'blur' }
  ],
  summary: [
    { max: 500, message: '摘要最多500个字符', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择文章分类', trigger: 'change' }
  ]
}

const newTagInput = ref('')
const showNewTagInput = ref(false)

// 方法
const fetchPost = async () => {
  if (!isEdit.value) return
  
  try {
    loading.value = true
    await postStore.fetchPostById(postId.value)
    const post = postStore.currentPost
    
    if (post) {
      postForm.title = post.title
      postForm.content = post.content
      postForm.summary = post.summary || ''
      postForm.categoryId = post.category?.id || null
      postForm.tagIds = post.tags?.map((tag: any) => tag.id) || []
    }
  } catch (error: any) {
    ElMessage.error(error.message || '获取文章详情失败')
    router.push('/admin/posts')
  } finally {
    loading.value = false
  }
}

const submitPost = async () => {
  if (!postFormRef.value) return
  
  await postFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      try {
        submitting.value = true
        
        const postData = {
          title: postForm.title,
          content: postForm.content,
          summary: postForm.summary,
          categoryId: postForm.categoryId,
          tagIds: postForm.tagIds
        }
        
        if (isEdit.value) {
          await postStore.updatePost(postId.value, postData)
          ElMessage.success('更新成功')
        } else {
          await postStore.createPost(postData)
          ElMessage.success('创建成功')
        }
        
        router.back()
      } catch (error: any) {
        ElMessage.error(error.message || (isEdit.value ? '更新失败' : '创建失败'))
      } finally {
        submitting.value = false
      }
    }
  })
}

const goBack = () => {
  router.back()
}

const showAddTag = () => {
  showNewTagInput.value = true
}

const addNewTag = async () => {
  if (!newTagInput.value.trim()) {
    ElMessage.warning('请输入标签名称')
    return
  }
  
  try {
    await tagStore.createTag({ name: newTagInput.value.trim() })
    ElMessage.success('标签添加成功')
    newTagInput.value = ''
    showNewTagInput.value = false
    await tagStore.fetchTags()
  } catch (error: any) {
    ElMessage.error(error.message || '添加标签失败')
  }
}

const cancelAddTag = () => {
  newTagInput.value = ''
  showNewTagInput.value = false
}

// 初始化
onMounted(async () => {
  await Promise.all([
    categoryStore.fetchCategories(),
    tagStore.fetchTags(),
    fetchPost()
  ])
})
</script>

<template>
  <div class="post-edit">
    <div class="post-edit-header">
      <el-button :icon="ArrowLeft" @click="goBack">返回</el-button>
      <h2>{{ isEdit ? '编辑文章' : '新建文章' }}</h2>
    </div>
    
    <div class="post-edit-container" v-loading="loading">
      <el-form
        ref="postFormRef"
        :model="postForm"
        :rules="postRules"
        label-width="80px"
      >
        <el-form-item label="标题" prop="title">
          <el-input v-model="postForm.title" placeholder="请输入文章标题" />
        </el-form-item>
        
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="postForm.categoryId" placeholder="请选择文章分类" style="width: 100%">
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="标签">
          <el-select
            v-model="postForm.tagIds"
            multiple
            placeholder="请选择文章标签"
            style="width: 100%"
          >
            <el-option
              v-for="tag in tags"
              :key="tag.id"
              :label="tag.name"
              :value="tag.id"
            />
          </el-select>
          
          <div class="tag-management">
            <el-button type="primary" :icon="Plus" size="small" @click="showAddTag">新建标签</el-button>
            
            <div class="new-tag-input" v-if="showNewTagInput">
              <el-input
                v-model="newTagInput"
                placeholder="请输入标签名称"
                size="small"
                style="width: 200px; margin-right: 10px"
              />
              <el-button type="success" size="small" @click="addNewTag">确定</el-button>
              <el-button size="small" @click="cancelAddTag">取消</el-button>
            </div>
          </div>
        </el-form-item>
        
        <el-form-item label="摘要" prop="summary">
          <el-input
            v-model="postForm.summary"
            type="textarea"
            :rows="3"
            placeholder="请输入文章摘要（可选）"
          />
        </el-form-item>
        
        <el-form-item label="内容" prop="content">
          <el-input
            v-model="postForm.content"
            type="textarea"
            :rows="20"
            placeholder="请输入文章内容，支持Markdown格式"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="submitPost">
            {{ isEdit ? '更新' : '发布' }}
          </el-button>
          <el-button @click="goBack">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<style scoped>
.post-edit {
  width: 100%;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.post-edit-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e4e7ed;
}

.post-edit-header h2 {
  margin: 0 0 0 15px;
  font-size: 20px;
  color: #303133;
}

.post-edit-container {
  max-width: 800px;
}

.tag-management {
  margin-top: 10px;
}

.new-tag-input {
  display: flex;
  align-items: center;
  margin-top: 10px;
}
</style>