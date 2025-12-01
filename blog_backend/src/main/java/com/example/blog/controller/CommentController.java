package com.example.blog.controller;

import com.example.blog.dto.ApiResponse;
import com.example.blog.dto.PageResult;
import com.example.blog.entity.Comment;
import com.example.blog.entity.User;
import com.example.blog.service.AuthService;
import com.example.blog.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {
    
    @Autowired
    private CommentService commentService;
    
    @Autowired
    private AuthService authService;
    
    /**
     * 管理后台：获取所有评论（分页）
     */
    @GetMapping("/admin")
    public ApiResponse<PageResult<Comment>> getAdminComments(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            return ApiResponse.error(403, "没有权限访问");
        }
        PageResult<Comment> comments = commentService.findAllWithPage(page, size, status);
        return ApiResponse.success("获取评论列表成功", comments);
    }
    
    @GetMapping("/post/{postId}")
    public ApiResponse<List<Comment>> getCommentsByPostId(@PathVariable Long postId) {
        List<Comment> comments = commentService.findByPostId(postId);
        return ApiResponse.success("获取评论列表成功", comments);
    }
    
    @GetMapping("/{id}")
    public ApiResponse<Comment> getCommentById(@PathVariable Long id) {
        Comment comment = commentService.findById(id);
        if (comment == null) {
            return ApiResponse.error("评论不存在");
        }
        return ApiResponse.success("获取评论详情成功", comment);
    }
    
    @PostMapping
    public ApiResponse<Comment> createComment(@RequestBody Comment comment) {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            return ApiResponse.error(401, "请先登录");
        }
        
        comment.setUserId(currentUser.getId());
        comment.setStatus(1); // 默认待审核状态
        
        Comment createdComment = commentService.create(comment);
        return ApiResponse.success("创建评论成功", createdComment);
    }
    
    @PutMapping("/{id}")
    public ApiResponse<Comment> updateComment(@PathVariable Long id, @RequestBody Comment comment) {
        Comment existingComment = commentService.findById(id);
        if (existingComment == null) {
            return ApiResponse.error("评论不存在");
        }
        
        User currentUser = authService.getCurrentUser();
        if (currentUser == null || (!currentUser.getId().equals(existingComment.getUserId()) && !"ADMIN".equals(currentUser.getRole()))) {
            return ApiResponse.error(403, "没有权限修改此评论");
        }
        
        comment.setId(id);
        comment.setUserId(existingComment.getUserId()); // 不允许修改用户
        comment.setStatus(existingComment.getStatus()); // 不允许修改状态
        
        Comment updatedComment = commentService.update(comment);
        return ApiResponse.success("更新评论成功", updatedComment);
    }
    
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteComment(@PathVariable Long id) {
        Comment existingComment = commentService.findById(id);
        if (existingComment == null) {
            return ApiResponse.error("评论不存在");
        }
        
        User currentUser = authService.getCurrentUser();
        if (currentUser == null || (!currentUser.getId().equals(existingComment.getUserId()) && !"ADMIN".equals(currentUser.getRole()))) {
            return ApiResponse.error(403, "没有权限删除此评论");
        }
        
        commentService.deleteById(id);
        return ApiResponse.success("删除评论成功", null);
    }
    
    @PostMapping("/{id}/approve")
    public ApiResponse<Void> approveComment(@PathVariable Long id) {
        Comment existingComment = commentService.findById(id);
        if (existingComment == null) {
            return ApiResponse.error("评论不存在");
        }
        
        User currentUser = authService.getCurrentUser();
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            return ApiResponse.error(403, "没有权限审核评论");
        }
        
        commentService.approve(id);
        return ApiResponse.success("审核通过评论成功", null);
    }
    
    @PostMapping("/{id}/reject")
    public ApiResponse<Void> rejectComment(@PathVariable Long id) {
        Comment existingComment = commentService.findById(id);
        if (existingComment == null) {
            return ApiResponse.error("评论不存在");
        }
        
        User currentUser = authService.getCurrentUser();
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            return ApiResponse.error(403, "没有权限审核评论");
        }
        
        commentService.reject(id);
        return ApiResponse.success("驳回评论成功", null);
    }
}