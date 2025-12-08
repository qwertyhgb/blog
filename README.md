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

```bash
# 进入后端目录
cd blog_backend

# 修改配置文件 src/main/resources/application.properties
# 配置数据库连接信息：
# spring.datasource.url=jdbc:mysql://localhost:3306/blog?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
# spring.datasource.username=root
# spring.datasource.password=你的密码

# 首次运行需要初始化数据库，设置：
# spring.sql.init.mode=always

# Windows环境启动
mvnw.cmd spring-boot:run

# Linux/Mac环境启动
./mvnw spring-boot:run

# 或者使用已安装的Maven
mvn spring-boot:run
```

后端将在 `http://localhost:8080/api` 启动

**⚠️ 重要提示：** 首次启动成功后，请将 `application.properties` 中的 `spring.sql.init.mode` 改为 `never`，避免每次启动都重新初始化数据库。

### 3. 前端配置与启动

```bash
# 进入前端目录
cd blog_frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
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
├── blog_backend/              # 后端项目（Spring Boot）
│   ├── src/main/java/com/example/blog/
│   │   ├── config/           # 配置类（Security、CORS、密码编码器）
│   │   ├── controller/       # REST API控制器
│   │   │   ├── AuthController.java
│   │   │   ├── PostController.java
│   │   │   ├── CategoryController.java
│   │   │   ├── TagController.java
│   │   │   ├── CommentController.java
│   │   │   ├── UserController.java
│   │   │   └── FileController.java
│   │   ├── dto/              # 数据传输对象
│   │   ├── entity/           # 实体类（User、Post、Category、Tag、Comment等）
│   │   ├── exception/        # 统一异常处理
│   │   ├── filter/           # JWT认证过滤器
│   │   ├── mapper/           # MyBatis Mapper接口
│   │   ├── service/          # 业务逻辑层
│   │   └── util/             # 工具类（JWT工具等）
│   ├── src/main/resources/
│   │   ├── mapper/           # MyBatis XML映射文件
│   │   ├── db/
│   │   │   └── init.sql      # 数据库初始化脚本
│   │   └── application.properties  # 应用配置
│   ├── uploads/              # 文件上传目录
│   ├── pom.xml              # Maven依赖配置
│   └── DEPLOYMENT.md        # 部署指南
│
└── blog_frontend/            # 前端项目（Vue3）
    ├── src/
    │   ├── api/             # API接口封装
    │   │   ├── modules/     # 按模块划分的API
    │   │   └── index.ts
    │   ├── assets/          # 静态资源
    │   ├── components/      # 可复用组件
    │   ├── router/          # 路由配置
    │   │   └── index.ts     # 包含路由守卫
    │   ├── stores/          # Pinia状态管理
    │   │   ├── user.ts      # 用户状态
    │   │   ├── post.ts      # 文章状态
    │   │   ├── category.ts  # 分类状态
    │   │   └── comment.ts   # 评论状态
    │   ├── types/           # TypeScript类型定义
    │   ├── utils/           # 工具函数
    │   │   └── request.ts   # Axios封装（含Token刷新）
    │   ├── views/           # 页面视图
    │   │   ├── admin/       # 管理后台页面
    │   │   ├── Home.vue
    │   │   ├── PostDetail.vue
    │   │   ├── PostEdit.vue
    │   │   └── ...
    │   ├── App.vue          # 根组件
    │   ├── main.ts          # 入口文件
    │   └── theme.ts         # 主题配置
    ├── public/              # 公共静态资源
    ├── package.json         # 依赖配置
    ├── vite.config.ts       # Vite配置
    └── tsconfig.json        # TypeScript配置
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

### 分类接口 (`/api/categories`)
| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/categories` | 获取所有分类 | 公开 |
| POST | `/categories` | 创建分类 | 管理员 |
| PUT | `/categories/{id}` | 更新分类 | 管理员 |
| DELETE | `/categories/{id}` | 删除分类 | 管理员 |

### 标签接口 (`/api/tags`)
| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/tags` | 获取所有标签 | 公开 |
| POST | `/tags` | 创建标签 | 管理员 |
| PUT | `/tags/{id}` | 更新标签 | 管理员 |
| DELETE | `/tags/{id}` | 删除标签 | 管理员 |

### 评论接口 (`/api/comments`)
| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/comments/post/{postId}` | 获取文章评论 | 公开 |
| POST | `/comments` | 发表评论 | 需登录 |
| DELETE | `/comments/{id}` | 删除评论 | 作者/管理员 |

### 文件接口 (`/api/files`)
| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | `/upload` | 上传文件 | 需登录 |
| GET | `/uploads/**` | 访问上传文件 | 公开 |

### 响应格式

成功响应：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": { ... }
}
```

错误响应：
```json
{
  "code": 400,
  "message": "错误信息",
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

### 权限控制
- **前端**: 路由守卫检查登录状态和角色
- **后端**: Spring Security + 方法级别权限控制
- **角色**: `ROLE_USER`（普通用户）、`ROLE_ADMIN`（管理员）

### CORS配置
- 开发环境允许 `localhost` 和 `127.0.0.1` 的所有端口
- 生产环境需配置具体域名

## 🎨 前端特性

### 状态管理
- 使用 **Pinia** 管理全局状态
- 持久化Token到localStorage
- 自动同步登录状态

### 路由守卫
- 检查登录状态
- 自动获取用户信息
- 管理员权限验证
- 登录后重定向到原页面

### HTTP请求封装
- 自动添加Token
- Token过期自动刷新
- 请求队列机制避免并发刷新
- 统一错误处理和消息提示

### UI/UX
- 响应式设计，支持移动端
- Markdown实时预览
- 代码语法高亮
- 图片上传预览
- 加载状态提示

## 🔧 开发配置

### 后端配置 (`application.properties`)

```properties
# 服务器配置
server.port=8080
server.servlet.context-path=/api

# 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/blog?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=your_password

# 数据库初始化（首次运行设为always，之后改为never）
spring.sql.init.mode=never

# MyBatis配置
mybatis.mapper-locations=classpath:mapper/*.xml
mybatis.configuration.log-impl=org.apache.ibatis.logging.stdout.StdOutImpl

# JWT配置
jwt.secret=mySecretKeyForJWT2024BlogApplicationThatIsMuchLongerAndMoreSecure
jwt.expiration=86400000
jwt.refresh.expiration=604800000

# 文件上传配置
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
file.upload-dir=uploads
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

## 📦 生产部署

### 后端部署

```bash
# 1. 打包
cd blog_backend
mvn clean package -DskipTests

# 2. 运行（使用环境变量配置）
java -jar target/blog-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=prod \
  --spring.datasource.url=jdbc:mysql://your-db-host:3306/blog?useUnicode=true&characterEncoding=utf8&useSSL=true&serverTimezone=Asia/Shanghai \
  --spring.datasource.username=your_username \
  --spring.datasource.password=your_password \
  --jwt.secret=your_very_long_and_secure_secret_key_at_least_32_characters
```

### 前端部署

```bash
# 1. 构建
cd blog_frontend
npm run build

# 2. 部署dist目录到Web服务器
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
        proxy_set_header X-Forwarded-Proto $scheme;
    }
    
    # Gzip压缩
    gzip on;
    gzip_types text/plain text/css application/json application/javascript text/xml application/xml application/xml+rss text/javascript;
    
    # 静态资源缓存
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
}
```

详细部署指南请查看 [`blog_backend/DEPLOYMENT.md`](blog_backend/DEPLOYMENT.md)

## 🐛 常见问题

### 后端问题

**Q: 启动后端报 `Communications link failure`？**  
A: 检查以下事项：
- MySQL服务是否启动
- 数据库 `blog` 是否已创建
- 用户名密码是否正确
- 端口3306是否开放

**Q: 启动报 `Table 'blog.user' doesn't exist`？**  
A: 确保 `spring.sql.init.mode=always` 并重启一次，然后改回 `never`

**Q: JWT认证失败？**  
A: 检查 `jwt.secret` 是否配置且长度至少32字符

### 前端问题

**Q: 前端无法连接后端？**  
A: 检查：
- 后端是否正常启动在8080端口
- CORS配置是否正确
- 浏览器控制台是否有错误信息

**Q: 登录后立即退出？**  
A: 可能是Token刷新失败，检查：
- JWT配置是否正确
- 后端日志是否有异常
- 浏览器localStorage是否被清空

**Q: 图片上传失败？**  
A: 检查：
- `uploads` 目录是否存在且有写权限
- 文件大小是否超过10MB限制
- 后端是否正确配置了文件上传路径

## 🚧 待开发功能

- [ ] Redis缓存热点数据
- [ ] ElasticSearch全文搜索
- [ ] 文章草稿自动保存
- [ ] 用户头像上传
- [ ] 邮件通知
- [ ] RSS订阅
- [ ] 文章导出PDF
- [ ] 访问统计图表
- [ ] 第三方登录（OAuth2）
- [ ] Docker容器化部署

## 🤝 贡献

欢迎提交Issue和Pull Request！

## 📄 开源协议

本项目采用 [MIT](LICENSE) 协议开源。

## 📧 联系方式

如有问题或建议，欢迎通过以下方式联系：
- 提交 GitHub Issue
- 发送邮件到项目维护者

---

**⭐ 如果这个项目对你有帮助，请给一个Star支持一下！**