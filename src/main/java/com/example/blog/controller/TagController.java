package com.example.blog.controller;

import com.example.blog.dto.ApiResponse;
import com.example.blog.entity.Tag;
import com.example.blog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tags")
public class TagController {
    
    @Autowired
    private TagService tagService;
    
    @GetMapping
    public ApiResponse<List<Tag>> getAllTags() {
        List<Tag> tags = tagService.findAll();
        return ApiResponse.success("获取标签列表成功", tags);
    }
    
    @GetMapping("/{id}")
    public ApiResponse<Tag> getTagById(@PathVariable Long id) {
        Tag tag = tagService.findById(id);
        if (tag == null) {
            return ApiResponse.error("标签不存在");
        }
        return ApiResponse.success("获取标签详情成功", tag);
    }
    
    @GetMapping("/post/{postId}")
    public ApiResponse<List<Tag>> getTagsByPostId(@PathVariable Long postId) {
        List<Tag> tags = tagService.findByPostId(postId);
        return ApiResponse.success("获取文章标签成功", tags);
    }
    
    @PostMapping
    public ApiResponse<Tag> createTag(@RequestBody Tag tag) {
        Tag createdTag = tagService.create(tag);
        return ApiResponse.success("创建标签成功", createdTag);
    }
    
    @PutMapping("/{id}")
    public ApiResponse<Tag> updateTag(@PathVariable Long id, @RequestBody Tag tag) {
        Tag existingTag = tagService.findById(id);
        if (existingTag == null) {
            return ApiResponse.error("标签不存在");
        }
        
        tag.setId(id);
        Tag updatedTag = tagService.update(tag);
        return ApiResponse.success("更新标签成功", updatedTag);
    }
    
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteTag(@PathVariable Long id) {
        Tag existingTag = tagService.findById(id);
        if (existingTag == null) {
            return ApiResponse.error("标签不存在");
        }
        
        tagService.deleteById(id);
        return ApiResponse.success("删除标签成功", null);
    }
}