package com.example.blog.controller;

import com.example.blog.dto.ApiResponse;
import com.example.blog.dto.PageResult;
import com.example.blog.entity.Comment;
import com.example.blog.entity.User;
import com.example.blog.service.IAuthService;
import com.example.blog.service.ICommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评论控制器
 *
 * <p>
 * 提供评论的查询、创建、修改、删除以及管理员审核功能，既服务于前台用户互动，
 * 也服务于后台审核流程。
 * </p>
 */
@RestController
@RequestMapping("/comments")
@Tag(name = "评论管理", description = "文章评论的增删改查及审核接口")
public class CommentController {

    @Autowired
    private ICommentService commentService;

    @Autowired
    private IAuthService authService;

    @Operation(summary = "管理后台获取评论列表", description = "分页获取所有评论，支持状态筛选")
    @GetMapping("/admin")
    public ApiResponse<PageResult<Comment>> getAdminComments(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "评论状态") @RequestParam(required = false) Integer status) {
        // 仅管理员可查看全量评论；普通用户只能查询自己的评论
        User currentUser = authService.getCurrentUser();
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            return ApiResponse.error(403, "没有权限访问");
        }

        PageResult<Comment> comments = commentService.findAllWithPage(page, size, status);
        return ApiResponse.success("获取评论列表成功", comments);
    }

    @Operation(summary = "获取文章评论", description = "根据文章ID获取评论列表")
    @GetMapping("/post/{postId}")
    public ApiResponse<List<Comment>> getCommentsByPostId(
            @Parameter(description = "文章ID") @PathVariable Long postId) {
        List<Comment> comments = commentService.findByPostId(postId);
        return ApiResponse.success("获取评论列表成功", comments);
    }

    @Operation(summary = "获取评论详情", description = "根据ID获取评论详情")
    @GetMapping("/{id}")
    public ApiResponse<Comment> getCommentById(
            @Parameter(description = "评论ID") @PathVariable Long id) {
        Comment comment = commentService.findById(id);
        if (comment == null) {
            return ApiResponse.error("评论不存在");
        }

        return ApiResponse.success("获取评论详情成功", comment);
    }

    @Operation(summary = "发表评论", description = "发表新评论")
    @PostMapping
    public ApiResponse<Comment> createComment(@RequestBody Comment comment) {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            return ApiResponse.error(401, "请先登录");
        }

        // 评论与当前登录用户绑定，初始状态设为待审核
        comment.setUserId(currentUser.getId());
        comment.setStatus(1); // 默认待审核状态

        Comment createdComment = commentService.create(comment);

        return ApiResponse.success("创建评论成功", createdComment);
    }

    @Operation(summary = "更新评论", description = "更新评论内容")
    @PutMapping("/{id}")
    public ApiResponse<Comment> updateComment(
            @Parameter(description = "评论ID") @PathVariable Long id,
            @RequestBody Comment comment) {
        Comment existingComment = commentService.findById(id);
        if (existingComment == null) {
            return ApiResponse.error("评论不存在");
        }

        User currentUser = authService.getCurrentUser();
        if (currentUser == null || (!currentUser.getId().equals(existingComment.getUserId())
                && !"ADMIN".equals(currentUser.getRole()))) {
            return ApiResponse.error(403, "没有权限修改此评论");
        }

        // 更新操作仅允许变更内容，不允许修改归属与审核状态
        comment.setId(id);
        comment.setUserId(existingComment.getUserId()); // 不允许修改用户
        comment.setStatus(existingComment.getStatus()); // 不允许修改状态

        Comment updatedComment = commentService.update(comment);
        return ApiResponse.success("更新评论成功", updatedComment);
    }

    @Operation(summary = "删除评论", description = "删除指定评论")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteComment(
            @Parameter(description = "评论ID") @PathVariable Long id) {
        Comment existingComment = commentService.findById(id);
        if (existingComment == null) {
            return ApiResponse.error("评论不存在");
        }

        User currentUser = authService.getCurrentUser();
        if (currentUser == null || (!currentUser.getId().equals(existingComment.getUserId())
                && !"ADMIN".equals(currentUser.getRole()))) {
            return ApiResponse.error(403, "没有权限删除此评论");
        }

        // 删除为物理删除，如需软删除可在 Service 中扩展
        commentService.deleteById(id);
        return ApiResponse.success("删除评论成功", null);
    }

    @Operation(summary = "审核通过评论", description = "管理员审核通过评论")
    @PostMapping("/{id}/approve")
    public ApiResponse<Void> approveComment(
            @Parameter(description = "评论ID") @PathVariable Long id) {
        Comment existingComment = commentService.findById(id);
        if (existingComment == null) {
            return ApiResponse.error("评论不存在");
        }

        // 审核操作仅允许管理员执行
        User currentUser = authService.getCurrentUser();
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            return ApiResponse.error(403, "没有权限审核评论");
        }

        commentService.approve(id);
        return ApiResponse.success("审核通过评论成功", null);
    }

    @Operation(summary = "驳回评论", description = "管理员驳回评论")
    @PostMapping("/{id}/reject")
    public ApiResponse<Void> rejectComment(
            @Parameter(description = "评论ID") @PathVariable Long id) {
        Comment existingComment = commentService.findById(id);
        if (existingComment == null) {
            return ApiResponse.error("评论不存在");
        }

        // 审核驳回逻辑与通过保持一致，后续可在 Service 中补充驳回原因
        User currentUser = authService.getCurrentUser();
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            return ApiResponse.error(403, "没有权限审核评论");
        }

        commentService.reject(id);
        return ApiResponse.success("驳回评论成功", null);
    }
}