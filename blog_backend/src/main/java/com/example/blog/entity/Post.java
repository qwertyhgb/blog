package com.example.blog.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章实体类
 * 
 * 存储博客文章的基本信息、内容和统计数据
 */
@Data
public class Post {
    /** 文章ID，主键 */
    private Long id;
    
    /** 文章标题 */
    private String title;
    
    /** 文章摘要 */
    private String summary;
    
    /** 文章内容 */
    private String content;
    
    /** 封面图片URL */
    private String coverImage;
    
    /** 作者ID，外键关联用户表 */
    private Long authorId;
    
    /** 分类ID，外键关联分类表 */
    private Long categoryId;
    
    /** 文章状态，0表示草稿，1表示已发布，2表示已下架 */
    private Integer status;
    
    /** 浏览次数 */
    private Integer viewCount;
    
    /** 点赞数 */
    private Integer likeCount;
    
    /** 评论数 */
    private Integer commentCount;
    
    /** 是否置顶，0表示否，1表示是 */
    private Integer isTop;
    
    /** 是否删除，0表示否，1表示是（软删除标记） */
    private Integer isDeleted;
    
    /** 发布时间 */
    private LocalDateTime publishedTime;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
    
    // 关联字段
    
    /** 文章作者对象 */
    private User author;
    
    /** 文章分类对象 */
    private Category category;
    
    /** 文章标签列表 */
    private List<Tag> tags;
}