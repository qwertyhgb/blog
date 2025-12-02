<script setup lang="ts">
import { ref, onMounted, h } from 'vue'
import { 
  NButton, 
  NDataTable, 
  NModal, 
  NForm, 
  NFormItem, 
  NInput, 
  NSelect, 
  NTag, 
  NCard,
  NSpace,
  useMessage,
  useDialog,
  type FormInst,
  type DataTableColumns
} from 'naive-ui'
import { AddOutline, CreateOutline, TrashOutline } from '@vicons/ionicons5'

const message = useMessage()
const dialog = useDialog()

const loading = ref(false)
const showModal = ref(false)
const isEdit = ref(false)
const currentUserId = ref<number | null>(null)

const userFormRef = ref<FormInst | null>(null)
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
    // Naive UI doesn't have built-in email validation type, so we use regex or custom validator if needed, 
    // but for now simple required check is fine, or we can add a regex pattern.
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' } 
  ],
  role: [
    { required: true, message: '请选择用户角色', trigger: 'change' }
  ]
}

const users = ref<any[]>([])

const roleOptions = [
  { label: '普通用户', value: 'user' },
  { label: '管理员', value: 'admin' }
]

const columns: DataTableColumns = [
  {
    title: 'ID',
    key: 'id',
    width: 80
  },
  {
    title: '用户名',
    key: 'username',
    minWidth: 120
  },
  {
    title: '昵称',
    key: 'nickname',
    minWidth: 120
  },
  {
    title: '邮箱',
    key: 'email',
    minWidth: 180
  },
  {
    title: '角色',
    key: 'role',
    width: 100,
    render(row: any) {
      return h(
        NTag,
        { type: getRoleType(row.role) },
        { default: () => getRoleText(row.role) }
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
    title: '最后登录',
    key: 'lastLoginTime',
    width: 160,
    render(row: any) {
      return row.lastLoginTime ? formatDate(row.lastLoginTime) : '从未登录'
    }
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
                onClick: () => deleteUser(row.id)
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
  showModal.value = true
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
  showModal.value = true
}

const submitUser = async () => {
  if (!userFormRef.value) return
  
  userFormRef.value.validate(async (errors) => {
    if (!errors) {
      try {
        const { userApi } = await import('@/api')
        if (isEdit.value && currentUserId.value) {
          await userApi.updateUser(currentUserId.value, userForm.value)
          message.success('更新成功')
        } else {
          message.warning('暂不支持创建用户，请通过注册页面注册')
          return
        }
        
        showModal.value = false
        fetchUsers()
      } catch (error: any) {
        message.error(error.message || (isEdit.value ? '更新失败' : '创建失败'))
      }
    }
  })
}

const deleteUser = async (id: number) => {
  dialog.warning({
    title: '提示',
    content: '确定要删除这个用户吗？',
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        const { userApi } = await import('@/api')
        await userApi.deleteUser(id)
        message.success('删除成功')
        fetchUsers()
      } catch (error: any) {
        message.error(error.message || '删除失败')
      }
    }
  })
}

const cancelDialog = () => {
  showModal.value = false
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
      return 'error'
    case 'USER':
      return 'success'
    default:
      return 'default'
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
      <n-button type="primary" @click="showCreateDialog">
        <template #icon>
          <n-icon :component="AddOutline" />
        </template>
        新建用户
      </n-button>
    </div>
    
    <div class="user-table">
      <n-data-table
        :columns="columns"
        :data="users"
        :loading="loading"
        :bordered="false"
        :single-line="false"
      />
    </div>
    
    <n-modal v-model:show="showModal">
      <n-card
        style="width: 500px"
        :title="isEdit ? '编辑用户' : '新建用户'"
        :bordered="false"
        size="huge"
        role="dialog"
        aria-modal="true"
      >
        <n-form
          ref="userFormRef"
          :model="userForm"
          :rules="userRules"
          label-placement="left"
          label-width="80"
        >
          <n-form-item label="用户名" path="username">
            <n-input v-model:value="userForm.username" placeholder="请输入用户名" :disabled="isEdit" />
          </n-form-item>
          
          <n-form-item label="昵称" path="nickname">
            <n-input v-model:value="userForm.nickname" placeholder="请输入昵称" />
          </n-form-item>
          
          <n-form-item label="邮箱" path="email">
            <n-input v-model:value="userForm.email" placeholder="请输入邮箱" />
          </n-form-item>
          
          <n-form-item label="角色" path="role">
            <n-select v-model:value="userForm.role" :options="roleOptions" placeholder="请选择角色" />
          </n-form-item>
        </n-form>
        
        <template #footer>
          <n-space justify="end">
            <n-button @click="cancelDialog">取消</n-button>
            <n-button type="primary" @click="submitUser">确定</n-button>
          </n-space>
        </template>
      </n-card>
    </n-modal>
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
  color: var(--n-text-color);
}

.user-table {
  background-color: var(--n-color);
  border-radius: 8px;
  padding: 20px;
}
</style>