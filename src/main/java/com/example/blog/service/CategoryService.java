package com.example.blog.service;

import com.example.blog.entity.Category;
import com.example.blog.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    
    @Autowired
    private CategoryMapper categoryMapper;
    
    public List<Category> findAll() {
        return categoryMapper.findAll();
    }
    
    public Category findById(Long id) {
        return categoryMapper.findById(id);
    }
    
    public Category findByName(String name) {
        return categoryMapper.findByName(name);
    }
    
    public Category create(Category category) {
        // 检查分类名是否已存在
        if (findByName(category.getName()) != null) {
            throw new RuntimeException("分类名已存在");
        }
        
        categoryMapper.insert(category);
        return category;
    }
    
    public Category update(Category category) {
        Category existingCategory = findById(category.getId());
        if (existingCategory == null) {
            throw new RuntimeException("分类不存在");
        }
        
        categoryMapper.update(category);
        return findById(category.getId());
    }
    
    public void deleteById(Long id) {
        categoryMapper.deleteById(id);
    }
}