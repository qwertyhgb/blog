-- 注意：首次运行前请手动创建数据库
-- CREATE DATABASE IF NOT EXISTS blog DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- USE blog;

-- ====================
-- 用户表 (user)
-- 存储系统用户信息，包括管理员和普通用户
-- ====================
CREATE TABLE IF NOT EXISTS `user` (
  -- 主键ID，自增
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  
  -- 用户名，唯一且不能为空
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  
  -- 密码，使用BCrypt加密存储
  `password` VARCHAR(255) NOT NULL COMMENT '密码',
  
  -- 昵称，可为空
  `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
  
  -- 邮箱，唯一且可为空
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  
  -- 头像URL，可为空
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
  
  -- 用户角色：USER普通用户，ADMIN管理员，默认为USER
  `role` VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色：USER用户，ADMIN管理员',
  
  -- 用户状态：0禁用，1正常，默认为1
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0禁用，1正常',
  
  -- 最后登录时间，可为空
  `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
  
  -- 创建时间，自动设置为当前时间
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  -- 更新时间，自动更新为当前时间
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  
  -- 设置主键
  PRIMARY KEY (`id`),
  
  -- 设置用户名唯一索引
  UNIQUE KEY `uk_username` (`username`),
  
  -- 设置邮箱唯一索引
  UNIQUE KEY `uk_email` (`email`),
  
  -- 设置状态索引，便于按状态查询
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ====================
-- 博客分类表 (category)
-- 存储博客文章的分类信息
-- ====================
CREATE TABLE IF NOT EXISTS `category` (
  -- 主键ID，自增
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  
  -- 分类名称，唯一且不能为空
  `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  
  -- 分类描述，可为空
  `description` VARCHAR(255) DEFAULT NULL COMMENT '分类描述',
  
  -- 排序顺序，数值越小越靠前，默认为0
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序顺序',
  
  -- 创建时间，自动设置为当前时间
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  -- 更新时间，自动更新为当前时间
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  
  -- 设置主键
  PRIMARY KEY (`id`),
  
  -- 设置分类名称唯一索引
  UNIQUE KEY `uk_name` (`name`),
  
  -- 设置排序索引
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='博客分类表';

-- ====================
-- 博客标签表 (tag)
-- 存储博客文章的标签信息
-- ====================
CREATE TABLE IF NOT EXISTS `tag` (
  -- 主键ID，自增
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  
  -- 标签名称，唯一且不能为空
  `name` VARCHAR(50) NOT NULL COMMENT '标签名称',
  
  -- 创建时间，自动设置为当前时间
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  -- 更新时间，自动更新为当前时间
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  
  -- 设置主键
  PRIMARY KEY (`id`),
  
  -- 设置标签名称唯一索引
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='博客标签表';

-- ====================
-- 博客文章表 (post)
-- 存储博客文章的核心内容
-- ====================
CREATE TABLE IF NOT EXISTS `post` (
  -- 主键ID，自增
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '文章ID',
  
  -- 文章标题，不能为空
  `title` VARCHAR(255) NOT NULL COMMENT '文章标题',
  
  -- 文章摘要，可为空
  `summary` VARCHAR(500) DEFAULT NULL COMMENT '文章摘要',
  
  -- 文章内容，使用LONGTEXT存储大量文本
  `content` LONGTEXT NOT NULL COMMENT '文章内容',
  
  -- 封面图片URL，可为空
  `cover_image` VARCHAR(255) DEFAULT NULL COMMENT '封面图片URL',
  
  -- 作者ID，关联user表
  `author_id` BIGINT NOT NULL COMMENT '作者ID',
  
  -- 分类ID，关联category表，可为空
  `category_id` BIGINT DEFAULT NULL COMMENT '分类ID',
  
  -- 文章状态：0草稿，1发布，2下架，默认为1
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0草稿，1发布，2下架',
  
  -- 浏览次数，默认为0
  `view_count` INT NOT NULL DEFAULT 0 COMMENT '浏览次数',
  
  -- 点赞次数，默认为0
  `like_count` INT NOT NULL DEFAULT 0 COMMENT '点赞次数',
  
  -- 评论次数，默认为0
  `comment_count` INT NOT NULL DEFAULT 0 COMMENT '评论次数',
  
  -- 是否置顶：0否，1是，默认为0
  `is_top` TINYINT NOT NULL DEFAULT 0 COMMENT '是否置顶：0否，1是',
  
  -- 是否删除：0否，1是，默认为0
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0否，1是',
  
  -- 发布时间，可为空
  `published_time` DATETIME DEFAULT NULL COMMENT '发布时间',
  
  -- 创建时间，自动设置为当前时间
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  -- 更新时间，自动更新为当前时间
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  
  -- 设置主键
  PRIMARY KEY (`id`),
  
  -- 设置作者ID索引，便于按作者查询
  KEY `idx_author_id` (`author_id`),
  
  -- 设置分类ID索引，便于按分类查询
  KEY `idx_category_id` (`category_id`),
  
  -- 设置状态索引，便于按状态查询
  KEY `idx_status` (`status`),
  
  -- 设置删除状态索引，便于查询未删除的文章
  KEY `idx_is_deleted` (`is_deleted`),
  
  -- 设置创建时间索引，便于按时间排序
  KEY `idx_create_time` (`create_time`),
  
  -- 设置发布时间索引，便于按发布时间查询
  KEY `idx_published_time` (`published_time`),
  
  -- 设置置顶索引，便于查询置顶文章
  KEY `idx_is_top` (`is_top`),
  
  -- 设置全文索引，支持标题和内容的全文搜索
  FULLTEXT KEY `ft_title_content` (`title`, `content`) WITH PARSER ngram,
  
  -- 设置外键约束：文章作者必须存在于用户表中
  CONSTRAINT `fk_post_author` FOREIGN KEY (`author_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT,
  
  -- 设置外键约束：文章分类可以为空，若存在则必须存在于分类表中
  CONSTRAINT `fk_post_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='博客文章表';

-- ====================
-- 文章标签关联表 (post_tag)
-- 多对多关联表，连接文章和标签
-- ====================
CREATE TABLE IF NOT EXISTS `post_tag` (
  -- 主键ID，自增
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  
  -- 文章ID，关联post表
  `post_id` BIGINT NOT NULL COMMENT '文章ID',
  
  -- 标签ID，关联tag表
  `tag_id` BIGINT NOT NULL COMMENT '标签ID',
  
  -- 创建时间，自动设置为当前时间
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  -- 设置主键
  PRIMARY KEY (`id`),
  
  -- 设置文章ID和标签ID的联合唯一索引，防止重复关联
  UNIQUE KEY `uk_post_tag` (`post_id`, `tag_id`),
  
  -- 设置标签ID索引，便于按标签查询
  KEY `idx_tag_id` (`tag_id`),
  
  -- 设置外键约束：关联的文章必须存在于文章表中，删除文章时级联删除关联记录
  CONSTRAINT `fk_post_tag_post` FOREIGN KEY (`post_id`) REFERENCES `post` (`id`) ON DELETE CASCADE,
  
  -- 设置外键约束：关联的标签必须存在于标签表中，删除标签时级联删除关联记录
  CONSTRAINT `fk_post_tag_tag` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章标签关联表';

-- ====================
-- 评论表 (comment)
-- 存储文章的评论信息，支持层级回复
-- ====================
CREATE TABLE IF NOT EXISTS `comment` (
  -- 主键ID，自增
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  
  -- 文章ID，关联post表
  `post_id` BIGINT NOT NULL COMMENT '文章ID',
  
  -- 用户ID，关联user表
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  
  -- 父评论ID，用于实现评论回复功能，可为空
  `parent_id` BIGINT DEFAULT NULL COMMENT '父评论ID，用于回复',
  
  -- 评论内容，使用TEXT存储
  `content` TEXT NOT NULL COMMENT '评论内容',
  
  -- 评论状态：0待审核，1已通过，2已拒绝，默认为1
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0待审核，1已通过，2已拒绝',
  
  -- 是否删除：0否，1是，默认为0
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0否，1是',
  
  -- 创建时间，自动设置为当前时间
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  -- 更新时间，自动更新为当前时间
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  
  -- 设置主键
  PRIMARY KEY (`id`),
  
  -- 设置文章ID索引，便于按文章查询评论
  KEY `idx_post_id` (`post_id`),
  
  -- 设置用户ID索引，便于按用户查询评论
  KEY `idx_user_id` (`user_id`),
  
  -- 设置父评论ID索引，便于查询回复
  KEY `idx_parent_id` (`parent_id`),
  
  -- 设置状态索引，便于按状态查询
  KEY `idx_status` (`status`),
  
  -- 设置删除状态索引，便于查询未删除的评论
  KEY `idx_is_deleted` (`is_deleted`),
  
  -- 设置复合索引，优化按文章、状态和时间查询评论
  KEY `idx_post_status_time` (`post_id`, `status`, `create_time`),
  
  -- 设置外键约束：评论的文章必须存在于文章表中，删除文章时级联删除评论
  CONSTRAINT `fk_comment_post` FOREIGN KEY (`post_id`) REFERENCES `post` (`id`) ON DELETE CASCADE,
  
  -- 设置外键约束：评论的用户必须存在于用户表中
  CONSTRAINT `fk_comment_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT,
  
  -- 设置外键约束：父评论可以为空，若存在则必须存在于评论表中，删除父评论时设为NULL
  CONSTRAINT `fk_comment_parent` FOREIGN KEY (`parent_id`) REFERENCES `comment` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- ====================
-- 插入初始数据
-- ====================

-- 插入管理员用户 (密码: admin123，已使用BCrypt加密)
INSERT IGNORE INTO `user` (`username`, `password`, `nickname`, `email`, `role`) 
VALUES ('admin', '$2a$10$OlEQtV/Z7OYyd6HopxDV7ekdE3Hxuhkt65yKHPWN8thuD.VY7Bq2C', '管理员', 'admin@example.com', 'ADMIN');

-- 插入普通用户 (密码: admin123，已使用BCrypt加密)
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