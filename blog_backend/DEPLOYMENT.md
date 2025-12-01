# 部署指南

## 开发环境部署

### 1. 数据库准备

```sql
CREATE DATABASE IF NOT EXISTS blog DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. 后端配置

复制配置文件模板：
```bash
cp src/main/resources/application-local.properties.example src/main/resources/application-local.properties
```

修改 `application-local.properties`：
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/blog?...
spring.datasource.username=root
spring.datasource.password=your_password

# 首次运行设为 always
spring.sql.init.mode=always
```

### 3. 启动后端

```bash
# Windows
mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

**重要：** 首次启动成功后，将 `spring.sql.init.mode` 改为 `never`

### 4. 启动前端

```bash
cd blog_frontend
npm install
npm run dev
```

## 生产环境部署

### 方式一：Docker部署（推荐）

#### 1. 创建 Dockerfile（后端）

```dockerfile
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/blog-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
```

#### 2. 创建 docker-compose.yml

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: your_password
      MYSQL_DATABASE: blog
    volumes:
      - mysql_data:/var/lib/mysql
      - ./blog_backend/src/main/resources/db/init.sql:/docker-entrypoint-initdb.d/init.sql
    ports:
      - "3306:3306"
    networks:
      - blog-network

  backend:
    build: ./blog_backend
    environment:
      DB_URL: jdbc:mysql://mysql:3306/blog?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
      DB_USERNAME: root
      DB_PASSWORD: your_password
      JWT_SECRET: your_very_long_secret_key_at_least_32_characters
    ports:
      - "8080:8080"
    depends_on:
      - mysql
    networks:
      - blog-network

  frontend:
    image: nginx:alpine
    volumes:
      - ./blog_frontend/dist:/usr/share/nginx/html
      - ./nginx.conf:/etc/nginx/conf.d/default.conf
    ports:
      - "80:80"
    depends_on:
      - backend
    networks:
      - blog-network

volumes:
  mysql_data:

networks:
  blog-network:
```

#### 3. 构建并启动

```bash
# 构建后端
cd blog_backend
mvn clean package -DskipTests

# 构建前端
cd ../blog_frontend
npm run build

# 启动所有服务
cd ..
docker-compose up -d
```

### 方式二：传统部署

#### 后端部署

1. 打包应用：
```bash
mvn clean package -DskipTests
```

2. 上传 `target/blog-0.0.1-SNAPSHOT.jar` 到服务器

3. 创建启动脚本 `start.sh`：
```bash
#!/bin/bash
nohup java -jar blog-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=prod \
  --DB_URL=jdbc:mysql://your-db-host:3306/blog?useUnicode=true&characterEncoding=utf8&useSSL=true&serverTimezone=Asia/Shanghai \
  --DB_USERNAME=your_username \
  --DB_PASSWORD=your_password \
  --JWT_SECRET=your_very_long_secret_key_at_least_32_characters \
  > logs/app.log 2>&1 &
```

4. 启动：
```bash
chmod +x start.sh
./start.sh
```

#### 前端部署

1. 构建：
```bash
npm run build
```

2. 配置 Nginx：
```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    # 前端静态文件
    root /var/www/blog/dist;
    index index.html;
    
    # 前端路由
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
}
```

3. 部署：
```bash
# 上传dist目录到服务器
scp -r dist/* user@server:/var/www/blog/dist/

# 重启Nginx
sudo systemctl restart nginx
```

### HTTPS配置（使用Let's Encrypt）

```bash
# 安装certbot
sudo apt install certbot python3-certbot-nginx

# 获取证书
sudo certbot --nginx -d your-domain.com

# 自动续期
sudo certbot renew --dry-run
```

## 环境变量说明

### 后端环境变量

| 变量名 | 说明 | 示例 |
|--------|------|------|
| DB_URL | 数据库连接URL | jdbc:mysql://localhost:3306/blog?... |
| DB_USERNAME | 数据库用户名 | root |
| DB_PASSWORD | 数据库密码 | your_password |
| JWT_SECRET | JWT密钥（至少32字符） | your_secret_key |
| JWT_EXPIRATION | Token过期时间（毫秒） | 86400000 (24小时) |
| JWT_REFRESH_EXPIRATION | Refresh Token过期时间 | 604800000 (7天) |

### 前端环境变量

| 变量名 | 说明 | 示例 |
|--------|------|------|
| VITE_API_BASE_URL | 后端API地址 | http://localhost:8080/api |
| VITE_APP_TITLE | 应用标题 | 个人博客 |
| VITE_APP_DESCRIPTION | 应用描述 | 分享技术心得 |

## 性能优化建议

### 数据库优化
- 为常用查询字段添加索引
- 定期清理过期数据
- 配置合适的连接池大小

### 后端优化
- 启用Redis缓存热点数据
- 配置合理的JVM参数
- 使用CDN加速静态资源

### 前端优化
- 启用Gzip压缩
- 配置浏览器缓存
- 使用CDN加速

## 监控和日志

### 日志位置
- 后端日志：`logs/blog.log`
- Nginx日志：`/var/log/nginx/`

### 健康检查
```bash
# 检查后端状态
curl http://localhost:8080/api/actuator/health

# 检查前端
curl http://localhost
```

## 备份策略

### 数据库备份
```bash
# 每日备份
mysqldump -u root -p blog > backup_$(date +%Y%m%d).sql

# 自动备份脚本
0 2 * * * /path/to/backup.sh
```

### 文件备份
- 定期备份上传的图片和附件
- 备份配置文件

## 故障排查

### 常见问题

1. **数据库连接失败**
   - 检查数据库是否启动
   - 检查连接信息是否正确
   - 检查防火墙设置

2. **JWT认证失败**
   - 检查JWT_SECRET是否配置
   - 检查Token是否过期
   - 检查时区设置

3. **CORS错误**
   - 检查SecurityConfig中的CORS配置
   - 确保前端域名在允许列表中

4. **前端无法访问后端**
   - 检查Nginx代理配置
   - 检查后端是否启动
   - 检查防火墙规则
