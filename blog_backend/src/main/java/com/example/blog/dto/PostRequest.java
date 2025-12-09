package com.example.blog.dto;

import lombok.Data;
import java.util.List;

/**
 * 文章请求数据传输对象
 * 
 * 用于接收创建和更新文章请求的数据
 */
@Data
public class PostRequest {
    /** 文章标题 */
    private String title;
    
    /** 文章摘要 */
    private String summary;
    
    /** 文章内容 */
    private String content;
    
    /** 封面图片URL */
    private String coverImage;
    
    /** 分类ID */
    private Long categoryId;
    
    /** 文章状态，0表示草稿，1表示已发布，2表示已下架 */
    private Integer status;
    
    /** 标签ID列表 */
    private List<Long> tagIds;
}