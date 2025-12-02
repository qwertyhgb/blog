<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'

const progress = ref(0)

const updateProgress = () => {
  const scrollTop = window.pageYOffset || document.documentElement.scrollTop
  const scrollHeight = document.documentElement.scrollHeight - document.documentElement.clientHeight
  progress.value = (scrollTop / scrollHeight) * 100
}

onMounted(() => {
  window.addEventListener('scroll', updateProgress)
  updateProgress()
})

onUnmounted(() => {
  window.removeEventListener('scroll', updateProgress)
})
</script>

<template>
  <div class="scroll-progress">
    <div class="progress-bar" :style="{ width: `${progress}%` }"></div>
  </div>
</template>

<style scoped>
.scroll-progress {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: rgba(0, 0, 0, 0.1);
  z-index: 1000;
}

html.dark .scroll-progress {
  background: rgba(255, 255, 255, 0.1);
}

.progress-bar {
  height: 100%;
  background: linear-gradient(90deg, var(--primary-color) 0%, var(--primary-hover) 100%);
  transition: width 0.1s ease;
  box-shadow: 0 0 10px var(--primary-color);
}
</style>
