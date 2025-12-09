package com.example.blog.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 评论实体类
 * 
 * 存储博客文章的评论信息，支持多级回复
 */
@Data
public class Comment {
    /** 评论ID，主键 */
    private Long id;
    
    /** 文章ID，外键关联文章表 */
    private Long postId;
    
    /** 用户ID，外键关联用户表 */
    private Long userId;
    
    /** 父评论ID，用于实现多级回复，0表示顶级评论 */
    private Long parentId;
    
    /** 评论内容 */
    private String content;
    
    /** 评论状态，0表示待审核，1表示已通过，2表示已拒绝 */
    private Integer status;
    
    /** 是否删除，0表示否，1表示是（软删除标记） */
    private Integer isDeleted;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
    
    // 关联字段
    
    /** 评论用户对象 */
    private User user;
    
    /** 评论所属文章对象 */
    private Post post;
    
    /** 父评论对象，用于多级回复 */
    private Comment parent;
}