// 导入Vite配置定义函数
import { defineConfig } from 'vite'

// 导入Vue插件，用于处理Vue单文件组件
import vue from '@vitejs/plugin-vue'

// 导入Node.js路径模块的resolve方法，用于路径解析
import { resolve } from 'path'

// Vite配置文档参考：https://vite.dev/config/
export default defineConfig({
  // 插件配置
  plugins: [
    // Vue插件，处理.vue文件
    vue()
  ],
  
  // 路径别名配置
  resolve: {
    alias: {
      // 设置@别名指向src目录，方便导入文件
      '@': resolve(__dirname, 'src'),
    },
  },
})