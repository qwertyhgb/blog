package com.example.blog.controller;

import com.example.blog.dto.ApiResponse;
import com.example.blog.entity.Post;
import com.example.blog.entity.User;
import com.example.blog.service.AuthService;
import com.example.blog.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {
    
    @Autowired
    private PostService postService;
    
    @Autowired
    private AuthService authService;
    
    @GetMapping
    public ApiResponse<List<Post>> getAllPosts() {
        List<Post> posts = postService.findPublished();
        return ApiResponse.success("获取文章列表成功", posts);
    }
    
    @GetMapping("/{id}")
    public ApiResponse<Post> getPostById(@PathVariable Long id) {
        Post post = postService.findById(id);
        if (post == null) {
            return ApiResponse.error("文章不存在");
        }
        
        // 增加浏览次数
        postService.increaseViewCount(id);
        
        return ApiResponse.success("获取文章详情成功", post);
    }
    
    @GetMapping("/category/{categoryId}")
    public ApiResponse<List<Post>> getPostsByCategory(@PathVariable Long categoryId) {
        List<Post> posts = postService.findByCategoryId(categoryId);
        return ApiResponse.success("获取分类文章列表成功", posts);
    }
    
    @GetMapping("/tag/{tagId}")
    public ApiResponse<List<Post>> getPostsByTag(@PathVariable Long tagId) {
        List<Post> posts = postService.findByTagId(tagId);
        return ApiResponse.success("获取标签文章列表成功", posts);
    }
    
    @PostMapping
    public ApiResponse<Post> createPost(@RequestBody Post post) {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            return ApiResponse.error(401, "请先登录");
        }
        
        post.setAuthorId(currentUser.getId());
        post.setStatus(1); // 默认发布状态
        
        Post createdPost = postService.create(post, null);
        return ApiResponse.success("创建文章成功", createdPost);
    }
    
    @PutMapping("/{id}")
    public ApiResponse<Post> updatePost(@PathVariable Long id, @RequestBody Post post) {
        Post existingPost = postService.findById(id);
        if (existingPost == null) {
            return ApiResponse.error("文章不存在");
        }
        
        User currentUser = authService.getCurrentUser();
        if (currentUser == null || (!currentUser.getId().equals(existingPost.getAuthorId()) && !"ADMIN".equals(currentUser.getRole()))) {
            return ApiResponse.error(403, "没有权限修改此文章");
        }
        
        post.setId(id);
        post.setAuthorId(existingPost.getAuthorId()); // 不允许修改作者
        
        Post updatedPost = postService.update(post, null);
        return ApiResponse.success("更新文章成功", updatedPost);
    }
    
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deletePost(@PathVariable Long id) {
        Post existingPost = postService.findById(id);
        if (existingPost == null) {
            return ApiResponse.error("文章不存在");
        }
        
        User currentUser = authService.getCurrentUser();
        if (currentUser == null || (!currentUser.getId().equals(existingPost.getAuthorId()) && !"ADMIN".equals(currentUser.getRole()))) {
            return ApiResponse.error(403, "没有权限删除此文章");
        }
        
        postService.deleteById(id);
        return ApiResponse.success("删除文章成功", null);
    }
    
    @PostMapping("/{id}/like")
    public ApiResponse<Void> likePost(@PathVariable Long id) {
        Post post = postService.findById(id);
        if (post == null) {
            return ApiResponse.error("文章不存在");
        }
        
        postService.increaseLikeCount(id);
        return ApiResponse.success("点赞成功", null);
    }
}