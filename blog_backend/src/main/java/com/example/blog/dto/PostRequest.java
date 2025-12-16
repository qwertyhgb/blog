package com.example.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;

/**
 * 文章请求数据传输对象
 *
 * 用于接收创建和更新文章请求的数据
 */
@Data
public class PostRequest {

    /** 文章标题 */
    @NotBlank(message = "文章标题不能为空")
    @Size(min = 1, max = 200, message = "文章标题长度必须在1-200个字符之间")
    private String title;

    /** 文章摘要 */
    @Size(max = 500, message = "文章摘要长度不能超过500个字符")
    private String summary;

    /** 文章内容 */
    @NotBlank(message = "文章内容不能为空")
    @Size(min = 1, message = "文章内容不能为空")
    private String content;

    /** 封面图片URL */
    @Size(max = 500, message = "封面图片URL长度不能超过500个字符")
    private String coverImage;

    /** 分类ID */
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    /** 文章状态，0表示草稿，1表示已发布，2表示已下架 */
    @NotNull(message = "文章状态不能为空")
    private Integer status;

    /** 标签ID列表 */
    private List<Long> tagIds;
}
