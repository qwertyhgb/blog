<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { NIcon } from 'naive-ui'
import { ListOutline } from '@vicons/ionicons5'

interface TocItem {
  id: string
  text: string
  level: number
}

const tocItems = ref<TocItem[]>([])
const activeId = ref('')

const generateToc = () => {
  const article = document.querySelector('.markdown-body')
  if (!article) return

  const headings = article.querySelectorAll('h1, h2, h3')
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

const updateActiveHeading = () => {
  const headings = document.querySelectorAll('.markdown-body h1, .markdown-body h2, .markdown-body h3')
  let currentId = ''

  headings.forEach((heading) => {
    const rect = heading.getBoundingClientRect()
    if (rect.top <= 100) {
      currentId = heading.id
    }
  })

  activeId.value = currentId
}

const scrollToHeading = (id: string) => {
  const element = document.getElementById(id)
  if (element) {
    const top = element.offsetTop - 80
    window.scrollTo({ top, behavior: 'smooth' })
  }
}

onMounted(() => {
  setTimeout(generateToc, 500)
  window.addEventListener('scroll', updateActiveHeading)
})

onUnmounted(() => {
  window.removeEventListener('scroll', updateActiveHeading)
})
</script>

<template>
  <div class="toc-container" v-if="tocItems.length > 0">
    <div class="toc-header">
      <n-icon :component="ListOutline" :size="18" />
      <span>目录</span>
    </div>
    <div class="toc-list">
      <div
        v-for="item in tocItems"
        :key="item.id"
        class="toc-item"
        :class="{ 
          active: activeId === item.id,
          [`level-${item.level}`]: true 
        }"
        @click="scrollToHeading(item.id)"
      >
        {{ item.text }}
      </div>
    </div>
  </div>
</template>

<style scoped>
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

.toc-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.toc-item {
  font-size: 13px;
  color: var(--text-secondary);
  cursor: pointer;
  padding: 6px 8px;
  border-radius: 4px;
  transition: all 0.15s ease;
  line-height: 1.4;
}

.toc-item:hover {
  background-color: var(--bg-hover);
  color: var(--text-color);
}

.toc-item.active {
  background-color: var(--bg-secondary);
  color: var(--primary-color);
  font-weight: 500;
}

.toc-item.level-1 {
  padding-left: 8px;
}

.toc-item.level-2 {
  padding-left: 24px;
}

.toc-item.level-3 {
  padding-left: 40px;
}
</style>
