<script setup lang="ts">
// 导入Vue组合式API
import { onMounted, onUnmounted, ref } from 'vue'

// Canvas元素引用和动画ID
const canvas = ref<HTMLCanvasElement | null>(null)
let animationId: number | null = null

// 粒子接口定义
interface Particle {
  x: number // X坐标
  y: number // Y坐标
  vx: number // X方向速度
  vy: number // Y方向速度
  size: number // 粒子大小
  opacity: number // 透明度
}

onMounted(() => {
  // 检查Canvas元素是否存在
  if (!canvas.value) return
  
  // 获取2D绘图上下文
  const ctx = canvas.value.getContext('2d')
  if (!ctx) return

  // 设置画布大小函数
  const resizeCanvas = () => {
    if (!canvas.value) return
    canvas.value.width = window.innerWidth
    canvas.value.height = window.innerHeight
  }
  
  // 初始化画布大小并添加窗口大小变化监听
  resizeCanvas()
  window.addEventListener('resize', resizeCanvas)

  // 创建粒子数组
  const particles: Particle[] = []
  const particleCount = 50 // 粒子数量

  // 初始化粒子
  for (let i = 0; i < particleCount; i++) {
    particles.push({
      x: Math.random() * canvas.value.width,
      y: Math.random() * canvas.value.height,
      vx: (Math.random() - 0.5) * 0.5, // 随机水平速度
      vy: (Math.random() - 0.5) * 0.5, // 随机垂直速度
      size: Math.random() * 2 + 1, // 随机大小
      opacity: Math.random() * 0.5 + 0.2 // 随机透明度
    })
  }

  // 动画循环函数
  const animate = () => {
    // 检查Canvas和上下文是否存在
    if (!canvas.value || !ctx) return

    // 清除画布
    ctx.clearRect(0, 0, canvas.value.width, canvas.value.height)

    // 更新和绘制粒子
    particles.forEach((particle, i) => {
      // 更新粒子位置
      particle.x += particle.vx
      particle.y += particle.vy

      // 边界碰撞检测
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

        // 如果距离小于阈值，则绘制连线
        if (distance < 150) {
          ctx.beginPath()
          ctx.moveTo(particle.x, particle.y)
          ctx.lineTo(particles[j].x, particles[j].y)
          // 根据距离调整线条透明度
          ctx.strokeStyle = `rgba(74, 158, 255, ${0.1 * (1 - distance / 150)})`
          ctx.lineWidth = 0.5
          ctx.stroke()
        }
      }
    })

    // 请求下一帧动画
    animationId = requestAnimationFrame(animate)
  }

  // 启动动画
  animate()

  // 组件卸载时清理资源
  onUnmounted(() => {
    window.removeEventListener('resize', resizeCanvas)
    if (animationId) cancelAnimationFrame(animationId)
  })
})
</script>

<template>
  <!-- 背景画布 -->
  <canvas ref="canvas" class="background-canvas"></canvas>
</template>

<style scoped>
/* 背景画布样式 */
.background-canvas {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none; /* 禁止鼠标事件，不影响页面交互 */
  z-index: 0; /* 置于最底层 */
  opacity: 0.4; /* 设置透明度 */
}
</style>