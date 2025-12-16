<script setup lang="ts">
import { onMounted, computed } from "vue";
import { useRouter } from "vue-router";
import { useCategoryStore } from "@/stores/category";
import { NCard, NGrid, NGridItem, NIcon, NSkeleton, NEmpty, NBadge, NTag } from "naive-ui";
import {
  FolderOutline,
  ChevronForwardOutline,
  GridOutline,
  ListOutline,
  DocumentTextOutline,
} from "@vicons/ionicons5";

const router = useRouter();
const categoryStore = useCategoryStore();

const loading = computed(() => categoryStore.loading);
const categories = computed(() => categoryStore.categories);

// 为每个分类分配颜色
const categoryColors = [
  { bg: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)", icon: "#667eea" },
  { bg: "linear-gradient(135deg, #f093fb 0%, #f5576c 100%)", icon: "#f093fb" },
  { bg: "linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)", icon: "#4facfe" },
  { bg: "linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)", icon: "#43e97b" },
  { bg: "linear-gradient(135deg, #fa709a 0%, #fee140 100%)", icon: "#fa709a" },
  { bg: "linear-gradient(135deg, #30cfd0 0%, #330867 100%)", icon: "#30cfd0" },
  { bg: "linear-gradient(135deg, #a8edea 0%, #fed6e3 100%)", icon: "#a8edea" },
  { bg: "linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%)", icon: "#ff9a9e" },
];

const getCategoryColor = (index: number) => {
  return categoryColors[index % categoryColors.length];
};

const goToCategory = (id: number) => {
  router.push(`/category/${id}`);
};

onMounted(() => {
  categoryStore.fetchCategories();
});
</script>

<template>
  <div class="categories-page">
    <!-- Hero Section -->
    <div class="hero-section">
      <div class="hero-content">
        <div class="hero-icon">
          <n-icon :component="GridOutline" :size="48" />
        </div>
        <h1 class="hero-title">文章分类</h1>
        <p class="hero-subtitle">探索 {{ categories.length }} 个精彩分类</p>
      </div>
    </div>

    <!-- Loading State -->
    <template v-if="loading">
      <n-grid :cols="3" :x-gap="20" :y-gap="20" responsive="screen">
        <n-grid-item v-for="i in 6" :key="i">
          <div class="category-card skeleton">
            <n-skeleton circle width="64px" height="64px" style="margin-bottom: 16px" />
            <n-skeleton text style="width: 60%; height: 20px; margin-bottom: 8px" />
            <n-skeleton text :repeat="2" style="margin-bottom: 12px" />
            <n-skeleton text style="width: 40%; height: 24px; border-radius: 12px" />
          </div>
        </n-grid-item>
      </n-grid>
    </template>

    <!-- Empty State -->
    <template v-else-if="categories.length === 0">
      <div class="empty-state">
        <n-empty description="暂无分类" size="large">
          <template #icon>
            <div class="empty-icon">📁</div>
          </template>
        </n-empty>
      </div>
    </template>

    <!-- Categories Grid -->
    <template v-else>
      <n-grid :cols="3" :x-gap="20" :y-gap="20" responsive="screen">
        <n-grid-item v-for="(category, index) in categories" :key="category.id">
          <div class="category-card" @click="goToCategory(category.id)">
            <div class="category-card-bg" :style="{ background: getCategoryColor(index).bg }"></div>

            <div class="category-icon-wrapper">
              <div class="category-icon" :style="{ background: getCategoryColor(index).bg }">
                <n-icon :component="FolderOutline" :size="32" />
              </div>
            </div>

            <h3 class="category-name">{{ category.name }}</h3>
            <p class="category-desc">{{ category.description || "暂无描述" }}</p>

            <div class="category-footer">
              <n-tag size="small" :bordered="false" round>
                <template #icon>
                  <n-icon :component="DocumentTextOutline" :size="14" />
                </template>
                {{ category.postCount || 0 }} 篇文章
              </n-tag>
              <div class="category-arrow">
                <n-icon :component="ChevronForwardOutline" :size="18" />
              </div>
            </div>
          </div>
        </n-grid-item>
      </n-grid>
    </template>
  </div>
</template>

<style scoped>
.categories-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 32px 60px;
  min-height: 100vh;
}

/* Hero Section */
.hero-section {
  text-align: center;
  padding: 60px 0;
  margin-bottom: 48px;
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
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-hover) 100%);
  border-radius: 20px;
  color: white;
  margin-bottom: 24px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%,
  100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-10px);
  }
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

/* Category Card */
.category-card {
  background: var(--card-bg);
  border: 1px solid var(--border-color);
  border-radius: 16px;
  padding: 32px 24px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.category-card-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  opacity: 0.8;
  transition: height 0.3s ease;
}

.category-card:hover .category-card-bg {
  height: 100%;
  opacity: 0.1;
}

.category-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 32px var(--shadow-md);
  border-color: transparent;
}

.category-card.skeleton {
  cursor: default;
}

.category-card.skeleton:hover {
  transform: none;
  box-shadow: none;
}

.category-icon-wrapper {
  margin-bottom: 20px;
}

.category-icon {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.category-card:hover .category-icon {
  transform: scale(1.1) rotate(5deg);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.25);
}

.category-name {
  font-size: 20px;
  font-weight: 700;
  color: var(--text-color);
  margin: 0 0 12px 0;
  line-height: 1.3;
}

.category-desc {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.6;
  margin: 0 0 20px 0;
  flex: 1;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.category-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid var(--border-color);
}

.category-arrow {
  color: var(--text-tertiary);
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: var(--bg-secondary);
}

.category-card:hover .category-arrow {
  transform: translateX(4px);
  color: white;
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-hover) 100%);
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
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.category-card {
  animation: fadeInUp 0.5s ease-out;
}

.category-card:nth-child(1) {
  animation-delay: 0.05s;
}
.category-card:nth-child(2) {
  animation-delay: 0.1s;
}
.category-card:nth-child(3) {
  animation-delay: 0.15s;
}
.category-card:nth-child(4) {
  animation-delay: 0.2s;
}
.category-card:nth-child(5) {
  animation-delay: 0.25s;
}
.category-card:nth-child(6) {
  animation-delay: 0.3s;
}

/* Responsive */
@media (max-width: 1024px) {
  .hero-title {
    font-size: 36px;
  }
}

@media (max-width: 768px) {
  .categories-page {
    padding: 0 16px 40px;
  }

  .hero-section {
    padding: 40px 0;
    margin-bottom: 32px;
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

  .category-card {
    padding: 24px 20px;
  }

  .category-icon {
    width: 56px;
    height: 56px;
  }

  .category-name {
    font-size: 18px;
  }

  .category-desc {
    font-size: 13px;
  }
}
</style>
