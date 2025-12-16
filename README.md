# 个人博客系统

一个功能完整的全栈博客系统，采用前后端分离架构，包含前端（Vue3 + TypeScript + Naive UI）和后端（Spring Boot + MyBatis + MySQL）。

## ✨ 功能特性

### 用户功能
- ✅ 用户注册、登录（JWT认证）
- ✅ Token自动刷新机制（24小时有效期）
- ✅ 个人资料管理
- ✅ 角色权限控制（普通用户/管理员）

### 文章管理
- ✅ 文章发布、编辑、删除
- ✅ Markdown格式支持（markdown-it）
- ✅ 代码高亮（highlight.js）
- ✅ 文章分类管理
- ✅ 多标签系统
- ✅ 文章状态：草稿/发布/下架
- ✅ 文章置顶功能
- ✅ 浏览量、点赞统计
- ✅ 全文搜索（MySQL全文索引）
- ✅ 封面图片上传

### 评论系统
- ✅ 发表评论
- ✅ 评论回复（支持父子评论）
- ✅ 评论审核状态
- ✅ 评论管理

### 管理后台
- ✅ 仪表盘（数据统计）
- ✅ 文章管理（包含草稿）
- ✅ 分类管理
- ✅ 标签管理
- ✅ 评论管理
- ✅ 用户管理
- ✅ 系统设置

## 🛠️ 技术栈

### 后端
- **框架**: Spring Boot 3.4.12 (Java 21)
- **安全**: Spring Security + JWT (jjwt 0.12.6)
- **ORM**: MyBatis 3.0.5
- **数据库**: MySQL 8.0+
- **参数校验**: Spring Boot Validation
- **日志**: Logback
- **工具**: Lombok
- **密码加密**: BCrypt

### 前端
- **框架**: Vue 3.5 + TypeScript
- **构建工具**: Vite 7
- **UI库**: Naive UI 2.43.2
- **路由**: Vue Router 4
- **状态管理**: Pinia 2
- **HTTP客户端**: Axios 1.7.9
- **Markdown**: markdown-it 14.1.0
- **代码高亮**: highlight.js 11.10.0
- **代码规范**: ESLint + Prettier
- **图标**: @vicons/ionicons5
- **字体**: vfonts (Lato + Fira Code)
- **日期处理**: Day.js 1.11.10

## 📋 前置要求

- JDK 21+
- Maven 3.6+
- Node.js 18+
- MySQL 8.0+

## 🚀 快速开始

### 1. 数据库准备

```sql
-- 创建数据库
CREATE DATABASE IF NOT EXISTS blog DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. 后端配置与启动

#### 方式一：使用启动脚本（推荐）

```bash
# 进入后端目录
cd blog_backend

# 复制环境变量模板
cp .env.example .env

# 编辑 .env 文件，填入数据库密码等配置
# 然后使用启动脚本

# Windows
start-dev.bat

# Linux/Mac
chmod +x start-dev.sh
./start-dev.sh
```

#### 方式二：手动配置启动

```bash
# 进入后端目录
cd blog_backend

# 设置环境变量（或在 .env 文件中配置）
export SPRING_PROFILES_ACTIVE=dev
export DB_PASSWORD=your_password

# Windows环境启动
mvnw.cmd spring-boot:run

# Linux/Mac环境启动
./mvnw spring-boot:run
```

后端将在 `http://localhost:8080/api` 启动

**⚠️ 重要提示：** 
- 首次运行需要初始化数据库，在 `application-dev.yml` 中设置 `spring.sql.init.mode=always`，启动成功后改回 `never`
- 生产环境请务必修改 JWT 密钥和数据库密码

### 3. 前端配置与启动

```bash
# 进入前端目录
cd blog_frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 代码格式化
npm run format

# 代码检查
npm run lint
```

前端将在 `http://localhost:5173` 启动

### 4. 默认账号

系统初始化后会创建以下测试账号：

| 角色 | 用户名 | 密码 | 说明 |
|------|--------|------|------|
| 管理员 | `admin` | `admin123` | 拥有所有权限 |
| 普通用户 | `user` | `admin123` | 基本权限 |

**建议：** 生产环境请立即修改默认密码！

## 📁 项目结构

```
blog/
├── blog_backend/                      # 后端项目（Spring Boot）
│   ├── src/main/java/com/example/blog/
│   │   ├── config/                   # 配置类（Security、CORS等）
│   │   ├── controller/               # REST API控制器
│   │   ├── dto/                      # 数据传输对象（含参数校验）
│   │   ├── entity/                   # 实体类
│   │   ├── exception/                # 异常处理（统一错误码）
│   │   │   ├── ErrorCode.java       # 错误码枚举
│   │   │   ├── BusinessException.java
│   │   │   └── GlobalExceptionHandler.java
│   │   ├── filter/                   # JWT认证过滤器
│   │   ├── mapper/                   # MyBatis Mapper接口
│   │   ├── service/                  # 业务逻辑层
│   │   └── util/                     # 工具类
│   ├── src/main/resources/
│   │   ├── mapper/                   # MyBatis XML映射文件
│   │   ├── db/init.sql              # 数据库初始化脚本
│   │   ├── application.properties    # 主配置文件
│   │   ├── application-dev.yml       # 开发环境配置
│   │   ├── application-prod.yml      # 生产环境配置
│   │   └── logback-spring.xml        # 日志配置
│   ├── .env.example                  # 环境变量模板
│   ├── start-dev.sh                  # Linux/Mac启动脚本
│   ├── start-dev.bat                 # Windows启动脚本
│   └── pom.xml                       # Maven依赖配置
│
└── blog_frontend/                     # 前端项目（Vue3）
    ├── src/
    │   ├── api/                      # API接口封装（完整类型）
    │   │   ├── modules/              # 按模块划分的API
    │   │   └── index.ts
    │   ├── components/               # 可复用组件
    │   ├── router/                   # 路由配置（类型化meta）
    │   ├── stores/                   # Pinia状态管理（优化loading）
    │   │   ├── user.ts
    │   │   ├── post.ts
    │   │   ├── category.ts
    │   │   └── comment.ts
    │   ├── types/                    # TypeScript类型定义
    │   ├── utils/                    # 工具函数
    │   │   └── request.ts            # Axios封装（泛型支持）
    │   ├── views/                    # 页面视图
    │   └── App.vue
    ├── .eslintrc.cjs                 # ESLint配置
    ├── .prettierrc.json              # Prettier配置
    ├── package.json
    └── vite.config.ts
```

## 🗄️ 数据库设计

系统包含6张核心表：

| 表名 | 说明 | 关键字段 |
|------|------|----------|
| `user` | 用户表 | username, password, role, status |
| `post` | 文章表 | title, content, author_id, category_id, status, view_count |
| `category` | 分类表 | name, description, sort_order |
| `tag` | 标签表 | name |
| `post_tag` | 文章标签关联表 | post_id, tag_id |
| `comment` | 评论表 | post_id, user_id, parent_id, content, status |

### 特性
- ✅ 外键约束保证数据完整性
- ✅ 软删除机制（is_deleted字段）
- ✅ 全文索引支持搜索
- ✅ 时间戳自动更新
- ✅ 级联删除规则

## 📡 API接口文档

### 认证接口 (`/api/auth`)
| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | `/auth/login` | 用户登录 | 公开 |
| POST | `/auth/register` | 用户注册 | 公开 |
| POST | `/auth/refresh` | 刷新Token | 公开 |
| GET | `/auth/me` | 获取当前用户信息 | 需登录 |

### 文章接口 (`/api/posts`)
| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/posts` | 获取文章列表（分页、搜索） | 公开 |
| GET | `/posts/{id}` | 获取文章详情 | 公开 |
| GET | `/posts/admin` | 管理后台文章列表 | 管理员 |
| GET | `/posts/category/{categoryId}` | 按分类查询 | 公开 |
| GET | `/posts/tag/{tagId}` | 按标签查询 | 公开 |
| POST | `/posts` | 创建文章 | 需登录 |
| PUT | `/posts/{id}` | 更新文章 | 作者/管理员 |
| DELETE | `/posts/{id}` | 删除文章 | 作者/管理员 |
| POST | `/posts/{id}/like` | 点赞文章 | 公开 |

### 响应格式

成功响应：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": { ... }
}
```

错误响应（统一错误码）：
```json
{
  "code": 4000,
  "message": "文章不存在",
  "data": null
}
```

## 🔐 安全机制

### JWT认证流程
1. 用户登录成功后返回 `token` 和 `refreshToken`
2. **Token**: 有效期24小时（86400000ms）
3. **Refresh Token**: 有效期7天（604800000ms）
4. 前端自动在请求头添加 `Authorization: Bearer {token}`
5. Token过期时，使用Refresh Token自动刷新
6. 刷新失败则跳转到登录页

### 密码安全
- 使用 **BCrypt** 算法加密存储
- 加密强度：`$2a$10`
- 永不明文存储或传输密码

### 参数校验
- 使用 **Spring Boot Validation** 进行参数校验
- 所有 DTO 都添加了校验注解（`@NotBlank`, `@Email`, `@Size` 等）
- 统一的参数校验错误处理

### 错误码体系
- 统一的错误码枚举 `ErrorCode`
- 分类清晰：通用错误、认证错误、业务错误等
- 前后端约定一致的错误码

### 权限控制
- **前端**: 路由守卫检查登录状态和角色（类型化 meta）
- **后端**: Spring Security + 方法级别权限控制
- **角色**: `ROLE_USER`（普通用户）、`ROLE_ADMIN`（管理员）

## 🎨 前端特性

### TypeScript 类型安全
- ✅ 所有 API 接口完整类型定义
- ✅ 泛型请求封装 `request<T>()`
- ✅ 统一的 `ApiResponse<T>` 类型
- ✅ 完整的 IDE 智能提示

### 状态管理优化
- 使用 **Pinia** 管理全局状态
- 统一的 `withLoading` 封装，减少 60% 样板代码
- 智能状态同步（删除/更新自动更新列表）
- 持久化Token到localStorage

### 路由守卫
- 类型化的 `RouteMeta`（`requiresAuth`, `requiresAdmin`, `roles`）
- 通用权限校验逻辑
- 自动获取用户信息
- 登录后重定向到原页面

### HTTP请求封装
- 自动添加Token
- Token过期自动刷新
- 请求队列机制避免并发刷新
- 统一错误处理和消息提示

### 代码规范
- **ESLint**: Vue3 + TypeScript 规则
- **Prettier**: 统一代码格式
- 保存时自动格式化
- 提交前自动检查

## 🔧 开发配置

### 后端环境变量

创建 `.env` 文件（参考 `.env.example`）：

```env
# 环境配置
SPRING_PROFILES_ACTIVE=dev

# 数据库配置
DB_HOST=localhost
DB_PORT=3306
DB_NAME=blog
DB_USERNAME=root
DB_PASSWORD=your_password

# JWT配置
JWT_SECRET=your_very_long_and_secure_secret_key
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000

# 文件上传
FILE_UPLOAD_DIR=uploads
```

### 前端环境变量

创建 `.env.development` 文件：
```env
VITE_API_BASE_URL=http://localhost:8080/api
```

创建 `.env.production` 文件：
```env
VITE_API_BASE_URL=https://your-domain.com/api
```

### 前端开发命令

```bash
npm run dev          # 启动开发服务器
npm run build        # 构建生产版本
npm run preview      # 预览生产版本
npm run lint         # 检查并修复代码规范
npm run lint:check   # 仅检查代码规范
npm run format       # 格式化代码
npm run format:check # 检查代码格式
```

## 📦 生产部署

### 后端部署

```bash
cd blog_backend

# 设置环境变量
export SPRING_PROFILES_ACTIVE=prod
export DB_HOST=your-db-host
export DB_PASSWORD=your-password
export JWT_SECRET=your-strong-secret

# 打包
mvn clean package -DskipTests

# 运行
java -jar target/blog-0.0.1-SNAPSHOT.jar
```

### 前端部署

```bash
cd blog_frontend

# 构建
npm run build

# 部署 dist 目录到 Web 服务器
```

### Nginx配置示例

```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    # 前端静态文件
    root /var/www/blog/dist;
    index index.html;
    
    # 前端路由（SPA）
    location / {
        try_files $uri $uri/ /index.html;
    }
    
    # 后端API代理
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
    
    # Gzip压缩
    gzip on;
    gzip_types text/plain text/css application/json application/javascript;
}
```

## 🐛 常见问题

### 后端问题

**Q: 启动后端报数据库连接失败？**  
A: 检查：
- MySQL服务是否启动
- 数据库 `blog` 是否已创建
- `.env` 文件中的数据库配置是否正确

**Q: 启动报 `Table 'blog.user' doesn't exist`？**  
A: 首次运行需要初始化数据库：
1. 在 `application-dev.yml` 中设置 `spring.sql.init.mode: always`
2. 启动一次后改回 `never`

**Q: JWT认证失败？**  
A: 检查 `JWT_SECRET` 环境变量是否设置且长度至少32字符

### 前端问题

**Q: 前端无法连接后端？**  
A: 检查：
- 后端是否正常启动在8080端口
- `.env.development` 中的 API 地址是否正确
- 浏览器控制台是否有 CORS 错误

**Q: ESLint 报错？**  
A: 运行 `npm run lint` 自动修复

**Q: 类型提示不完整？**  
A: 确保已安装所有依赖，重启 VSCode

## ⚡ 性能优化

### 后端优化
- ✅ HikariCP 连接池优化
- ✅ 异步日志（Logback AsyncAppender）
- ✅ 多环境配置（dev/prod）
- ✅ Gzip 压缩（生产环境）
- ✅ 统一错误处理

### 前端优化
- ✅ Vite 构建优化
- ✅ 路由懒加载
- ✅ 代码分割
- ✅ 请求防抖与节流
- ✅ 智能状态管理

## 🎯 项目亮点

### 代码质量
- ✅ 完整的 TypeScript 类型系统
- ✅ ESLint + Prettier 代码规范
- ✅ 统一的错误码体系
- ✅ 参数校验完善

### 架构设计
- ✅ 前后端分离
- ✅ RESTful API 设计
- ✅ 多环境配置隔离
- ✅ 统一响应格式

### 开发体验
- ✅ 热更新
- ✅ 完整的 IDE 提示
- ✅ 一键启动脚本
- ✅ 详细的日志记录

## 📝 更新日志

### v1.1.0 (2024-12)
- ✅ 前端完整类型化（API、Router、Store）
- ✅ 添加 ESLint + Prettier 代码规范
- ✅ 优化 Store loading 封装
- ✅ 后端多环境配置
- ✅ 统一错误码体系
- ✅ 添加参数校验
- ✅ 完善日志系统

### v1.0.0 (2024-11)
- ✅ 基础功能完成
- ✅ 用户认证系统
- ✅ 文章管理系统
- ✅ 评论系统
- ✅ 管理后台

## 🚧 待开发功能

- [ ] Redis缓存热点数据
- [ ] ElasticSearch全文搜索
- [ ] 文章草稿自动保存
- [ ] 用户头像上传
- [ ] 邮件通知
- [ ] RSS订阅
- [ ] Docker容器化部署

## 🤝 贡献

欢迎提交Issue和Pull Request！

提交前请确保：
- 代码通过 ESLint 检查
- 代码已格式化（Prettier）
- 添加了必要的注释

## 📄 开源协议

本项目采用 [MIT](LICENSE) 协议开源。

---

**⭐ 如果这个项目对你有帮助，请给一个Star支持一下！**