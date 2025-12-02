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
  <div class="reading-progress">
    <div class="progress-bar" :style="{ width: `${progress}%` }"></div>
  </div>
</template>

<style scoped>
.reading-progress {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background-color: var(--bg-secondary);
  z-index: 1000;
}

.progress-bar {
  height: 100%;
  background: linear-gradient(90deg, var(--primary-color), var(--primary-hover));
  transition: width 0.1s ease;
}
</style>
