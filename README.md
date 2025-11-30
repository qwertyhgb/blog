# 个人博客系统

这是一个基于Spring Boot + MyBatis的个人博客系统。

## 技术栈

- 后端框架：Spring Boot
- 数据库：MySQL
- ORM框架：MyBatis
- 安全框架：Spring Security + JWT
- 其他：Lombok, Spring Boot Test

## 项目结构

```
src/main/java/com/example/blog/
├── config          # 配置类
├── controller      # 控制器
├── dto            # 数据传输对象
├── entity         # 实体类
├── exception      # 异常处理
├── filter         # 过滤器
├── mapper         # MyBatis Mapper接口
├── service        # 服务层
└── util           # 工具类

src/main/resources/
├── mapper         # MyBatis XML映射文件
└── application.yml # 配置文件
```

## 功能特性

- 用户注册、登录、权限管理
- 文章的增删改查、分类、标签
- 评论功能
- JWT认证
- RESTful API设计

## 快速开始

1. 配置数据库连接信息（application.yml）
2. 创建数据库和表（参考SQL脚本）
3. 运行BlogApplication启动项目
4. 访问API接口进行测试

## API文档

### 认证相关
- POST /auth/login - 用户登录
- POST /auth/register - 用户注册
- POST /auth/refresh - 刷新令牌
- GET /auth/me - 获取当前用户信息

### 文章相关
- GET /posts - 获取文章列表
- GET /posts/{id} - 获取文章详情
- POST /posts - 创建文章
- PUT /posts/{id} - 更新文章
- DELETE /posts/{id} - 删除文章

### 分类相关
- GET /categories - 获取分类列表
- GET /categories/{id} - 获取分类详情
- POST /categories - 创建分类
- PUT /categories/{id} - 更新分类
- DELETE /categories/{id} - 删除分类

### 标签相关
- GET /tags - 获取标签列表
- GET /tags/{id} - 获取标签详情
- POST /tags - 创建标签
- PUT /tags/{id} - 更新标签
- DELETE /tags/{id} - 删除标签

### 评论相关
- GET /comments/post/{postId} - 获取文章评论
- GET /comments/{id} - 获取评论详情
- POST /comments - 创建评论
- PUT /comments/{id} - 更新评论
- DELETE /comments/{id} - 删除评论

## 开发说明

- 使用JWT进行身份认证
- 密码使用BCrypt加密存储
- 使用MyBatis进行数据库操作
- 统一异常处理
- RESTful API设计