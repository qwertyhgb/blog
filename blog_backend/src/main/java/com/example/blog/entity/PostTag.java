package com.example.blog.entity;

import lombok.Data;

/**
 * 文章标签关联实体类
 * 
 * 用于建立文章和标签之间的多对多关系，表示文章包含哪些标签
 */
@Data
public class PostTag {
    /** 关联ID，主键 */
    private Long id;
    
    /** 文章ID，外键关联文章表 */
    private Long postId;
    
    /** 标签ID，外键关联标签表 */
    private Long tagId;
}