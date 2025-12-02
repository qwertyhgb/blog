<script setup lang="ts">
import { ref, onMounted } from 'vue'

const show = ref(true)

onMounted(() => {
  setTimeout(() => {
    show.value = false
  }, 1500)
})
</script>

<template>
  <transition name="loading-fade">
    <div v-if="show" class="loading-screen">
      <div class="loading-content">
        <div class="loader">
          <div class="circle"></div>
          <div class="circle"></div>
          <div class="circle"></div>
        </div>
        <h2 class="loading-text">加载中...</h2>
      </div>
    </div>
  </transition>
</template>

<style scoped>
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

.loading-content {
  text-align: center;
}

.loader {
  display: flex;
  gap: 12px;
  justify-content: center;
  margin-bottom: 24px;
}

.circle {
  width: 16px;
  height: 16px;
  background: white;
  border-radius: 50%;
  animation: bounce 1.4s infinite ease-in-out both;
}

.circle:nth-child(1) {
  animation-delay: -0.32s;
}

.circle:nth-child(2) {
  animation-delay: -0.16s;
}

@keyframes bounce {
  0%, 80%, 100% {
    transform: scale(0);
    opacity: 0.5;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

.loading-text {
  color: white;
  font-size: 20px;
  font-weight: 600;
  letter-spacing: 2px;
  animation: pulse 1.5s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.6;
  }
}

.loading-fade-enter-active,
.loading-fade-leave-active {
  transition: opacity 0.5s ease;
}

.loading-fade-enter-from,
.loading-fade-leave-to {
  opacity: 0;
}
</style>
