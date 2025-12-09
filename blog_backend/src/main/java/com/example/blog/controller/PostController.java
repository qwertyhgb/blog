package com.example.blog.controller;

import com.example.blog.dto.ApiResponse;
import com.example.blog.dto.PageResult;
import com.example.blog.dto.PostRequest;
import com.example.blog.entity.Post;
import com.example.blog.entity.User;
import com.example.blog.service.AuthService;
import com.example.blog.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文章控制器
 * 
 * 处理文章相关的请求，包括文章的增删改查、点赞等
 */
@RestController
@RequestMapping("/posts")
public class PostController {
    
    @Autowired
    private PostService postService;
    
    @Autowired
    private AuthService authService;
    
    /**
     * 获取已发布文章列表
     * 
     * @param page 页码，默认为1
     * @param size 每页大小，默认为10
     * @param keyword 关键词搜索，可选
     * @return 分页文章列表
     */
    @GetMapping
    public ApiResponse<PageResult<Post>> getAllPosts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        PageResult<Post> posts = postService.findPublishedWithPage(page, size, keyword);
        return ApiResponse.success("获取文章列表成功", posts);
    }
    
    /**
     * 管理后台：获取所有文章（包括草稿）
     * 
     * @param page 页码，默认为1
     * @param size 每页大小，默认为10
     * @param keyword 关键词搜索，可选
     * @return 分页文章列表
     */
    @GetMapping("/admin")
    public ApiResponse<PageResult<Post>> getAdminPosts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            return ApiResponse.error(403, "没有权限访问");
        }
        PageResult<Post> posts = postService.findAllWithPage(page, size, keyword);
        return ApiResponse.success("获取文章列表成功", posts);
    }
    
    /**
     * 根据ID获取文章详情
     * 
     * @param id 文章ID
     * @return 文章详情
     */
    @GetMapping("/{id}")
    public ApiResponse<Post> getPostById(@PathVariable Long id) {
        Post post = postService.findById(id);
        if (post == null) {
            return ApiResponse.error("文章不存在");
        }
        
        // 检查文章状态：只有已发布的文章、作者本人或管理员可以查看
        User currentUser = authService.getCurrentUser();
        boolean isAuthor = currentUser != null && currentUser.getId().equals(post.getAuthorId());
        boolean isAdmin = currentUser != null && "ADMIN".equals(currentUser.getRole());
        boolean isPublished = post.getStatus() != null && post.getStatus() == 1;
        
        if (!isPublished && !isAuthor && !isAdmin) {
            return ApiResponse.error(403, "该文章暂未发布");
        }
        
        // 增加浏览次数（只对已发布的文章）
        if (isPublished) {
            postService.increaseViewCount(id);
        }
        
        return ApiResponse.success("获取文章详情成功", post);
    }
    
    /**
     * 根据分类ID获取文章列表
     * 
     * @param categoryId 分类ID
     * @return 文章列表
     */
    @GetMapping("/category/{categoryId}")
    public ApiResponse<List<Post>> getPostsByCategory(@PathVariable Long categoryId) {
        List<Post> posts = postService.findByCategoryId(categoryId);
        return ApiResponse.success("获取分类文章列表成功", posts);
    }
    
    /**
     * 根据标签ID获取文章列表
     * 
     * @param tagId 标签ID
     * @return 文章列表
     */
    @GetMapping("/tag/{tagId}")
    public ApiResponse<List<Post>> getPostsByTag(@PathVariable Long tagId) {
        List<Post> posts = postService.findByTagId(tagId);
        return ApiResponse.success("获取标签文章列表成功", posts);
    }
    
    /**
     * 创建新文章
     * 
     * @param postRequest 文章请求对象
     * @return 创建的文章
     */
    @PostMapping
    public ApiResponse<Post> createPost(@RequestBody PostRequest postRequest) {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            return ApiResponse.error(401, "请先登录");
        }
        
        Post post = new Post();
        post.setTitle(postRequest.getTitle());
        post.setSummary(postRequest.getSummary());
        post.setContent(postRequest.getContent());
        post.setCoverImage(postRequest.getCoverImage());
        post.setCategoryId(postRequest.getCategoryId());
        post.setAuthorId(currentUser.getId());
        post.setStatus(postRequest.getStatus() != null ? postRequest.getStatus() : 1);
        
        Post createdPost = postService.create(post, postRequest.getTagIds());
        return ApiResponse.success("创建文章成功", createdPost);
    }
    
    /**
     * 更新文章
     * 
     * @param id 文章ID
     * @param postRequest 文章请求对象
     * @return 更新后的文章
     */
    @PutMapping("/{id}")
    public ApiResponse<Post> updatePost(@PathVariable Long id, @RequestBody PostRequest postRequest) {
        Post existingPost = postService.findById(id);
        if (existingPost == null) {
            return ApiResponse.error("文章不存在");
        }
        
        User currentUser = authService.getCurrentUser();
        if (currentUser == null || (!currentUser.getId().equals(existingPost.getAuthorId()) && !"ADMIN".equals(currentUser.getRole()))) {
            return ApiResponse.error(403, "没有权限修改此文章");
        }
        
        Post post = new Post();
        post.setId(id);
        post.setTitle(postRequest.getTitle());
        post.setSummary(postRequest.getSummary());
        post.setContent(postRequest.getContent());
        post.setCoverImage(postRequest.getCoverImage());
        post.setCategoryId(postRequest.getCategoryId());
        post.setAuthorId(existingPost.getAuthorId());
        if (postRequest.getStatus() != null) {
            post.setStatus(postRequest.getStatus());
        }
        
        Post updatedPost = postService.update(post, postRequest.getTagIds());
        return ApiResponse.success("更新文章成功", updatedPost);
    }
    
    /**
     * 删除文章
     * 
     * @param id 文章ID
     * @return 删除结果
     */
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
    
    /**
     * 点赞文章
     * 
     * @param id 文章ID
     * @return 点赞结果
     */
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