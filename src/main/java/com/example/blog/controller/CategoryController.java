package com.example.blog.controller;

import com.example.blog.dto.ApiResponse;
import com.example.blog.entity.Category;
import com.example.blog.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    
    @Autowired
    private CategoryService categoryService;
    
    @GetMapping
    public ApiResponse<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.findAll();
        return ApiResponse.success("获取分类列表成功", categories);
    }
    
    @GetMapping("/{id}")
    public ApiResponse<Category> getCategoryById(@PathVariable Long id) {
        Category category = categoryService.findById(id);
        if (category == null) {
            return ApiResponse.error("分类不存在");
        }
        return ApiResponse.success("获取分类详情成功", category);
    }
    
    @PostMapping
    public ApiResponse<Category> createCategory(@RequestBody Category category) {
        Category createdCategory = categoryService.create(category);
        return ApiResponse.success("创建分类成功", createdCategory);
    }
    
    @PutMapping("/{id}")
    public ApiResponse<Category> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        Category existingCategory = categoryService.findById(id);
        if (existingCategory == null) {
            return ApiResponse.error("分类不存在");
        }
        
        category.setId(id);
        Category updatedCategory = categoryService.update(category);
        return ApiResponse.success("更新分类成功", updatedCategory);
    }
    
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCategory(@PathVariable Long id) {
        Category existingCategory = categoryService.findById(id);
        if (existingCategory == null) {
            return ApiResponse.error("分类不存在");
        }
        
        categoryService.deleteById(id);
        return ApiResponse.success("删除分类成功", null);
    }
}