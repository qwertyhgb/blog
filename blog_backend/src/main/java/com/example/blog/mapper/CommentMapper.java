package com.example.blog.mapper;

import com.example.blog.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper {
    
    List<Comment> findByPostId(@Param("postId") Long postId);
    
    List<Comment> findByUserId(@Param("userId") Long userId);
    
    Comment findById(@Param("id") Long id);
    
    List<Comment> findByParentId(@Param("parentId") Long parentId);
    
    // 管理后台：分页查询所有评论
    List<Comment> findAllWithPage(@Param("offset") Integer offset, 
                                   @Param("size") Integer size,
                                   @Param("status") Integer status);
    
    Long countAll(@Param("status") Integer status);
    
    int insert(Comment comment);
    
    int update(Comment comment);
    
    int deleteById(@Param("id") Long id);
}