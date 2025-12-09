package com.example.blog.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 文章标签实体类
 * 
 * 存储博客文章的标签信息，用于标记和筛选文章
 */
@Data
public class Tag {
    /** 标签ID，主键 */
    private Long id;
    
    /** 标签名称 */
    private String name;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
}