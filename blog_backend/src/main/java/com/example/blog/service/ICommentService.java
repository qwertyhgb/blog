package com.example.blog.service;

import com.example.blog.dto.PageResult;
import com.example.blog.entity.Comment;

import java.util.List;

/**
 * 评论服务接口
 */
public interface ICommentService {

    /**
     * 根据文章ID查询评论
     * 
     * @param postId 文章ID
     * @return 评论列表
     */
    List<Comment> findByPostId(Long postId);

    /**
     * 根据用户ID查询评论
     * 
     * @param userId 用户ID
     * @return 评论列表
     */
    List<Comment> findByUserId(Long userId);

    /**
     * 根据ID查询评论
     * 
     * @param id 评论ID
     * @return 评论
     */
    Comment findById(Long id);

    /**
     * 根据父评论ID查询子评论
     * 
     * @param parentId 父评论ID
     * @return 子评论列表
     */
    List<Comment> findByParentId(Long parentId);

    /**
     * 分页查询所有评论（管理后台）
     * 
     * @param page   页码
     * @param size   每页大小
     * @param status 状态筛选
     * @return 分页结果
     */
    PageResult<Comment> findAllWithPage(Integer page, Integer size, Integer status);

    /**
     * 创建评论
     * 
     * @param comment 评论信息
     * @return 创建的评论
     */
    Comment create(Comment comment);

    /**
     * 更新评论
     * 
     * @param comment 评论信息
     * @return 更新后的评论
     */
    Comment update(Comment comment);

    /**
     * 删除评论
     * 
     * @param id 评论ID
     */
    void deleteById(Long id);

    /**
     * 审核通过评论
     * 
     * @param id 评论ID
     * @return 更新后的评论
     */
    Comment approve(Long id);

    /**
     * 驳回评论
     * 
     * @param id 评论ID
     * @return 更新后的评论
     */
    Comment reject(Long id);
}
