package com.example.blog.controller;

import com.example.blog.dto.ApiResponse;
import com.example.blog.entity.Category;
import com.example.blog.service.ICategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类控制器
 *
 * <p>
 * 提供分类的列表查询、详情、创建、更新、删除等接口，主要面向后台管理场景。
 * </p>
 */
@RestController
@RequestMapping("/categories")
@Tag(name = "分类管理", description = "文章分类的增删改查接口")
public class CategoryController {

    @Autowired
    private ICategoryService categoryService;

    @Operation(summary = "获取所有分类", description = "获取所有文章分类列表")
    @GetMapping
    public ApiResponse<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.findAll();
        return ApiResponse.success("获取分类列表成功", categories);
    }

    @Operation(summary = "获取分类详情", description = "根据ID获取分类详情")
    @GetMapping("/{id}")
    public ApiResponse<Category> getCategoryById(
            @Parameter(description = "分类ID") @PathVariable Long id) {
        Category category = categoryService.findById(id);
        if (category == null) {
            return ApiResponse.error("分类不存在");
        }
        return ApiResponse.success("获取分类详情成功", category);
    }

    @Operation(summary = "创建分类", description = "创建新的文章分类")
    @PostMapping
    public ApiResponse<Category> createCategory(@RequestBody Category category) {
        // CategoryService 内部负责设置必要的默认值及排序等逻辑
        Category createdCategory = categoryService.create(category);
        return ApiResponse.success("创建分类成功", createdCategory);
    }

    @Operation(summary = "更新分类", description = "更新指定分类的信息")
    @PutMapping("/{id}")
    public ApiResponse<Category> updateCategory(
            @Parameter(description = "分类ID") @PathVariable Long id,
            @RequestBody Category category) {
        Category existingCategory = categoryService.findById(id);
        if (existingCategory == null) {
            return ApiResponse.error("分类不存在");
        }

        // 先校验存在性，再将路径参数写回实体，防止客户端伪造ID
        category.setId(id);
        Category updatedCategory = categoryService.update(category);
        return ApiResponse.success("更新分类成功", updatedCategory);
    }

    @Operation(summary = "删除分类", description = "删除指定的分类")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCategory(
            @Parameter(description = "分类ID") @PathVariable Long id) {
        Category existingCategory = categoryService.findById(id);
        if (existingCategory == null) {
            return ApiResponse.error("分类不存在");
        }

        categoryService.deleteById(id);
        return ApiResponse.success("删除分类成功", null);
    }
}