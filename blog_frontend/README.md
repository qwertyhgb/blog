# 博客前端

基于 Vue 3 + TypeScript + Vite + Element Plus 的现代化博客前端应用。

## 技术栈

- **框架**: Vue 3.5 (Composition API)
- **语言**: TypeScript
- **构建工具**: Vite 7
- **UI框架**: Element Plus
- **路由**: Vue Router 4
- **状态管理**: Pinia
- **HTTP客户端**: Axios
- **Markdown**: markdown-it + highlight.js
- **日期处理**: dayjs

## 功能特性

- ✅ 用户注册/登录
- ✅ 文章列表、详情、搜索
- ✅ Markdown编辑器
- ✅ 分类和标签浏览
- ✅ 评论系统
- ✅ 管理员后台
- ✅ 响应式设计
- ✅ Token自动刷新
- ✅ 路由权限控制

## 快速开始

### 安装依赖

```bash
npm install
```

### 开发环境

```bash
npm run dev
```

访问 http://localhost:5173

### 生产构建

```bash
npm run build
```

### 预览生产构建

```bash
npm run preview
```

## 项目结构

```
src/
├── api/              # API接口定义
│   ├── index.ts
│   └── modules/      # 按模块划分的API
│       ├── auth.ts
│       ├── post.ts
│       ├── category.ts
│       ├── tag.ts
│       ├── comment.ts
│       └── user.ts
├── assets/           # 静态资源
├── components/       # 公共组件
│   ├── Header.vue
│   ├── Footer.vue
│   └── ...
├── router/           # 路由配置
│   └── index.ts
├── stores/           # Pinia状态管理
│   ├── user.ts
│   ├── post.ts
│   ├── category.ts
│   └── comment.ts
├── types/            # TypeScript类型定义
│   └── index.ts
├── utils/            # 工具函数
│   └── request.ts    # Axios封装
├── views/            # 页面组件
│   ├── Home.vue
│   ├── Login.vue
│   ├── Register.vue
│   ├── PostDetail.vue
│   ├── PostEdit.vue
│   └── admin/        # 管理后台页面
│       ├── Layout.vue
│       ├── Dashboard.vue
│       ├── PostManagement.vue
│       └── ...
├── App.vue           # 根组件
└── main.ts           # 入口文件
```

## 环境变量

创建 `.env.local` 文件（参考 `.env.local.example`）：

```bash
# API地址
VITE_API_BASE_URL=http://localhost:8080/api

# 应用信息
VITE_APP_TITLE=个人博客
VITE_APP_DESCRIPTION=分享技术心得与生活感悟
```

## 路由说明

### 公开路由
- `/` - 首页
- `/login` - 登录
- `/register` - 注册
- `/post/:id` - 文章详情
- `/category/:id` - 分类页面
- `/tag/:id` - 标签页面

### 需要登录
- `/post/edit/:id?` - 编辑/创建文章

### 需要管理员权限
- `/admin` - 管理后台
- `/admin/dashboard` - 仪表盘
- `/admin/posts` - 文章管理
- `/admin/categories` - 分类管理
- `/admin/tags` - 标签管理
- `/admin/comments` - 评论管理
- `/admin/users` - 用户管理
- `/admin/settings` - 系统设置

## API封装

### 请求拦截器
- 自动添加 Authorization header
- 统一错误处理

### 响应拦截器
- 自动处理Token刷新
- 统一响应格式处理
- 错误提示

### 使用示例

```typescript
import { authApi, postApi } from '@/api'

// 登录
const res = await authApi.login({ username, password })

// 获取文章列表
const posts = await postApi.getList({ page: 1, size: 10 })
```

## 状态管理

使用 Pinia 进行状态管理：

```typescript
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

// 登录
await userStore.login({ username, password })

// 获取用户信息
const user = userStore.userInfo

// 登出
userStore.logout()
```

## 开发规范

### 代码风格
- 使用 Composition API
- 使用 `<script setup>` 语法
- TypeScript 严格模式
- 组件使用 PascalCase 命名

### 提交规范
- feat: 新功能
- fix: 修复bug
- docs: 文档更新
- style: 代码格式调整
- refactor: 重构
- test: 测试相关
- chore: 构建/工具相关

## 常见问题

### 1. 端口被占用
修改 `vite.config.ts` 中的端口配置：
```typescript
export default defineConfig({
  server: {
    port: 3000
  }
})
```

### 2. API请求失败
- 检查后端是否启动
- 检查 `.env.local` 中的 API 地址
- 检查浏览器控制台的网络请求

### 3. 路由404
- 确保使用 `createWebHistory` 模式
- 生产环境需要配置服务器重定向到 index.html

## 浏览器支持

- Chrome >= 87
- Firefox >= 78
- Safari >= 14
- Edge >= 88

## License

MIT
