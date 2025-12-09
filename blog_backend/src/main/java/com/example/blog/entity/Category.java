package com.example.blog.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 文章分类实体类
 * 
 * 存储博客文章的分类信息，用于组织和归类文章
 */
@Data
public class Category {
    /** 分类ID，主键 */
    private Long id;
    
    /** 分类名称 */
    private String name;
    
    /** 分类描述 */
    private String description;
    
    /** 排序顺序，数值越小越靠前 */
    private Integer sortOrder;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
}