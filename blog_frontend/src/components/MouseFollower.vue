<script setup lang="ts">
// 导入Vue组合式API
import { ref, onMounted, onUnmounted } from 'vue'

// 鼠标光标点和轮廓的DOM引用
const cursorDot = ref<HTMLDivElement | null>(null)
const cursorOutline = ref<HTMLDivElement | null>(null)

// 鼠标位置和轮廓位置变量
let mouseX = 0
let mouseY = 0
let outlineX = 0
let outlineY = 0

// 处理鼠标移动事件
const handleMouseMove = (e: MouseEvent) => {
  // 获取鼠标当前位置
  mouseX = e.clientX
  mouseY = e.clientY

  // 更新光标点的位置
  if (cursorDot.value) {
    cursorDot.value.style.left = `${mouseX}px`
    cursorDot.value.style.top = `${mouseY}px`
  }
}

// 动画更新轮廓位置，实现平滑跟随效果
const animateOutline = () => {
  // 使用缓动系数实现平滑跟随
  outlineX += (mouseX - outlineX) * 0.15
  outlineY += (mouseY - outlineY) * 0.15

  // 更新轮廓位置
  if (cursorOutline.value) {
    cursorOutline.value.style.left = `${outlineX}px`
    cursorOutline.value.style.top = `${outlineY}px`
  }

  // 使用requestAnimationFrame实现流畅动画
  requestAnimationFrame(animateOutline)
}

// 组件挂载时添加事件监听和启动动画
onMounted(() => {
  document.addEventListener('mousemove', handleMouseMove)
  animateOutline()
})

// 组件卸载时移除事件监听
onUnmounted(() => {
  document.removeEventListener('mousemove', handleMouseMove)
})
</script>

<template>
  <!-- 鼠标跟随效果容器 -->
  <div class="cursor-effects">
    <!-- 鼠标光标点 -->
    <div ref="cursorDot" class="cursor-dot"></div>
    <!-- 鼠标光标轮廓 -->
    <div ref="cursorOutline" class="cursor-outline"></div>
  </div>
</template>

<style scoped>
/* 鼠标效果容器样式 */
.cursor-effects {
  pointer-events: none;
  position: fixed;
  z-index: 9999;
}

/* 鼠标光标点样式 */
.cursor-dot {
  width: 8px;
  height: 8px;
  background: var(--primary-color);
  border-radius: 50%;
  position: fixed;
  transform: translate(-50%, -50%);
  pointer-events: none;
  z-index: 9999;
  mix-blend-mode: difference;
}

/* 鼠标光标轮廓样式 */
.cursor-outline {
  width: 32px;
  height: 32px;
  border: 2px solid var(--primary-color);
  border-radius: 50%;
  position: fixed;
  transform: translate(-50%, -50%);
  pointer-events: none;
  z-index: 9999;
  transition: width 0.3s ease, height 0.3s ease;
  mix-blend-mode: difference;
}

/* 移动设备上隐藏鼠标跟随效果 */
@media (max-width: 768px) {
  .cursor-effects {
    display: none;
  }
}
</style>