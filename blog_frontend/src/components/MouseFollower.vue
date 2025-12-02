<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'

const cursorDot = ref<HTMLDivElement | null>(null)
const cursorOutline = ref<HTMLDivElement | null>(null)

let mouseX = 0
let mouseY = 0
let outlineX = 0
let outlineY = 0

const handleMouseMove = (e: MouseEvent) => {
  mouseX = e.clientX
  mouseY = e.clientY

  if (cursorDot.value) {
    cursorDot.value.style.left = `${mouseX}px`
    cursorDot.value.style.top = `${mouseY}px`
  }
}

const animateOutline = () => {
  outlineX += (mouseX - outlineX) * 0.15
  outlineY += (mouseY - outlineY) * 0.15

  if (cursorOutline.value) {
    cursorOutline.value.style.left = `${outlineX}px`
    cursorOutline.value.style.top = `${outlineY}px`
  }

  requestAnimationFrame(animateOutline)
}

onMounted(() => {
  document.addEventListener('mousemove', handleMouseMove)
  animateOutline()
})

onUnmounted(() => {
  document.removeEventListener('mousemove', handleMouseMove)
})
</script>

<template>
  <div class="cursor-effects">
    <div ref="cursorDot" class="cursor-dot"></div>
    <div ref="cursorOutline" class="cursor-outline"></div>
  </div>
</template>

<style scoped>
.cursor-effects {
  pointer-events: none;
  position: fixed;
  z-index: 9999;
}

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

@media (max-width: 768px) {
  .cursor-effects {
    display: none;
  }
}
</style>
