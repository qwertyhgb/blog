<script setup lang="ts">
import { useRouter } from 'vue-router'
import { DocumentRemove } from '@element-plus/icons-vue'

const router = useRouter()

const goHome = () => {
  router.push('/')
}
</script>

<template>
  <div class="not-found">
    <div class="content">
      <el-result
        icon="error"
        title="404"
        sub-title="抱歉，您访问的页面不存在"
      >
        <template #icon>
          <el-icon :size="100" color="#409EFF">
            <DocumentRemove />
          </el-icon>
        </template>
        <template #extra>
          <el-button type="primary" @click="goHome">返回首页</el-button>
        </template>
      </el-result>
    </div>
  </div>
</template>

<style scoped>
.not-found {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 60vh;
}

.content {
  text-align: center;
}
</style>