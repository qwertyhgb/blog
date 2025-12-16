<script setup lang="ts">
// 导入Naive UI组件
import { NEmpty, NButton, NIcon } from "naive-ui";
// 导入图标组件
import { AddOutline } from "@vicons/ionicons5";

// 定义组件属性
const props = defineProps<{
  title?: string; // 标题
  description?: string; // 描述文本
  showAction?: boolean; // 是否显示操作按钮
  actionText?: string; // 操作按钮文本
  actionIcon?: any; // 操作按钮图标
}>();

// 定义组件事件
const emit = defineEmits<{
  action: []; // 操作按钮点击事件
}>();
</script>

<template>
  <!-- 空状态容器 -->
  <div class="empty-state-container">
    <!-- Naive UI空状态组件 -->
    <n-empty :description="description || '暂无数据'" size="large">
      <!-- 自定义图标插槽 -->
      <template #icon>
        <div class="empty-icon">📭</div>
      </template>
      <!-- 操作按钮插槽 -->
      <template #extra v-if="showAction">
        <n-button type="primary" size="large" @click="emit('action')">
          <!-- 按钮图标插槽 -->
          <template #icon v-if="actionIcon">
            <n-icon :component="actionIcon" />
          </template>
          {{ actionText || "添加" }}
        </n-button>
      </template>
    </n-empty>
  </div>
</template>

<style scoped>
/* 空状态容器样式 */
.empty-state-container {
  padding: 80px 20px;
  text-align: center;
}

/* 空状态图标样式 */
.empty-icon {
  font-size: 64px;
  margin-bottom: 16px;
}
</style>
