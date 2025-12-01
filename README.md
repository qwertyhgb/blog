# 个人博客系统

一个功能完整的全栈博客系统，包含前端（Vue3 + TypeScript + Element Plus）和后端（Spring Boot + MyBatis + MySQL）。

## 技术栈

### 后端
- Spring Boot 3.4.12
- Spring Security + JWT
- MyBatis 3.0.5
- MySQL 8.0+
- Lombok

### 前端
- Vue 3.5
- TypeScript
- Vite 7
- Element Plus
- Vue Router
- Pinia
- Axios

## 功能特性

- ✅ 用户注册、登录、JWT认证
- ✅ 文章发布、编辑、删除（支持Markdown）
- ✅ 文章分类和标签管理
- ✅ 评论系统（支持回复）
- ✅ 管理员后台
- ✅ 响应式设计
- ✅ Token自动刷新

## 快速开始

### 前置要求

- JDK 21+
- Maven 3.6+
- Node.js 18+
- MySQL 8.0+

### 1. 数据库准备

```sql
-- 创建数据库
CREATE DATABASE IF NOT EXISTS blog DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. 后端启动

```bash
# 进入后端目录
cd blog_backend

# 修改配置文件 src/main/resources/application.properties
# 配置数据库连接信息：
# spring.datasource.url=jdbc:mysql://localhost:3306/blog?...
# spring.datasource.username=root
# spring.datasource.password=你的密码

# 首次运行需要初始化数据库，设置：
# spring.sql.init.mode=always

# 编译并运行
mvnw.cmd spring-boot:run

# 或者使用Maven
mvn spring-boot:run
```

后端将在 `http://localhost:8080/api` 启动

**重要：** 首次启动成功后，将 `spring.sql.init.mode` 改为 `never`，避免每次启动都重新初始化数据库。

### 3. 前端启动

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

- 管理员：`admin` / `admin123`
- 普通用户：`user` / `admin123`

## 项目结构

```
blog/
├── blog_backend/           # 后端项目
│   ├── src/main/java/
│   │   └── com/example/blog/
│   │       ├── config/     # 配置类（Security、CORS等）
│   │       ├── controller/ # 控制器
│   │       ├── dto/        # 数据传输对象
│   │       ├── entity/     # 实体类
│   │       ├── exception/  # 异常处理
│   │       ├── filter/     # JWT过滤器
│   │       ├── mapper/     # MyBatis Mapper
│   │       ├── service/    # 业务逻辑
│   │       └── util/       # 工具类
│   └── src/main/resources/
│       ├── mapper/         # MyBatis XML
│       ├── db/init.sql     # 数据库初始化脚本
│       └── application.properties
│
└── blog_frontend/          # 前端项目
    ├── src/
    │   ├── api/           # API接口
    │   ├── components/    # 组件
    │   ├── router/        # 路由配置
    │   ├── stores/        # Pinia状态管理
    │   ├── types/         # TypeScript类型
    │   ├── utils/         # 工具函数
    │   └── views/         # 页面视图
    └── vite.config.ts
```

## API文档

### 认证接口
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/refresh` - 刷新Token
- `GET /api/auth/me` - 获取当前用户信息

### 文章接口
- `GET /api/posts` - 获取文章列表（支持分页、搜索）
- `GET /api/posts/{id}` - 获取文章详情
- `POST /api/posts` - 创建文章（需登录）
- `PUT /api/posts/{id}` - 更新文章（需登录）
- `DELETE /api/posts/{id}` - 删除文章（需登录）

### 分类接口
- `GET /api/categories` - 获取所有分类
- `POST /api/categories` - 创建分类（需管理员）
- `PUT /api/categories/{id}` - 更新分类（需管理员）
- `DELETE /api/categories/{id}` - 删除分类（需管理员）

### 标签接口
- `GET /api/tags` - 获取所有标签
- `POST /api/tags` - 创建标签（需管理员）
- `PUT /api/tags/{id}` - 更新标签（需管理员）
- `DELETE /api/tags/{id}` - 删除标签（需管理员）

### 评论接口
- `GET /api/comments/post/{postId}` - 获取文章评论
- `POST /api/comments` - 发表评论（需登录）
- `DELETE /api/comments/{id}` - 删除评论（需登录）

## 生产部署

### 后端部署

```bash
# 打包
mvn clean package -DskipTests

# 运行（使用环境变量配置）
java -jar target/blog-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=prod \
  --DB_URL=jdbc:mysql://your-db-host:3306/blog \
  --DB_USERNAME=your-username \
  --DB_PASSWORD=your-password \
  --JWT_SECRET=your-secret-key
```

### 前端部署

```bash
# 构建
npm run build

# dist目录部署到Nginx或其他Web服务器
```

Nginx配置示例：
```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    root /path/to/dist;
    index index.html;
    
    location / {
        try_files $uri $uri/ /index.html;
    }
    
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

## 开发说明

### 后端开发
- 使用MyBatis进行数据库操作
- JWT Token有效期24小时，Refresh Token有效期7天
- 密码使用BCrypt加密
- 统一异常处理和响应格式

### 前端开发
- 使用Composition API
- TypeScript严格模式
- Axios自动处理Token刷新
- 路由守卫处理权限控制

## 常见问题

**Q: 启动后端报数据库连接错误？**
A: 检查MySQL是否启动，数据库是否创建，用户名密码是否正确。

**Q: 前端无法连接后端？**
A: 检查后端是否启动，端口是否正确，CORS配置是否正确。

**Q: 登录后立即退出？**
A: 检查JWT配置，确保secret足够长（至少32字符）。

## License

MIT
