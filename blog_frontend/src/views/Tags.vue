<script setup lang="ts">
import { onMounted, computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useTagStore } from '@/stores/category'
import { 
  NCard, 
  NSpace, 
  NTag, 
  NIcon, 
  NSkeleton, 
  NEmpty,
  NInput,
  NGrid,
  NGridItem
} from 'naive-ui'
import { 
  PricetagOutline, 
  PricetagsOutline,
  SearchOutline,
  DocumentTextOutline
} from '@vicons/ionicons5'

const router = useRouter()
const tagStore = useTagStore()

const loading = computed(() => tagStore.loading)
const tags = computed(() => tagStore.tags)
const searchKeyword = ref('')

// 标签颜色方案
const tagColors = [
  { bg: 'rgba(102, 126, 234, 0.15)', border: 'rgba(102, 126, 234, 0.3)', text: '#667eea' },
  { bg: 'rgba(240, 147, 251, 0.15)', border: 'rgba(240, 147, 251, 0.3)', text: '#f093fb' },
  { bg: 'rgba(79, 172, 254, 0.15)', border: 'rgba(79, 172, 254, 0.3)', text: '#4facfe' },
  { bg: 'rgba(67, 233, 123, 0.15)', border: 'rgba(67, 233, 123, 0.3)', text: '#43e97b' },
  { bg: 'rgba(250, 112, 154, 0.15)', border: 'rgba(250, 112, 154, 0.3)', text: '#fa709a' },
  { bg: 'rgba(48, 207, 208, 0.15)', border: 'rgba(48, 207, 208, 0.3)', text: '#30cfd0' },
  { bg: 'rgba(168, 237, 234, 0.15)', border: 'rgba(168, 237, 234, 0.3)', text: '#a8edea' },
  { bg: 'rgba(255, 154, 158, 0.15)', border: 'rgba(255, 154, 158, 0.3)', text: '#ff9a9e' },
]

const getTagColor = (index: number) => {
  return tagColors[index % tagColors.length]
}

// 根据标签名称生成一致的颜色索引
const getTagColorIndex = (tagName: string) => {
  let hash = 0
  for (let i = 0; i < tagName.length; i++) {
    hash = tagName.charCodeAt(i) + ((hash << 5) - hash)
  }
  return Math.abs(hash) % tagColors.length
}

// 筛选标签
const filteredTags = computed(() => {
  if (!searchKeyword.value) return tags.value
  return tags.value.filter(tag => 
    tag.name.toLowerCase().includes(searchKeyword.value.toLowerCase())
  )
})

const goToTag = (id: number) => {
  router.push(`/tag/${id}`)
}

onMounted(() => {
  tagStore.fetchTags()
})
</script>

<template>
  <div class="tags-page">
    <!-- Hero Section -->
    <div class="hero-section">
      <div class="hero-content">
        <div class="hero-icon">
          <n-icon :component="PricetagsOutline" :size="48" />
        </div>
        <h1 class="hero-title">文章标签</h1>
        <p class="hero-subtitle">探索 {{ tags.length }} 个热门标签</p>
      </div>
    </div>

    <!-- Search Bar -->
    <div class="search-section">
      <n-input
        v-model:value="searchKeyword"
        size="large"
        placeholder="搜索标签..."
        clearable
        round
      >
        <template #prefix>
          <n-icon :component="SearchOutline" :size="18" />
        </template>
      </n-input>
    </div>

    <!-- Loading State -->
    <template v-if="loading">
      <div class="tags-grid">
        <div v-for="i in 12" :key="i" class="tag-card skeleton">
          <n-skeleton circle width="48px" height="48px" style="margin-bottom: 12px" />
          <n-skeleton text style="width: 60%; height: 18px; margin-bottom: 8px" />
          <n-skeleton text style="width: 40%; height: 14px" />
        </div>
      </div>
    </template>

    <!-- Empty State -->
    <template v-else-if="filteredTags.length === 0">
      <div class="empty-state">
        <n-empty :description="searchKeyword ? '未找到匹配的标签' : '暂无标签'" size="large">
          <template #icon>
            <div class="empty-icon">🏷️</div>
          </template>
        </n-empty>
      </div>
    </template>

    <!-- Tags Grid -->
    <template v-else>
      <div class="tags-grid">
        <div 
          v-for="(tag, index) in filteredTags" 
          :key="tag.id"
          class="tag-card"
          @click="goToTag(tag.id)"
          :style="{
            '--tag-bg': getTagColor(getTagColorIndex(tag.name)).bg,
            '--tag-border': getTagColor(getTagColorIndex(tag.name)).border,
            '--tag-text': getTagColor(getTagColorIndex(tag.name)).text
          }"
        >
          <div class="tag-icon">
            <n-icon :component="PricetagOutline" :size="24" />
          </div>
          <h3 class="tag-name">{{ tag.name }}</h3>
          <div class="tag-count">
            <n-icon :component="DocumentTextOutline" :size="14" />
            <span>{{ tag.postCount || 0 }} 篇</span>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<style scoped>
.tags-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 32px 60px;
  min-height: 100vh;
}

/* Hero Section */
.hero-section {
  text-align: center;
  padding: 60px 0 40px;
  position: relative;
}

.hero-content {
  max-width: 600px;
  margin: 0 auto;
}

.hero-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
  border-radius: 20px;
  color: white;
  margin-bottom: 24px;
  box-shadow: 0 8px 24px rgba(250, 112, 154, 0.3);
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0px) rotate(0deg); }
  50% { transform: translateY(-10px) rotate(5deg); }
}

.hero-title {
  font-size: 42px;
  font-weight: 800;
  color: var(--text-color);
  margin-bottom: 12px;
  letter-spacing: -1px;
}

.hero-subtitle {
  font-size: 16px;
  color: var(--text-secondary);
  margin: 0;
}

/* Search Section */
.search-section {
  max-width: 500px;
  margin: 0 auto 48px;
}

/* Tags Grid */
.tags-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  gap: 20px;
}

.tag-card {
  background: var(--card-bg);
  border: 2px solid var(--tag-border);
  border-radius: 16px;
  padding: 24px 20px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  text-align: center;
  position: relative;
  overflow: hidden;
}

.tag-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: var(--tag-bg);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.tag-card:hover::before {
  opacity: 1;
}

.tag-card:hover {
  transform: translateY(-6px) scale(1.02);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15);
  border-color: var(--tag-text);
}

.tag-card.skeleton {
  cursor: default;
  border-color: var(--border-color);
}

.tag-card.skeleton:hover {
  transform: none;
  box-shadow: none;
}

.tag-card.skeleton::before {
  display: none;
}

.tag-icon {
  width: 48px;
  height: 48px;
  margin: 0 auto 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--tag-bg);
  border-radius: 12px;
  color: var(--tag-text);
  transition: all 0.3s ease;
  position: relative;
  z-index: 1;
}

.tag-card:hover .tag-icon {
  transform: scale(1.15) rotate(10deg);
  background: var(--tag-text);
  color: white;
}

.tag-name {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-color);
  margin: 0 0 8px 0;
  position: relative;
  z-index: 1;
  transition: color 0.3s ease;
}

.tag-card:hover .tag-name {
  color: var(--tag-text);
}

.tag-count {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  font-size: 13px;
  color: var(--text-secondary);
  position: relative;
  z-index: 1;
  transition: color 0.3s ease;
}

.tag-card:hover .tag-count {
  color: var(--tag-text);
}

/* Empty State */
.empty-state {
  padding: 100px 20px;
  text-align: center;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 16px;
}

/* Animations */
@keyframes fadeInScale {
  from {
    opacity: 0;
    transform: scale(0.9);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.tag-card {
  animation: fadeInScale 0.4s ease-out;
}

.tag-card:nth-child(1) { animation-delay: 0.03s; }
.tag-card:nth-child(2) { animation-delay: 0.06s; }
.tag-card:nth-child(3) { animation-delay: 0.09s; }
.tag-card:nth-child(4) { animation-delay: 0.12s; }
.tag-card:nth-child(5) { animation-delay: 0.15s; }
.tag-card:nth-child(6) { animation-delay: 0.18s; }
.tag-card:nth-child(7) { animation-delay: 0.21s; }
.tag-card:nth-child(8) { animation-delay: 0.24s; }
.tag-card:nth-child(9) { animation-delay: 0.27s; }
.tag-card:nth-child(10) { animation-delay: 0.3s; }
.tag-card:nth-child(11) { animation-delay: 0.33s; }
.tag-card:nth-child(12) { animation-delay: 0.36s; }

/* Responsive */
@media (max-width: 1024px) {
  .hero-title {
    font-size: 36px;
  }

  .tags-grid {
    grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  }
}

@media (max-width: 768px) {
  .tags-page {
    padding: 0 16px 40px;
  }

  .hero-section {
    padding: 40px 0 32px;
  }

  .hero-icon {
    width: 64px;
    height: 64px;
  }

  .hero-title {
    font-size: 28px;
  }

  .hero-subtitle {
    font-size: 14px;
  }

  .search-section {
    margin-bottom: 32px;
  }

  .tags-grid {
    grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
    gap: 16px;
  }

  .tag-card {
    padding: 20px 16px;
  }

  .tag-icon {
    width: 40px;
    height: 40px;
  }

  .tag-name {
    font-size: 14px;
  }

  .tag-count {
    font-size: 12px;
  }
}
</style>
