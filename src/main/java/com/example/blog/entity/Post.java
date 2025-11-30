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
    private Integer status;
    private Integer viewCount;
    private Integer likeCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 关联字段
    private User author;
    private Category category;
    private List<Tag> tags;
}