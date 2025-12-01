<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

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

const siteSettingsRef = ref()
const systemSettingsRef = ref()

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
    { required: true, message: '请输入网站URL', trigger: 'blur' },
    { type: 'url', message: '请输入正确的URL格式', trigger: 'blur' }
  ],
  pageSize: [
    { required: true, message: '请输入每页显示数量', trigger: 'blur' },
    { type: 'number', min: 5, max: 50, message: '数量在 5 到 50 之间', trigger: 'blur' }
  ]
}

const systemRules = {
  uploadMaxSize: [
    { required: true, message: '请输入上传文件大小限制', trigger: 'blur' },
    { type: 'number', min: 1, max: 100, message: '大小在 1 到 100 MB之间', trigger: 'blur' }
  ],
  uploadAllowedTypes: [
    { required: true, message: '请输入允许上传的文件类型', trigger: 'blur' }
  ],
  emailPort: [
    { type: 'number', min: 1, max: 65535, message: '端口在 1 到 65535 之间', trigger: 'blur' }
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
  
  await siteSettingsRef.value.validate(async (valid: boolean) => {
    if (valid) {
      try {
        // 这里应该有一个专门的API来保存网站设置
        ElMessage.success('保存成功')
      } catch (error: any) {
        ElMessage.error(error.message || '保存失败')
      }
    }
  })
}

const saveSystemSettings = async () => {
  if (!systemSettingsRef.value) return
  
  await systemSettingsRef.value.validate(async (valid: boolean) => {
    if (valid) {
      try {
        // 这里应该有一个专门的API来保存系统设置
        ElMessage.success('保存成功')
      } catch (error: any) {
        ElMessage.error(error.message || '保存失败')
      }
    }
  })
}

const testEmail = async () => {
  try {
    // 这里应该有一个专门的API来测试邮件发送
    ElMessage.success('邮件发送成功')
  } catch (error: any) {
    ElMessage.error(error.message || '邮件发送失败')
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
    
    <el-tabs v-model="activeTab" class="settings-tabs">
      <el-tab-pane label="网站设置" name="site">
        <el-card class="settings-card">
          <el-form
            ref="siteSettingsRef"
            :model="siteSettings"
            :rules="siteRules"
            label-width="120px"
          >
            <el-form-item label="网站名称" prop="siteName">
              <el-input v-model="siteSettings.siteName" placeholder="请输入网站名称" />
            </el-form-item>
            
            <el-form-item label="网站描述" prop="siteDescription">
              <el-input
                v-model="siteSettings.siteDescription"
                type="textarea"
                :rows="3"
                placeholder="请输入网站描述"
              />
            </el-form-item>
            
            <el-form-item label="网站关键词" prop="siteKeywords">
              <el-input v-model="siteSettings.siteKeywords" placeholder="请输入网站关键词，多个关键词用逗号分隔" />
            </el-form-item>
            
            <el-form-item label="作者名称" prop="siteAuthor">
              <el-input v-model="siteSettings.siteAuthor" placeholder="请输入作者名称" />
            </el-form-item>
            
            <el-form-item label="网站URL" prop="siteUrl">
              <el-input v-model="siteSettings.siteUrl" placeholder="请输入网站URL" />
            </el-form-item>
            
            <el-form-item label="每页显示数量" prop="pageSize">
              <el-input-number v-model="siteSettings.pageSize" :min="5" :max="50" />
            </el-form-item>
            
            <el-form-item label="评论审核">
              <el-switch v-model="siteSettings.commentReview" />
              <span class="form-item-tip">开启后，新评论需要审核通过后才会显示</span>
            </el-form-item>
            
            <el-form-item label="允许注册">
              <el-switch v-model="siteSettings.allowRegister" />
              <span class="form-item-tip">关闭后，新用户将无法注册</span>
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="saveSiteSettings">保存设置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>
      
      <el-tab-pane label="系统设置" name="system">
        <el-card class="settings-card">
          <el-form
            ref="systemSettingsRef"
            :model="systemSettings"
            :rules="systemRules"
            label-width="120px"
          >
            <el-form-item label="上传文件大小" prop="uploadMaxSize">
              <el-input-number v-model="systemSettings.uploadMaxSize" :min="1" :max="100" />
              <span class="form-item-tip">单位：MB</span>
            </el-form-item>
            
            <el-form-item label="允许上传类型" prop="uploadAllowedTypes">
              <el-input v-model="systemSettings.uploadAllowedTypes" placeholder="请输入允许上传的文件类型，多个类型用逗号分隔" />
            </el-form-item>
            
            <el-divider content-position="left">邮件设置</el-divider>
            
            <el-form-item label="邮件服务器">
              <el-input v-model="systemSettings.emailHost" placeholder="请输入邮件服务器地址" />
            </el-form-item>
            
            <el-form-item label="邮件端口" prop="emailPort">
              <el-input-number v-model="systemSettings.emailPort" :min="1" :max="65535" />
            </el-form-item>
            
            <el-form-item label="邮件用户名">
              <el-input v-model="systemSettings.emailUsername" placeholder="请输入邮件用户名" />
            </el-form-item>
            
            <el-form-item label="邮件密码">
              <el-input v-model="systemSettings.emailPassword" type="password" placeholder="请输入邮件密码" />
            </el-form-item>
            
            <el-form-item label="发件人邮箱">
              <el-input v-model="systemSettings.emailFrom" placeholder="请输入发件人邮箱" />
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="saveSystemSettings">保存设置</el-button>
              <el-button @click="testEmail">测试邮件发送</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>
    </el-tabs>
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
  color: #303133;
}

.settings-tabs {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.settings-card {
  border: none;
  box-shadow: none;
}

.form-item-tip {
  margin-left: 10px;
  font-size: 12px;
  color: #909399;
}
</style>