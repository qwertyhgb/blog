# 个人博客系统 - 前端

这是一个基于 Vue 3 + TypeScript + Element Plus 构建的个人博客系统前端应用。

## 技术栈

- Vue 3
- TypeScript
- Element Plus
- Vue Router
- Pinia
- Axios
- Day.js
- Markdown-it

## 项目结构

```
frontend/
├── public/                 # 静态资源
├── src/
│   ├── api/               # API 接口
│   ├── assets/            # 资源文件
│   ├── components/        # 公共组件
│   │   ├── Header.vue     # 页头组件
│   │   └── Footer.vue     # 页脚组件
│   ├── router/            # 路由配置
│   ├── stores/            # 状态管理
│   │   ├── user.ts        # 用户状态
│   │   ├── post.ts        # 文章状态
│   │   ├── category.ts    # 分类和标签状态
│   │   └── comment.ts     # 评论状态
│   ├── utils/             # 工具函数
│   │   └── request.ts     # 请求封装
│   ├── views/             # 页面组件
│   │   ├── Home.vue       # 首页
│   │   ├── Login.vue      # 登录页
│   │   ├── Register.vue   # 注册页
│   │   ├── PostDetail.vue # 文章详情页
│   │   ├── PostEdit.vue   # 文章编辑页
│   │   ├── NotFound.vue   # 404页面
│   │   └── admin/         # 管理后台页面
│   │       ├── Layout.vue           # 后台布局
│   │       ├── Dashboard.vue        # 仪表盘
│   │       ├── PostManagement.vue   # 文章管理
│   │       ├── CategoryManagement.vue # 分类管理
│   │       ├── TagManagement.vue    # 标签管理
│   │       ├── CommentManagement.vue # 评论管理
│   │       ├── UserManagement.vue   # 用户管理
│   │       └── SystemSettings.vue   # 系统设置
│   ├── App.vue            # 根组件
│   └── main.ts            # 入口文件
├── .env.development       # 开发环境变量
├── .env.production        # 生产环境变量
├── index.html             # HTML 模板
└── package.json           # 项目配置
```

## 功能特性

### 用户功能
- 用户注册和登录
- 浏览文章列表和详情
- 按分类和标签筛选文章
- 搜索文章
- 发表评论
- 编辑个人文章

### 管理员功能
- 仪表盘数据统计
- 文章管理（增删改查）
- 分类管理
- 标签管理
- 评论管理（审核、删除）
- 用户管理
- 系统设置

## 开发指南

### 环境要求
- Node.js 16+
- npm 或 yarn

### 安装依赖
```bash
npm install
```

### 开发运行
```bash
npm run dev
```

### 构建生产版本
```bash
npm run build
```

### 预览生产版本
```bash
npm run preview
```

## 环境变量

### 开发环境 (.env.development)
```
VITE_API_BASE_URL=http://localhost:8080/api
VITE_APP_TITLE=个人博客
VITE_APP_DESCRIPTION=分享技术心得与生活感悟
```

### 生产环境 (.env.production)
```
VITE_API_BASE_URL=/api
VITE_APP_TITLE=个人博客
VITE_APP_DESCRIPTION=分享技术心得与生活感悟
```

## API 接口

API 接口封装在 `src/api/index.ts` 中，包括：
- 用户认证接口
- 文章接口
- 分类接口
- 标签接口
- 评论接口

## 状态管理

使用 Pinia 进行状态管理，主要 store 包括：
- `useUserStore`: 用户信息和认证状态
- `usePostStore`: 文章数据
- `useCategoryStore`: 分类数据
- `useTagStore`: 标签数据
- `useCommentStore`: 评论数据

## 路由配置

路由配置在 `src/router/index.ts` 中，包括：
- 前台页面路由
- 后台管理路由
- 路由守卫（权限控制）

## 部署说明

1. 修改 `.env.production` 中的 `VITE_API_BASE_URL` 为实际的后端 API 地址
2. 执行构建命令：`npm run build`
3. 将 `dist` 目录部署到 Web 服务器

## 注意事项

- 本项目需要配合后端 API 使用
- 确保后端 API 服务正常运行
- 生产环境需要配置正确的 API 地址