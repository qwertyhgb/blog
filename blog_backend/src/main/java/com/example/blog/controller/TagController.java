package com.example.blog.controller;

import com.example.blog.dto.ApiResponse;
import com.example.blog.entity.Tag;
import com.example.blog.service.ITagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 标签控制器
 *
 * <p>
 * 提供标签的增删改查及文章标签查询，主要用于后台管理以及文章编辑时的标签选择。
 * </p>
 */
@RestController
@RequestMapping("/tags")
@io.swagger.v3.oas.annotations.tags.Tag(name = "标签管理", description = "文章标签的增删改查接口")
public class TagController {

    @Autowired
    private ITagService tagService;

    @Operation(summary = "获取所有标签", description = "获取所有文章标签列表")
    @GetMapping
    public ApiResponse<List<Tag>> getAllTags() {
        List<Tag> tags = tagService.findAll();
        return ApiResponse.success("获取标签列表成功", tags);
    }

    @Operation(summary = "获取标签详情", description = "根据ID获取标签详情")
    @GetMapping("/{id}")
    public ApiResponse<Tag> getTagById(
            @Parameter(description = "标签ID") @PathVariable Long id) {
        Tag tag = tagService.findById(id);
        if (tag == null) {
            return ApiResponse.error("标签不存在");
        }
        return ApiResponse.success("获取标签详情成功", tag);
    }

    @Operation(summary = "获取文章标签", description = "根据文章ID获取关联的标签")
    @GetMapping("/post/{postId}")
    public ApiResponse<List<Tag>> getTagsByPostId(
            @Parameter(description = "文章ID") @PathVariable Long postId) {
        // 供文章详情、编辑页使用，便于展示已选标签
        List<Tag> tags = tagService.findByPostId(postId);
        return ApiResponse.success("获取文章标签成功", tags);
    }

    @Operation(summary = "创建标签", description = "创建新的文章标签")
    @PostMapping
    public ApiResponse<Tag> createTag(@RequestBody Tag tag) {
        Tag createdTag = tagService.create(tag);
        return ApiResponse.success("创建标签成功", createdTag);
    }

    @Operation(summary = "更新标签", description = "更新指定标签的信息")
    @PutMapping("/{id}")
    public ApiResponse<Tag> updateTag(
            @Parameter(description = "标签ID") @PathVariable Long id,
            @RequestBody Tag tag) {
        Tag existingTag = tagService.findById(id);
        if (existingTag == null) {
            return ApiResponse.error("标签不存在");
        }

        // 只有路径参数中的ID可信，将其回写到实体，避免覆盖其他标签
        tag.setId(id);
        Tag updatedTag = tagService.update(tag);
        return ApiResponse.success("更新标签成功", updatedTag);
    }

    @Operation(summary = "删除标签", description = "删除指定的标签")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteTag(
            @Parameter(description = "标签ID") @PathVariable Long id) {
        Tag existingTag = tagService.findById(id);
        if (existingTag == null) {
            return ApiResponse.error("标签不存在");
        }

        tagService.deleteById(id);
        return ApiResponse.success("删除标签成功", null);
    }
}