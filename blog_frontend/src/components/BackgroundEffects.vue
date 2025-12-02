<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue'

const canvas = ref<HTMLCanvasElement | null>(null)
let animationId: number | null = null

interface Particle {
  x: number
  y: number
  vx: number
  vy: number
  size: number
  opacity: number
}

onMounted(() => {
  if (!canvas.value) return
  
  const ctx = canvas.value.getContext('2d')
  if (!ctx) return

  // 设置画布大小
  const resizeCanvas = () => {
    if (!canvas.value) return
    canvas.value.width = window.innerWidth
    canvas.value.height = window.innerHeight
  }
  resizeCanvas()
  window.addEventListener('resize', resizeCanvas)

  // 创建粒子
  const particles: Particle[] = []
  const particleCount = 50

  for (let i = 0; i < particleCount; i++) {
    particles.push({
      x: Math.random() * canvas.value.width,
      y: Math.random() * canvas.value.height,
      vx: (Math.random() - 0.5) * 0.5,
      vy: (Math.random() - 0.5) * 0.5,
      size: Math.random() * 2 + 1,
      opacity: Math.random() * 0.5 + 0.2
    })
  }

  // 动画循环
  const animate = () => {
    if (!canvas.value || !ctx) return

    ctx.clearRect(0, 0, canvas.value.width, canvas.value.height)

    // 更新和绘制粒子
    particles.forEach((particle, i) => {
      particle.x += particle.vx
      particle.y += particle.vy

      // 边界检测
      if (particle.x < 0 || particle.x > canvas.value!.width) particle.vx *= -1
      if (particle.y < 0 || particle.y > canvas.value!.height) particle.vy *= -1

      // 绘制粒子
      ctx.beginPath()
      ctx.arc(particle.x, particle.y, particle.size, 0, Math.PI * 2)
      ctx.fillStyle = `rgba(74, 158, 255, ${particle.opacity})`
      ctx.fill()

      // 连接附近的粒子
      for (let j = i + 1; j < particles.length; j++) {
        const dx = particles[j].x - particle.x
        const dy = particles[j].y - particle.y
        const distance = Math.sqrt(dx * dx + dy * dy)

        if (distance < 150) {
          ctx.beginPath()
          ctx.moveTo(particle.x, particle.y)
          ctx.lineTo(particles[j].x, particles[j].y)
          ctx.strokeStyle = `rgba(74, 158, 255, ${0.1 * (1 - distance / 150)})`
          ctx.lineWidth = 0.5
          ctx.stroke()
        }
      }
    })

    animationId = requestAnimationFrame(animate)
  }

  animate()

  onUnmounted(() => {
    window.removeEventListener('resize', resizeCanvas)
    if (animationId) cancelAnimationFrame(animationId)
  })
})
</script>

<template>
  <canvas ref="canvas" class="background-canvas"></canvas>
</template>

<style scoped>
.background-canvas {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 0;
  opacity: 0.4;
}
</style>
