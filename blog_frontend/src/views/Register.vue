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
  useMessage,
  type FormInst
} from 'naive-ui'
import { PersonOutline, LockClosedOutline, MailOutline } from '@vicons/ionicons5'

const router = useRouter()
const userStore = useUserStore()
const message = useMessage()

const loading = ref(false)
const registerFormRef = ref<FormInst | null>(null)
const agreeTerms = ref(false)

const registerForm = reactive({
  username: '',
  nickname: '',
  email: '',
  password: '',
  confirmPassword: ''
})

const checkPassword = (_rule: any, value: string) => {
  return new Promise<void>((resolve, reject) => {
    if (value === '') {
      reject(new Error('请再次输入密码'))
    } else if (value !== registerForm.password) {
      reject(new Error('两次输入密码不一致'))
    } else {
      resolve()
    }
  })
}

const registerRules = {
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
    { type: 'email' as const, message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: checkPassword, trigger: 'blur' }
  ]
}

const handleRegister = (e: Event) => {
  e.preventDefault()
  registerFormRef.value?.validate(async (errors) => {
    if (!errors) {
      try {
        loading.value = true
        await userStore.register({
          username: registerForm.username,
          nickname: registerForm.nickname,
          email: registerForm.email,
          password: registerForm.password
        })
        message.success('注册成功，请登录')
        router.push('/login')
      } catch (error: any) {
        message.error(error.message || '注册失败')
      } finally {
        loading.value = false
      }
    }
  })
}

const goToLogin = () => {
  router.push('/login')
}
</script>

<template>
  <div class="auth-container">
    <div class="auth-left">
      <div class="brand-section">
        <div class="brand-icon">📝</div>
        <h1 class="brand-title">加入我们</h1>
        <p class="brand-subtitle">开启你的创作之旅</p>
      </div>
      <div class="features">
        <div class="feature-item">
          <div class="feature-icon">🎨</div>
          <div class="feature-text">
            <h3>自由创作</h3>
            <p>无限制的创作空间，记录你的想法</p>
          </div>
        </div>
        <div class="feature-item">
          <div class="feature-icon">🌟</div>
          <div class="feature-text">
            <h3>精美排版</h3>
            <p>专业的排版工具，让内容更出彩</p>
          </div>
        </div>
        <div class="feature-item">
          <div class="feature-icon">🔒</div>
          <div class="feature-text">
            <h3>安全可靠</h3>
            <p>数据加密存储，保护你的隐私</p>
          </div>
        </div>
      </div>
    </div>

    <div class="auth-right">
      <div class="auth-card">
        <div class="auth-header">
          <h1 class="auth-title">创建账号</h1>
          <p class="auth-subtitle">填写以下信息开始使用</p>
        </div>
        
        <n-form
          ref="registerFormRef"
          :model="registerForm"
          :rules="registerRules"
          :show-label="false"
        >
          <n-form-item path="username">
            <n-input
              v-model:value="registerForm.username"
              placeholder="用户名（用于登录）"
              size="large"
              :input-props="{ autocomplete: 'username' }"
            >
              <template #prefix>
                <n-icon :component="PersonOutline" />
              </template>
            </n-input>
          </n-form-item>
          
          <n-form-item path="nickname">
            <n-input
              v-model:value="registerForm.nickname"
              placeholder="昵称（显示名称）"
              size="large"
            >
              <template #prefix>
                <n-icon :component="PersonOutline" />
              </template>
            </n-input>
          </n-form-item>
          
          <n-form-item path="email">
            <n-input
              v-model:value="registerForm.email"
              placeholder="邮箱地址"
              size="large"
              :input-props="{ autocomplete: 'email' }"
            >
              <template #prefix>
                <n-icon :component="MailOutline" />
              </template>
            </n-input>
          </n-form-item>
          
          <n-form-item path="password">
            <n-input
              v-model:value="registerForm.password"
              type="password"
              show-password-on="click"
              placeholder="密码（至少6位）"
              size="large"
              :input-props="{ autocomplete: 'new-password' }"
            >
              <template #prefix>
                <n-icon :component="LockClosedOutline" />
              </template>
            </n-input>
          </n-form-item>
          
          <n-form-item path="confirmPassword">
            <n-input
              v-model:value="registerForm.confirmPassword"
              type="password"
              show-password-on="click"
              placeholder="确认密码"
              size="large"
              :input-props="{ autocomplete: 'new-password' }"
            >
              <template #prefix>
                <n-icon :component="LockClosedOutline" />
              </template>
            </n-input>
          </n-form-item>

          <div class="terms">
            <n-checkbox v-model:checked="agreeTerms">
              我已阅读并同意<n-button text type="primary" size="small">服务条款</n-button>和<n-button text type="primary" size="small">隐私政策</n-button>
            </n-checkbox>
          </div>
          
          <n-button
            type="primary"
            block
            size="large"
            :loading="loading"
            :disabled="!agreeTerms"
            @click="handleRegister"
            strong
          >
            注册
          </n-button>
        </n-form>
        
        <div class="auth-footer">
          <span>已有账号？</span>
          <n-button text type="primary" @click="goToLogin" strong>立即登录</n-button>
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
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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

.terms {
  margin-bottom: 24px;
  font-size: 13px;
}

.auth-footer {
  margin-top: 24px;
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