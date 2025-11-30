package com.example.blog.mapper;

import com.example.blog.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostMapper {
    
    List<Post> findAll();
    
    List<Post> findByStatus(@Param("status") Integer status);
    
    Post findById(@Param("id") Long id);
    
    List<Post> findByAuthorId(@Param("authorId") Long authorId);
    
    List<Post> findByCategoryId(@Param("categoryId") Long categoryId);
    
    List<Post> findByTagId(@Param("tagId") Long tagId);
    
    int insert(Post post);
    
    int update(Post post);
    
    int deleteById(@Param("id") Long id);
    
    int increaseViewCount(@Param("id") Long id);
    
    int increaseLikeCount(@Param("id") Long id);
}