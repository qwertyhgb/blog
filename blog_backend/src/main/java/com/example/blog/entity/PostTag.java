package com.example.blog.entity;

import lombok.Data;

@Data
public class PostTag {
    private Long id;
    private Long postId;
    private Long tagId;
}
