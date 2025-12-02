<script setup lang="ts">
import { NSkeleton } from 'naive-ui'
</script>

<template>
  <div class="post-card-skeleton">
    <div class="skeleton-header">
      <div class="skeleton-author">
        <n-skeleton circle width="32px" height="32px" />
        <div class="skeleton-author-info">
          <n-skeleton text style="width: 100px; height: 14px; margin-bottom: 4px" />
          <n-skeleton text style="width: 60px; height: 12px" />
        </div>
      </div>
      <n-skeleton text style="width: 60px; height: 24px; border-radius: 12px" />
    </div>
    
    <n-skeleton text style="width: 70%; height: 24px; margin-bottom: 12px" />
    <n-skeleton text :repeat="2" style="margin-bottom: 20px" />
    
    <div class="skeleton-footer">
      <div class="skeleton-stats">
        <n-skeleton text style="width: 50px; height: 16px" />
        <n-skeleton text style="width: 50px; height: 16px" />
        <n-skeleton text style="width: 50px; height: 16px" />
        <n-skeleton text style="width: 60px; height: 16px" />
      </div>
      <div class="skeleton-tags">
        <n-skeleton text style="width: 60px; height: 24px; border-radius: 12px" />
        <n-skeleton text style="width: 50px; height: 24px; border-radius: 12px" />
      </div>
    </div>
  </div>
</template>

<style scoped>
.post-card-skeleton {
  background-color: var(--card-bg);
  border: 1px solid var(--border-color);
  border-radius: 12px;
  padding: 28px;
}

.skeleton-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.skeleton-author {
  display: flex;
  align-items: center;
  gap: 10px;
}

.skeleton-author-info {
  display: flex;
  flex-direction: column;
}

.skeleton-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid var(--border-color);
}

.skeleton-stats {
  display: flex;
  gap: 20px;
}

.skeleton-tags {
  display: flex;
  gap: 8px;
}
</style>
