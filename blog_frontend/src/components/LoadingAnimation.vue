<script setup lang="ts">
// 导入Vue组合式API
import { ref, onMounted } from "vue";

// 控制加载动画显示状态
const show = ref(true);

// 组件挂载后设置定时器，1.5秒后隐藏加载动画
onMounted(() => {
  setTimeout(() => {
    show.value = false;
  }, 1500);
});
</script>

<template>
  <!-- 加载动画过渡效果 -->
  <transition name="loading-fade">
    <!-- 加载屏幕容器 -->
    <div v-if="show" class="loading-screen">
      <!-- 加载内容容器 -->
      <div class="loading-content">
        <!-- 加载动画器 -->
        <div class="loader">
          <!-- 动画圆圈1 -->
          <div class="circle"></div>
          <!-- 动画圆圈2 -->
          <div class="circle"></div>
          <!-- 动画圆圈3 -->
          <div class="circle"></div>
        </div>
        <!-- 加载文本 -->
        <h2 class="loading-text">加载中...</h2>
      </div>
    </div>
  </transition>
</template>

<style scoped>
/* 加载屏幕容器样式 */
.loading-screen {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-hover) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10000;
}

/* 加载内容容器样式 */
.loading-content {
  text-align: center;
}

/* 加载动画器样式 */
.loader {
  display: flex;
  gap: 12px;
  justify-content: center;
  margin-bottom: 24px;
}

/* 动画圆圈基础样式 */
.circle {
  width: 16px;
  height: 16px;
  background: white;
  border-radius: 50%;
  animation: bounce 1.4s infinite ease-in-out both;
}

/* 第一个圆圈动画延迟 */
.circle:nth-child(1) {
  animation-delay: -0.32s;
}

/* 第二个圆圈动画延迟 */
.circle:nth-child(2) {
  animation-delay: -0.16s;
}

/* 圆圈弹跳动画 */
@keyframes bounce {
  0%,
  80%,
  100% {
    transform: scale(0);
    opacity: 0.5;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

/* 加载文本样式 */
.loading-text {
  color: white;
  font-size: 20px;
  font-weight: 600;
  letter-spacing: 2px;
  animation: pulse 1.5s ease-in-out infinite;
}

/* 文本脉冲动画 */
@keyframes pulse {
  0%,
  100% {
    opacity: 1;
  }
  50% {
    opacity: 0.6;
  }
}

/* 加载动画过渡效果 */
.loading-fade-enter-active,
.loading-fade-leave-active {
  transition: opacity 0.5s ease;
}

/* 加载动画进入和离开状态 */
.loading-fade-enter-from,
.loading-fade-leave-to {
  opacity: 0;
}
</style>
