package com.example.blog.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Comment {
    private Long id;
    private Long postId;
    private Long userId;
    private Long parentId;
    private String content;
    private Integer status; // 0待审核，1已通过，2已拒绝
    private Integer isDeleted; // 0否，1是
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 关联字段
    private User user;
    private Post post;
    private Comment parent;
}