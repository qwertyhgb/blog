<script setup lang="ts">
import { NBackTop, NIcon } from 'naive-ui'
import { ArrowUpOutline } from '@vicons/ionicons5'
</script>

<template>
  <n-back-top :right="40" :bottom="80">
    <div class="back-top-button">
      <n-icon :component="ArrowUpOutline" :size="20" />
    </div>
  </n-back-top>
</template>

<style scoped>
.back-top-button {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--card-bg);
  border: 1px solid var(--border-color);
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 2px 8px var(--shadow-sm);
}

.back-top-button:hover {
  background-color: var(--primary-color);
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px var(--shadow-md);
}
</style>
