<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { Edit, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const userStore = useUserStore()

const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const currentUserId = ref<number | null>(null)

const userFormRef = ref()
const userForm = ref({
  username: '',
  nickname: '',
  email: '',
  role: 'user'
})

const userRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择用户角色', trigger: 'change' }
  ]
}

const users = ref<any[]>([])

// 方法
const fetchUsers = async () => {
  try {
    loading.value = true
    const { userApi } = await import('@/api')
    const res = await userApi.getUsers()
    users.value = res.data || []
  } catch (error) {
    console.error('获取用户列表失败:', error)
    users.value = []
  } finally {
    loading.value = false
  }
}

const showCreateDialog = () => {
  isEdit.value = false
  currentUserId.value = null
  userForm.value = {
    username: '',
    nickname: '',
    email: '',
    role: 'user'
  }
  dialogVisible.value = true
}

const showEditDialog = (user: any) => {
  isEdit.value = true
  currentUserId.value = user.id
  userForm.value = {
    username: user.username,
    nickname: user.nickname,
    email: user.email,
    role: user.role
  }
  dialogVisible.value = true
}

const submitUser = async () => {
  if (!userFormRef.value) return
  
  await userFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      try {
        const { userApi } = await import('@/api')
        if (isEdit.value && currentUserId.value) {
          await userApi.updateUser(currentUserId.value, userForm.value)
          ElMessage.success('更新成功')
        } else {
          ElMessage.warning('暂不支持创建用户，请通过注册页面注册')
          return
        }
        
        dialogVisible.value = false
        fetchUsers()
      } catch (error: any) {
        ElMessage.error(error.message || (isEdit.value ? '更新失败' : '创建失败'))
      }
    }
  })
}

const deleteUser = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这个用户吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const { userApi } = await import('@/api')
    await userApi.deleteUser(id)
    ElMessage.success('删除成功')
    fetchUsers()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

const cancelDialog = () => {
  dialogVisible.value = false
}

const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const getRoleText = (role: string) => {
  switch (role?.toUpperCase()) {
    case 'ADMIN':
      return '管理员'
    case 'USER':
      return '普通用户'
    default:
      return '未知'
  }
}

const getRoleType = (role: string) => {
  switch (role?.toUpperCase()) {
    case 'ADMIN':
      return 'danger'
    case 'USER':
      return 'success'
    default:
      return 'info'
  }
}

// 初始化
onMounted(() => {
  fetchUsers()
})
</script>

<template>
  <div class="user-management">
    <div class="page-header">
      <h2>用户管理</h2>
      <el-button type="primary" @click="showCreateDialog">新建用户</el-button>
    </div>
    
    <div class="user-table">
      <el-table
        :data="users"
        v-loading="loading"
        style="width: 100%"
        border
      >
        <el-table-column prop="id" label="ID" width="80" />
        
        <el-table-column prop="username" label="用户名" min-width="120" />
        
        <el-table-column prop="nickname" label="昵称" min-width="120" />
        
        <el-table-column prop="email" label="邮箱" min-width="180" />
        
        <el-table-column prop="role" label="角色" width="100">
          <template #default="{ row }">
            <el-tag :type="getRoleType(row.role)">
              {{ getRoleText(row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="createTime" label="创建时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        
        <el-table-column prop="lastLoginTime" label="最后登录" width="160">
          <template #default="{ row }">
            {{ row.lastLoginTime ? formatDate(row.lastLoginTime) : '从未登录' }}
          </template>
        </el-table-column>
        
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
              @click="deleteUser(row.id)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    
    <el-dialog
      :title="isEdit ? '编辑用户' : '新建用户'"
      v-model="dialogVisible"
      width="500px"
    >
      <el-form
        ref="userFormRef"
        :model="userForm"
        :rules="userRules"
        label-width="80px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" placeholder="请输入用户名" :disabled="isEdit" />
        </el-form-item>
        
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="userForm.nickname" placeholder="请输入昵称" />
        </el-form-item>
        
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        
        <el-form-item label="角色" prop="role">
          <el-select v-model="userForm.role" placeholder="请选择角色" style="width: 100%">
            <el-option label="普通用户" value="user" />
            <el-option label="管理员" value="admin" />
          </el-select>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="cancelDialog">取消</el-button>
          <el-button type="primary" @click="submitUser">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.user-management {
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

.user-table {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 20px;
}
</style>