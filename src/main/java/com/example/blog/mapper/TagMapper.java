package com.example.blog.mapper;

import com.example.blog.entity.Tag;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TagMapper {
    
    List<Tag> findAll();
    
    Tag findById(Long id);
    
    Tag findByName(String name);
    
    int insert(Tag tag);
    
    int update(Tag tag);
    
    int deleteById(Long id);
    
    List<Tag> findByPostId(Long postId);
}