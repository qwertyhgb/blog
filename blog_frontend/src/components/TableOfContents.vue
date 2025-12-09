<script setup lang="ts">
// 导入Vue组合式API
import { ref, onMounted, onUnmounted } from 'vue'
// 导入Naive UI组件
import { NIcon } from 'naive-ui'
// 导入图标组件
import { ListOutline } from '@vicons/ionicons5'

// 目录项接口定义
interface TocItem {
  id: string // 标题ID
  text: string // 标题文本
  level: number // 标题级别
}

// 目录项列表和当前激活的标题ID
const tocItems = ref<TocItem[]>([])
const activeId = ref('')

// 生成目录
const generateToc = () => {
  // 获取文章内容容器
  const article = document.querySelector('.markdown-body')
  if (!article) return

  // 获取所有标题元素
  const headings = article.querySelectorAll('h1, h2, h3')
  // 将标题转换为目录项
  tocItems.value = Array.from(headings).map((heading, index) => {
    const id = `heading-${index}`
    heading.id = id
    return {
      id,
      text: heading.textContent || '',
      level: parseInt(heading.tagName.substring(1))
    }
  })
}

// 更新当前激活的标题
const updateActiveHeading = () => {
  // 获取所有标题元素
  const headings = document.querySelectorAll('.markdown-body h1, .markdown-body h2, .markdown-body h3')
  let currentId = ''

  // 遍历标题，找到当前视口中的标题
  headings.forEach((heading) => {
    const rect = heading.getBoundingClientRect()
    if (rect.top <= 100) {
      currentId = heading.id
    }
  })

  activeId.value = currentId
}

// 滚动到指定标题
const scrollToHeading = (id: string) => {
  const element = document.getElementById(id)
  if (element) {
    // 计算滚动位置，考虑固定头部的高度
    const top = element.offsetTop - 80
    window.scrollTo({ top, behavior: 'smooth' })
  }
}

// 组件挂载时初始化目录和添加滚动监听
onMounted(() => {
  // 延迟生成目录，确保DOM已渲染
  setTimeout(generateToc, 500)
  window.addEventListener('scroll', updateActiveHeading)
})

// 组件卸载时移除滚动监听
onUnmounted(() => {
  window.removeEventListener('scroll', updateActiveHeading)
})
</script>

<template>
  <!-- 目录容器，当有目录项时显示 -->
  <div class="toc-container" v-if="tocItems.length > 0">
    <!-- 目录头部 -->
    <div class="toc-header">
      <!-- 目录图标 -->
      <n-icon :component="ListOutline" :size="18" />
      <span>目录</span>
    </div>
    <!-- 目录列表 -->
    <div class="toc-list">
      <!-- 目录项 -->
      <div
        v-for="item in tocItems"
        :key="item.id"
        class="toc-item"
        :class="{ 
          active: activeId === item.id, // 当前激活状态
          [`level-${item.level}`]: true // 根据标题级别设置样式
        }"
        @click="scrollToHeading(item.id)"
      >
        {{ item.text }}
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 目录容器样式 */
.toc-container {
  position: sticky;
  top: 100px;
  background-color: var(--card-bg);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  padding: 16px;
  max-height: calc(100vh - 200px);
  overflow-y: auto;
}

/* 目录头部样式 */
.toc-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 600;
  color: var(--text-color);
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--border-color);
}

/* 目录列表样式 */
.toc-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

/* 目录项基础样式 */
.toc-item {
  font-size: 13px;
  color: var(--text-secondary);
  cursor: pointer;
  padding: 6px 8px;
  border-radius: 4px;
  transition: all 0.15s ease;
  line-height: 1.4;
}

/* 目录项悬停效果 */
.toc-item:hover {
  background-color: var(--bg-hover);
  color: var(--text-color);
}

/* 目录项激活状态样式 */
.toc-item.active {
  background-color: var(--bg-secondary);
  color: var(--primary-color);
  font-weight: 500;
}

/* 一级标题样式 */
.toc-item.level-1 {
  padding-left: 8px;
}

/* 二级标题样式 */
.toc-item.level-2 {
  padding-left: 24px;
}

/* 三级标题样式 */
.toc-item.level-3 {
  padding-left: 40px;
}
</style>