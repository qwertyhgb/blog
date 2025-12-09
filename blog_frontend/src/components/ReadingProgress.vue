<script setup lang="ts">
// 导入Vue组合式API
import { ref, onMounted, onUnmounted } from 'vue'

// 阅读进度百分比
const progress = ref(0)

// 更新阅读进度
const updateProgress = () => {
  // 获取当前滚动位置
  const scrollTop = window.pageYOffset || document.documentElement.scrollTop
  // 计算文档总高度减去视口高度
  const scrollHeight = document.documentElement.scrollHeight - document.documentElement.clientHeight
  // 计算进度百分比
  progress.value = (scrollTop / scrollHeight) * 100
}

// 组件挂载时添加滚动事件监听
onMounted(() => {
  window.addEventListener('scroll', updateProgress)
  updateProgress()
})

// 组件卸载时移除滚动事件监听
onUnmounted(() => {
  window.removeEventListener('scroll', updateProgress)
})
</script>

<template>
  <!-- 阅读进度条容器 -->
  <div class="reading-progress">
    <!-- 进度条 -->
    <div class="progress-bar" :style="{ width: `${progress}%` }"></div>
  </div>
</template>

<style scoped>
/* 阅读进度条容器样式 */
.reading-progress {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background-color: var(--bg-secondary);
  z-index: 1000;
}

/* 进度条样式 */
.progress-bar {
  height: 100%;
  background: linear-gradient(90deg, var(--primary-color), var(--primary-hover));
  transition: width 0.1s ease;
}
</style>