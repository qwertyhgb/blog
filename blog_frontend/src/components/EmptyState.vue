<script setup lang="ts">
import { NEmpty, NButton, NIcon } from 'naive-ui'
import { AddOutline } from '@vicons/ionicons5'

const props = defineProps<{
  title?: string
  description?: string
  showAction?: boolean
  actionText?: string
  actionIcon?: any
}>()

const emit = defineEmits<{
  action: []
}>()
</script>

<template>
  <div class="empty-state-container">
    <n-empty 
      :description="description || '暂无数据'" 
      size="large"
    >
      <template #icon>
        <div class="empty-icon">📭</div>
      </template>
      <template #extra v-if="showAction">
        <n-button type="primary" size="large" @click="emit('action')">
          <template #icon v-if="actionIcon">
            <n-icon :component="actionIcon" />
          </template>
          {{ actionText || '添加' }}
        </n-button>
      </template>
    </n-empty>
  </div>
</template>

<style scoped>
.empty-state-container {
  padding: 80px 20px;
  text-align: center;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 16px;
}
</style>
