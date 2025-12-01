package com.example.blog.dto;

import lombok.Data;
import java.util.List;

@Data
public class PostRequest {
    private String title;
    private String summary;
    private String content;
    private String coverImage;
    private Long categoryId;
    private Integer status;
    private List<Long> tagIds;
}
