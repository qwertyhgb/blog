package com.example.blog.service;

import com.example.blog.entity.Tag;

import java.util.List;

/**
 * 标签服务接口
 */
public interface ITagService {

    /**
     * 查询所有标签
     * 
     * @return 标签列表
     */
    List<Tag> findAll();

    /**
     * 根据ID查询标签
     * 
     * @param id 标签ID
     * @return 标签
     */
    Tag findById(Long id);

    /**
     * 根据名称查询标签
     * 
     * @param name 标签名称
     * @return 标签
     */
    Tag findByName(String name);

    /**
     * 根据文章ID查询标签
     * 
     * @param postId 文章ID
     * @return 标签列表
     */
    List<Tag> findByPostId(Long postId);

    /**
     * 创建标签
     * 
     * @param tag 标签信息
     * @return 创建的标签
     */
    Tag create(Tag tag);

    /**
     * 更新标签
     * 
     * @param tag 标签信息
     * @return 更新后的标签
     */
    Tag update(Tag tag);

    /**
     * 删除标签
     * 
     * @param id 标签ID
     */
    void deleteById(Long id);
}
