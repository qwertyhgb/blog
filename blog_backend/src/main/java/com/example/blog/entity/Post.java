package com.example.blog.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Post {
    private Long id;
    private String title;
    private String summary;
    private String content;
    private String coverImage;
    private Long authorId;
    private Long categoryId;
    private Integer status; // 0草稿，1发布，2下架
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer isTop; // 0否，1是
    private Integer isDeleted; // 0否，1是
    private LocalDateTime publishedTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 关联字段
    private User author;
    private Category category;
    private List<Tag> tags;
}