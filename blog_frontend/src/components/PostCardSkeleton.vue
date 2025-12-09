<script setup lang="ts">
// 导入Naive UI的骨架屏组件
import { NSkeleton } from 'naive-ui'
</script>

<template>
  <!-- 文章卡片骨架屏容器 -->
  <div class="post-card-skeleton">
    <!-- 骨架屏头部：包含作者信息和状态 -->
    <div class="skeleton-header">
      <!-- 作者信息骨架屏 -->
      <div class="skeleton-author">
        <!-- 作者头像骨架屏 -->
        <n-skeleton circle width="32px" height="32px" />
        <!-- 作者信息文本骨架屏 -->
        <div class="skeleton-author-info">
          <!-- 作者名骨架屏 -->
          <n-skeleton text style="width: 100px; height: 14px; margin-bottom: 4px" />
          <!-- 发布时间骨架屏 -->
          <n-skeleton text style="width: 60px; height: 12px" />
        </div>
      </div>
      <!-- 文章状态标签骨架屏 -->
      <n-skeleton text style="width: 60px; height: 24px; border-radius: 12px" />
    </div>
    
    <!-- 文章标题骨架屏 -->
    <n-skeleton text style="width: 70%; height: 24px; margin-bottom: 12px" />
    <!-- 文章摘要骨架屏 -->
    <n-skeleton text :repeat="2" style="margin-bottom: 20px" />
    
    <!-- 骨架屏底部：包含统计信息和标签 -->
    <div class="skeleton-footer">
      <!-- 统计信息骨架屏 -->
      <div class="skeleton-stats">
        <!-- 浏览次数骨架屏 -->
        <n-skeleton text style="width: 50px; height: 16px" />
        <!-- 点赞次数骨架屏 -->
        <n-skeleton text style="width: 50px; height: 16px" />
        <!-- 评论次数骨架屏 -->
        <n-skeleton text style="width: 50px; height: 16px" />
        <!-- 阅读时间骨架屏 -->
        <n-skeleton text style="width: 60px; height: 16px" />
      </div>
      <!-- 标签骨架屏 -->
      <div class="skeleton-tags">
        <!-- 标签1骨架屏 -->
        <n-skeleton text style="width: 60px; height: 24px; border-radius: 12px" />
        <!-- 标签2骨架屏 -->
        <n-skeleton text style="width: 50px; height: 24px; border-radius: 12px" />
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 文章卡片骨架屏容器样式 */
.post-card-skeleton {
  background-color: var(--card-bg);
  border: 1px solid var(--border-color);
  border-radius: 12px;
  padding: 28px;
}

/* 骨架屏头部样式 */
.skeleton-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

/* 作者信息骨架屏样式 */
.skeleton-author {
  display: flex;
  align-items: center;
  gap: 10px;
}

/* 作者信息文本骨架屏样式 */
.skeleton-author-info {
  display: flex;
  flex-direction: column;
}

/* 骨架屏底部样式 */
.skeleton-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid var(--border-color);
}

/* 统计信息骨架屏样式 */
.skeleton-stats {
  display: flex;
  gap: 20px;
}

/* 标签骨架屏样式 */
.skeleton-tags {
  display: flex;
  gap: 8px;
}
</style>