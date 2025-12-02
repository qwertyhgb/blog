<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { 
  NForm, 
  NFormItem, 
  NInput, 
  NButton,
  NIcon,
  NCheckbox,
  NDivider,
  useMessage,
  type FormInst
} from 'naive-ui'
import { PersonOutline, LockClosedOutline } from '@vicons/ionicons5'

const router = useRouter()
const userStore = useUserStore()
const message = useMessage()

const loading = ref(false)
const loginFormRef = ref<FormInst | null>(null)
const rememberMe = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ]
}

const handleLogin = (e: Event) => {
  e.preventDefault()
  loginFormRef.value?.validate(async (errors) => {
    if (!errors) {
      try {
        loading.value = true
        await userStore.login(loginForm)
        message.success('登录成功')
        router.push('/')
      } catch (error: any) {
        message.error(error.message || '登录失败')
      } finally {
        loading.value = false
      }
    }
  })
}

const goToRegister = () => {
  router.push('/register')
}

const quickLogin = (type: string) => {
  if (type === 'admin') {
    loginForm.username = 'admin'
    loginForm.password = 'admin123'
    handleLogin(new Event('click'))
  }
}
</script>

<template>
  <div class="auth-container">
    <div class="auth-left">
      <div class="brand-section">
        <div class="brand-icon">📝</div>
        <h1 class="brand-title">我的博客</h1>
        <p class="brand-subtitle">记录生活，分享知识</p>
      </div>
      <div class="features">
        <div class="feature-item">
          <div class="feature-icon">✨</div>
          <div class="feature-text">
            <h3>简洁优雅</h3>
            <p>Notion 风格设计，专注内容创作</p>
          </div>
        </div>
        <div class="feature-item">
          <div class="feature-icon">🚀</div>
          <div class="feature-text">
            <h3>快速发布</h3>
            <p>支持 Markdown，一键发布文章</p>
          </div>
        </div>
        <div class="feature-item">
          <div class="feature-icon">💡</div>
          <div class="feature-text">
            <h3>智能管理</h3>
            <p>分类标签，轻松组织你的内容</p>
          </div>
        </div>
      </div>
    </div>

    <div class="auth-right">
      <div class="auth-card">
        <div class="auth-header">
          <h1 class="auth-title">欢迎回来</h1>
          <p class="auth-subtitle">登录以继续你的创作之旅</p>
        </div>
        
        <n-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          :show-label="false"
        >
          <n-form-item path="username">
            <n-input
              v-model:value="loginForm.username"
              placeholder="用户名"
              size="large"
              :input-props="{ autocomplete: 'username' }"
            >
              <template #prefix>
                <n-icon :component="PersonOutline" />
              </template>
            </n-input>
          </n-form-item>
          
          <n-form-item path="password">
            <n-input
              v-model:value="loginForm.password"
              type="password"
              show-password-on="click"
              placeholder="密码"
              size="large"
              :input-props="{ autocomplete: 'current-password' }"
              @keyup.enter="handleLogin"
            >
              <template #prefix>
                <n-icon :component="LockClosedOutline" />
              </template>
            </n-input>
          </n-form-item>

          <div class="form-options">
            <n-checkbox v-model:checked="rememberMe">记住我</n-checkbox>
          </div>
          
          <n-button
            type="primary"
            block
            size="large"
            :loading="loading"
            @click="handleLogin"
            strong
          >
            登录
          </n-button>
        </n-form>
        
        <n-divider style="margin: 24px 0">
          <span style="font-size: 12px; color: var(--text-tertiary)">或</span>
        </n-divider>

        <div class="quick-login">
          <n-button block size="large" secondary @click="quickLogin('admin')">
            <template #icon>
              <n-icon :component="PersonOutline" />
            </template>
            管理员快速登录
          </n-button>
        </div>
        
        <div class="auth-footer">
          <span>还没有账号？</span>
          <n-button text type="primary" @click="goToRegister" strong>立即注册</n-button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.auth-container {
  display: flex;
  min-height: 100vh;
  background-color: var(--bg-color);
}

.auth-left {
  flex: 1;
  display: none;
  flex-direction: column;
  justify-content: center;
  padding: 80px;
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-hover) 100%);
  color: white;
}

.brand-section {
  margin-bottom: 80px;
}

.brand-icon {
  font-size: 64px;
  margin-bottom: 24px;
}

.brand-title {
  font-size: 48px;
  font-weight: 700;
  margin-bottom: 16px;
  color: white;
}

.brand-subtitle {
  font-size: 20px;
  opacity: 0.9;
  color: white;
}

.features {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.feature-item {
  display: flex;
  gap: 20px;
  align-items: flex-start;
}

.feature-icon {
  font-size: 32px;
  flex-shrink: 0;
}

.feature-text h3 {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 8px;
  color: white;
}

.feature-text p {
  font-size: 14px;
  opacity: 0.85;
  line-height: 1.6;
  color: white;
}

.auth-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px;
  background-color: var(--bg-color);
}

.auth-card {
  width: 100%;
  max-width: 420px;
}

.auth-header {
  margin-bottom: 32px;
}

.auth-title {
  font-size: 32px;
  font-weight: 700;
  color: var(--text-color);
  margin-bottom: 8px;
}

.auth-subtitle {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.6;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.quick-login {
  margin-bottom: 24px;
}

.auth-footer {
  text-align: center;
  font-size: 14px;
  color: var(--text-secondary);
}

@media (min-width: 1024px) {
  .auth-left {
    display: flex;
  }
}

@media (max-width: 768px) {
  .auth-right {
    padding: 24px 16px;
  }

  .auth-title {
    font-size: 24px;
  }
}
</style>