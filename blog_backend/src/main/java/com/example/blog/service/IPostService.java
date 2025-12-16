package com.example.blog.service;

import com.example.blog.dto.PageResult;
import com.example.blog.entity.Post;

import java.util.List;

/**
 * 文章服务接口
 */
public interface IPostService {

    /**
     * 查询所有文章
     * 
     * @return 文章列表
     */
    List<Post> findAll();

    /**
     * 查询已发布的文章
     * 
     * @return 已发布文章列表
     */
    List<Post> findPublished();

    /**
     * 分页查询已发布文章
     * 
     * @param page    页码
     * @param size    每页大小
     * @param keyword 关键词
     * @return 分页结果
     */
    PageResult<Post> findPublishedWithPage(Integer page, Integer size, String keyword);

    /**
     * 分页查询所有文章（管理后台）
     * 
     * @param page    页码
     * @param size    每页大小
     * @param keyword 关键词
     * @return 分页结果
     */
    PageResult<Post> findAllWithPage(Integer page, Integer size, String keyword);

    /**
     * 根据ID查询文章
     * 
     * @param id 文章ID
     * @return 文章
     */
    Post findById(Long id);

    /**
     * 根据作者ID查询文章
     * 
     * @param authorId 作者ID
     * @return 文章列表
     */
    List<Post> findByAuthorId(Long authorId);

    /**
     * 根据分类ID查询文章
     * 
     * @param categoryId 分类ID
     * @return 文章列表
     */
    List<Post> findByCategoryId(Long categoryId);

    /**
     * 根据标签ID查询文章
     * 
     * @param tagId 标签ID
     * @return 文章列表
     */
    List<Post> findByTagId(Long tagId);

    /**
     * 创建文章
     * 
     * @param post   文章信息
     * @param tagIds 标签ID列表
     * @return 创建的文章
     */
    Post create(Post post, List<Long> tagIds);

    /**
     * 更新文章
     * 
     * @param post   文章信息
     * @param tagIds 标签ID列表
     * @return 更新后的文章
     */
    Post update(Post post, List<Long> tagIds);

    /**
     * 删除文章（软删除）
     * 
     * @param id 文章ID
     */
    void deleteById(Long id);

    /**
     * 增加浏览次数
     * 
     * @param id 文章ID
     */
    void increaseViewCount(Long id);

    /**
     * 增加点赞次数
     * 
     * @param id 文章ID
     */
    void increaseLikeCount(Long id);
}
