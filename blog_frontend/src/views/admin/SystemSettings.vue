<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { 
  NTabs, 
  NTabPane, 
  NCard, 
  NForm, 
  NFormItem, 
  NInput, 
  NInputNumber, 
  NSwitch, 
  NButton, 
  NDivider,
  useMessage,
  type FormInst
} from 'naive-ui'

const message = useMessage()

const loading = ref(false)
const activeTab = ref('site')

const siteSettings = ref({
  siteName: '个人博客',
  siteDescription: '分享技术心得与生活感悟',
  siteKeywords: '博客,技术,生活',
  siteAuthor: '博主',
  siteUrl: 'https://example.com',
  pageSize: 10,
  commentReview: true,
  allowRegister: true
})

const systemSettings = ref({
  uploadMaxSize: 5,
  uploadAllowedTypes: 'jpg,jpeg,png,gif,pdf,doc,docx',
  emailHost: '',
  emailPort: 587,
  emailUsername: '',
  emailPassword: '',
  emailFrom: ''
})

const siteSettingsRef = ref<FormInst | null>(null)
const systemSettingsRef = ref<FormInst | null>(null)

const siteRules = {
  siteName: [
    { required: true, message: '请输入网站名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  siteDescription: [
    { max: 200, message: '描述最多200个字符', trigger: 'blur' }
  ],
  siteKeywords: [
    { max: 100, message: '关键词最多100个字符', trigger: 'blur' }
  ],
  siteAuthor: [
    { required: true, message: '请输入作者名称', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  siteUrl: [
    { required: true, message: '请输入网站URL', trigger: 'blur' }
  ],
  pageSize: [
    { required: true, message: '请输入每页显示数量', trigger: 'blur' }
  ]
}

const systemRules = {
  uploadMaxSize: [
    { required: true, message: '请输入上传文件大小限制', trigger: 'blur' }
  ],
  uploadAllowedTypes: [
    { required: true, message: '请输入允许上传的文件类型', trigger: 'blur' }
  ],
  emailPort: [
    { type: 'number' as const, min: 1, max: 65535, message: '端口在 1 到 65535 之间', trigger: 'blur' }
  ]
}

// 方法
const fetchSettings = async () => {
  try {
    loading.value = true
    // 这里应该有一个专门的API来获取系统设置
    // 暂时使用模拟数据
  } catch (error) {
    console.error('获取系统设置失败:', error)
  } finally {
    loading.value = false
  }
}

const saveSiteSettings = async () => {
  if (!siteSettingsRef.value) return
  
  siteSettingsRef.value.validate(async (errors) => {
    if (!errors) {
      try {
        // 这里应该有一个专门的API来保存网站设置
        message.success('保存成功')
      } catch (error: any) {
        message.error(error.message || '保存失败')
      }
    }
  })
}

const saveSystemSettings = async () => {
  if (!systemSettingsRef.value) return
  
  systemSettingsRef.value.validate(async (errors) => {
    if (!errors) {
      try {
        // 这里应该有一个专门的API来保存系统设置
        message.success('保存成功')
      } catch (error: any) {
        message.error(error.message || '保存失败')
      }
    }
  })
}

const testEmail = async () => {
  try {
    // 这里应该有一个专门的API来测试邮件发送
    message.success('邮件发送成功')
  } catch (error: any) {
    message.error(error.message || '邮件发送失败')
  }
}

// 初始化
onMounted(() => {
  fetchSettings()
})
</script>

<template>
  <div class="system-settings">
    <div class="page-header">
      <h2>系统设置</h2>
    </div>
    
    <n-card class="settings-card">
      <n-tabs v-model:value="activeTab" type="line" animated>
        <n-tab-pane name="site" tab="网站设置">
          <n-form
            ref="siteSettingsRef"
            :model="siteSettings"
            :rules="siteRules"
            label-placement="left"
            label-width="120"
          >
            <n-form-item label="网站名称" path="siteName">
              <n-input v-model:value="siteSettings.siteName" placeholder="请输入网站名称" />
            </n-form-item>
            
            <n-form-item label="网站描述" path="siteDescription">
              <n-input
                v-model:value="siteSettings.siteDescription"
                type="textarea"
                :rows="3"
                placeholder="请输入网站描述"
              />
            </n-form-item>
            
            <n-form-item label="网站关键词" path="siteKeywords">
              <n-input v-model:value="siteSettings.siteKeywords" placeholder="请输入网站关键词，多个关键词用逗号分隔" />
            </n-form-item>
            
            <n-form-item label="作者名称" path="siteAuthor">
              <n-input v-model:value="siteSettings.siteAuthor" placeholder="请输入作者名称" />
            </n-form-item>
            
            <n-form-item label="网站URL" path="siteUrl">
              <n-input v-model:value="siteSettings.siteUrl" placeholder="请输入网站URL" />
            </n-form-item>
            
            <n-form-item label="每页显示数量" path="pageSize">
              <n-input-number v-model:value="siteSettings.pageSize" :min="5" :max="50" />
            </n-form-item>
            
            <n-form-item label="评论审核">
              <n-switch v-model:value="siteSettings.commentReview" />
              <span class="form-item-tip">开启后，新评论需要审核通过后才会显示</span>
            </n-form-item>
            
            <n-form-item label="允许注册">
              <n-switch v-model:value="siteSettings.allowRegister" />
              <span class="form-item-tip">关闭后，新用户将无法注册</span>
            </n-form-item>
            
            <n-form-item>
              <n-button type="primary" @click="saveSiteSettings">保存设置</n-button>
            </n-form-item>
          </n-form>
        </n-tab-pane>
        
        <n-tab-pane name="system" tab="系统设置">
          <n-form
            ref="systemSettingsRef"
            :model="systemSettings"
            :rules="systemRules"
            label-placement="left"
            label-width="120"
          >
            <n-form-item label="上传文件大小" path="uploadMaxSize">
              <n-input-number v-model:value="systemSettings.uploadMaxSize" :min="1" :max="100" />
              <span class="form-item-tip">单位：MB</span>
            </n-form-item>
            
            <n-form-item label="允许上传类型" path="uploadAllowedTypes">
              <n-input v-model:value="systemSettings.uploadAllowedTypes" placeholder="请输入允许上传的文件类型，多个类型用逗号分隔" />
            </n-form-item>
            
            <n-divider title-placement="left">邮件设置</n-divider>
            
            <n-form-item label="邮件服务器">
              <n-input v-model:value="systemSettings.emailHost" placeholder="请输入邮件服务器地址" />
            </n-form-item>
            
            <n-form-item label="邮件端口" path="emailPort">
              <n-input-number v-model:value="systemSettings.emailPort" :min="1" :max="65535" />
            </n-form-item>
            
            <n-form-item label="邮件用户名">
              <n-input v-model:value="systemSettings.emailUsername" placeholder="请输入邮件用户名" />
            </n-form-item>
            
            <n-form-item label="邮件密码">
              <n-input v-model:value="systemSettings.emailPassword" type="password" placeholder="请输入邮件密码" />
            </n-form-item>
            
            <n-form-item label="发件人邮箱">
              <n-input v-model:value="systemSettings.emailFrom" placeholder="请输入发件人邮箱" />
            </n-form-item>
            
            <n-form-item>
              <div style="display: flex; gap: 12px;">
                <n-button type="primary" @click="saveSystemSettings">保存设置</n-button>
                <n-button @click="testEmail">测试邮件发送</n-button>
              </div>
            </n-form-item>
          </n-form>
        </n-tab-pane>
      </n-tabs>
    </n-card>
  </div>
</template>

<style scoped>
.system-settings {
  width: 100%;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  font-size: 20px;
  color: var(--n-text-color);
}

.settings-card {
  background-color: var(--n-color);
}

.form-item-tip {
  margin-left: 10px;
  font-size: 12px;
  color: var(--n-text-color-3);
}
</style>