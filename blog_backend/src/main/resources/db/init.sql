-- 注意：首次运行前请手动创建数据库
# CREATE DATABASE IF NOT EXISTS blog DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
# USE blog;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(255) NOT NULL COMMENT '密码',
  `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
  `role` VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色：USER用户，ADMIN管理员',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0禁用，1正常',
  `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 博客分类表
CREATE TABLE IF NOT EXISTS `category` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `description` VARCHAR(255) DEFAULT NULL COMMENT '分类描述',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序顺序',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='博客分类表';

-- 博客标签表
CREATE TABLE IF NOT EXISTS `tag` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `name` VARCHAR(50) NOT NULL COMMENT '标签名称',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='博客标签表';

-- 博客文章表
CREATE TABLE IF NOT EXISTS `post` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '文章ID',
  `title` VARCHAR(255) NOT NULL COMMENT '文章标题',
  `summary` VARCHAR(500) DEFAULT NULL COMMENT '文章摘要',
  `content` LONGTEXT NOT NULL COMMENT '文章内容',
  `cover_image` VARCHAR(255) DEFAULT NULL COMMENT '封面图片URL',
  `author_id` BIGINT NOT NULL COMMENT '作者ID',
  `category_id` BIGINT DEFAULT NULL COMMENT '分类ID',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0草稿，1发布，2下架',
  `view_count` INT NOT NULL DEFAULT 0 COMMENT '浏览次数',
  `like_count` INT NOT NULL DEFAULT 0 COMMENT '点赞次数',
  `comment_count` INT NOT NULL DEFAULT 0 COMMENT '评论次数',
  `is_top` TINYINT NOT NULL DEFAULT 0 COMMENT '是否置顶：0否，1是',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0否，1是',
  `published_time` DATETIME DEFAULT NULL COMMENT '发布时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_author_id` (`author_id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_status` (`status`),
  KEY `idx_is_deleted` (`is_deleted`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_published_time` (`published_time`),
  KEY `idx_is_top` (`is_top`),
  FULLTEXT KEY `ft_title_content` (`title`, `content`) WITH PARSER ngram,
  CONSTRAINT `fk_post_author` FOREIGN KEY (`author_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_post_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='博客文章表';

-- 文章标签关联表
CREATE TABLE IF NOT EXISTS `post_tag` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `post_id` BIGINT NOT NULL COMMENT '文章ID',
  `tag_id` BIGINT NOT NULL COMMENT '标签ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_post_tag` (`post_id`, `tag_id`),
  KEY `idx_tag_id` (`tag_id`),
  CONSTRAINT `fk_post_tag_post` FOREIGN KEY (`post_id`) REFERENCES `post` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_post_tag_tag` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章标签关联表';

-- 评论表
CREATE TABLE IF NOT EXISTS `comment` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `post_id` BIGINT NOT NULL COMMENT '文章ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `parent_id` BIGINT DEFAULT NULL COMMENT '父评论ID，用于回复',
  `content` TEXT NOT NULL COMMENT '评论内容',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0待审核，1已通过，2已拒绝',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0否，1是',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_post_id` (`post_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_status` (`status`),
  KEY `idx_is_deleted` (`is_deleted`),
  KEY `idx_post_status_time` (`post_id`, `status`, `create_time`),
  CONSTRAINT `fk_comment_post` FOREIGN KEY (`post_id`) REFERENCES `post` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_comment_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_comment_parent` FOREIGN KEY (`parent_id`) REFERENCES `comment` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- 插入初始数据
-- 插入管理员用户 (密码: admin123)
INSERT IGNORE INTO `user` (`username`, `password`, `nickname`, `email`, `role`) 
VALUES ('admin', '$2a$10$OlEQtV/Z7OYyd6HopxDV7ekdE3Hxuhkt65yKHPWN8thuD.VY7Bq2C', '管理员', 'admin@example.com', 'ADMIN');

-- 插入普通用户 (密码: admin123)
INSERT IGNORE INTO `user` (`username`, `password`, `nickname`, `email`, `role`) 
VALUES ('user', '$2a$10$OlEQtV/Z7OYyd6HopxDV7ekdE3Hxuhkt65yKHPWN8thuD.VY7Bq2C', '普通用户', 'user@example.com', 'USER');

-- 插入默认分类
INSERT IGNORE INTO `category` (`name`, `description`, `sort_order`) VALUES 
('技术', '技术相关文章', 1),
('生活', '生活随笔', 2),
('读书', '读书笔记', 3),
('旅行', '旅行见闻', 4);

-- 插入默认标签
INSERT IGNORE INTO `tag` (`name`) VALUES 
('Java'),
('Spring Boot'),
('Vue'),
('JavaScript'),
('MySQL'),
('Redis'),
('Docker'),
('Linux'),
('前端')