package com.example.blog.controller;

import com.example.blog.dto.ApiResponse;
import com.example.blog.dto.PageResult;
import com.example.blog.dto.PostRequest;
import com.example.blog.entity.Post;
import com.example.blog.entity.User;
import com.example.blog.service.IAuthService;
import com.example.blog.service.IPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 文章控制器
 *
 * <p>
 * 该类负责处理所有与文章相关的 HTTP 请求，包括：
 * <ul>
 *     <li>公开文章列表、详情的查询</li>
 *     <li>后台文章的管理（分页、草稿、删除）</li>
 *     <li>文章的创建、更新、删除、点赞</li>
 * </ul>
 * 控制器中所有返回值都使用统一的 {@link ApiResponse} 进行封装，便于前端消费。
 * </p>
 */
@RestController
@RequestMapping("/posts")
@Tag(name = "文章管理", description = "文章的增删改查、点赞等接口")
public class PostController {

    @Autowired
    private IPostService postService;

    @Autowired
    private IAuthService authService;

    @Operation(summary = "获取文章列表", description = "分页获取已发布的文章列表，支持关键词搜索")
    @GetMapping
    public ApiResponse<PageResult<Post>> getAllPosts(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword) {
        // 仅查询已发布状态（status=1）的文章
        PageResult<Post> posts = postService.findPublishedWithPage(
                page,
                size,
                keyword);
        return ApiResponse.success("获取文章列表成功", posts);
    }

    @Operation(summary = "管理后台获取文章列表", description = "分页获取所有文章（包括草稿）")
    @GetMapping("/admin")
    public ApiResponse<PageResult<Post>> getAdminPosts(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword) {
        // 管理端接口必须检测当前用户是否为管理员
        User currentUser = authService.getCurrentUser();
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            return ApiResponse.error(403, "没有权限访问");
        }
        // 管理员可以查看所有文章，包括草稿、下架等状态
        PageResult<Post> posts = postService.findAllWithPage(
                page,
                size,
                keyword);
        return ApiResponse.success("获取文章列表成功", posts);
    }

    @Operation(summary = "获取文章详情", description = "根据ID获取文章详情")
    @GetMapping("/{id}")
    public ApiResponse<Post> getPostById(
            @Parameter(description = "文章ID") @PathVariable Long id) {
        Post post = postService.findById(id);
        if (post == null) {
            return ApiResponse.error("文章不存在");
        }

        // 检查文章状态：只有已发布的文章、作者本人或管理员可以查看
        User currentUser = authService.getCurrentUser();
        boolean isAuthor = currentUser != null &&
                currentUser.getId().equals(post.getAuthorId());
        boolean isAdmin = currentUser != null && "ADMIN".equals(currentUser.getRole());
        // status: 1=已发布，0=草稿，2=下架
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

    @Operation(summary = "获取分类文章", description = "根据分类ID获取文章列表")
    @GetMapping("/category/{categoryId}")
    public ApiResponse<List<Post>> getPostsByCategory(
            @Parameter(description = "分类ID") @PathVariable Long categoryId) {
        List<Post> posts = postService.findByCategoryId(categoryId);
        return ApiResponse.success("获取分类文章列表成功", posts);
    }

    @Operation(summary = "获取标签文章", description = "根据标签ID获取文章列表")
    @GetMapping("/tag/{tagId}")
    public ApiResponse<List<Post>> getPostsByTag(
            @Parameter(description = "标签ID") @PathVariable Long tagId) {
        List<Post> posts = postService.findByTagId(tagId);
        return ApiResponse.success("获取标签文章列表成功", posts);
    }

    @Operation(summary = "创建文章", description = "创建新文章")
    @PostMapping
    public ApiResponse<Post> createPost(
            @Valid @RequestBody PostRequest postRequest) {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            return ApiResponse.error(401, "请先登录");
        }

        // DTO -> 实体映射：只拷贝允许设置的字段，避免前端传入非法属性
        Post post = new Post();
        post.setTitle(postRequest.getTitle());
        post.setSummary(postRequest.getSummary());
        post.setContent(postRequest.getContent());
        post.setCoverImage(postRequest.getCoverImage());
        post.setCategoryId(postRequest.getCategoryId());
        post.setAuthorId(currentUser.getId());
        post.setStatus(
                postRequest.getStatus() != null ? postRequest.getStatus() : 1);

        Post createdPost = postService.create(post, postRequest.getTagIds());
        return ApiResponse.success("创建文章成功", createdPost);
    }

    @Operation(summary = "更新文章", description = "更新指定文章")
    @PutMapping("/{id}")
    public ApiResponse<Post> updatePost(
            @Parameter(description = "文章ID") @PathVariable Long id,
            @Valid @RequestBody PostRequest postRequest) {
        Post existingPost = postService.findById(id);
        if (existingPost == null) {
            return ApiResponse.error("文章不存在");
        }

        User currentUser = authService.getCurrentUser();
        if (currentUser == null ||
                (!currentUser.getId().equals(existingPost.getAuthorId()) &&
                        !"ADMIN".equals(currentUser.getRole()))) {
            return ApiResponse.error(403, "没有权限修改此文章");
        }

        // 仅允许更新白名单字段，避免覆盖作者、删除标志等敏感信息
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

    @Operation(summary = "删除文章", description = "删除指定文章（软删除）")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deletePost(
            @Parameter(description = "文章ID") @PathVariable Long id) {
        Post existingPost = postService.findById(id);
        if (existingPost == null) {
            return ApiResponse.error("文章不存在");
        }

        User currentUser = authService.getCurrentUser();
        if (currentUser == null ||
                (!currentUser.getId().equals(existingPost.getAuthorId()) &&
                        !"ADMIN".equals(currentUser.getRole()))) {
            return ApiResponse.error(403, "没有权限删除此文章");
        }

        // 业务上采用软删除，记录不会从数据库物理删除，便于后续恢复
        postService.deleteById(id);
        return ApiResponse.success("删除文章成功", null);
    }

    @Operation(summary = "点赞文章", description = "为文章点赞")
    @PostMapping("/{id}/like")
    public ApiResponse<Void> likePost(
            @Parameter(description = "文章ID") @PathVariable Long id) {
        Post post = postService.findById(id);
        if (post == null) {
            return ApiResponse.error("文章不存在");
        }

        // 直接累计点赞，不区分是否重复，前端可结合用户行为做限制
        postService.increaseLikeCount(id);
        return ApiResponse.success("点赞成功", null);
    }
}
