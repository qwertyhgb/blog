package com.example.blog.service;

import com.example.blog.entity.Category;

import java.util.List;

/**
 * 分类服务接口
 */
public interface ICategoryService {

    /**
     * 查询所有分类
     * 
     * @return 分类列表
     */
    List<Category> findAll();

    /**
     * 根据ID查询分类
     * 
     * @param id 分类ID
     * @return 分类
     */
    Category findById(Long id);

    /**
     * 根据名称查询分类
     * 
     * @param name 分类名称
     * @return 分类
     */
    Category findByName(String name);

    /**
     * 创建分类
     * 
     * @param category 分类信息
     * @return 创建的分类
     */
    Category create(Category category);

    /**
     * 更新分类
     * 
     * @param category 分类信息
     * @return 更新后的分类
     */
    Category update(Category category);

    /**
     * 删除分类
     * 
     * @param id 分类ID
     */
    void deleteById(Long id);
}
