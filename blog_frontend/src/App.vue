<script setup lang="ts">
import { useUserStore } from '@/stores/user'
import { onMounted } from 'vue'
import Header from '@/components/Header.vue'
import Footer from '@/components/Footer.vue'

const userStore = useUserStore()

onMounted(() => {
  // 如果有token，则获取用户信息
  if (userStore.token) {
    userStore.getCurrentUser()
  }
})
</script>

<template>
  <div class="app-container">
    <Header />
    <main class="main-content">
      <router-view />
    </main>
    <Footer />
  </div>
</template>

<style scoped>
.app-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-content {
  flex: 1;
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
}
</style>