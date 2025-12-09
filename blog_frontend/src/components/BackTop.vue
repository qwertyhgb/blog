<script setup lang="ts">
// 导入Naive UI组件
import { NBackTop, NIcon } from 'naive-ui'
// 导入向上箭头图标
import { ArrowUpOutline } from '@vicons/ionicons5'
</script>

<template>
  <!-- Naive UI回到顶部组件 -->
  <n-back-top :right="40" :bottom="80">
    <!-- 自定义回到顶部按钮 -->
    <div class="back-top-button">
      <!-- 向上箭头图标 -->
      <n-icon :component="ArrowUpOutline" :size="20" />
    </div>
  </n-back-top>
</template>

<style scoped>
/* 回到顶部按钮样式 */
.back-top-button {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--card-bg);
  border: 1px solid var(--border-color);
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 2px 8px var(--shadow-sm);
}

/* 回到顶部按钮悬停效果 */
.back-top-button:hover {
  background-color: var(--primary-color);
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px var(--shadow-md);
}
</style>