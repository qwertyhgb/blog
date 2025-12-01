package com.example.blog.service;

import com.example.blog.entity.Tag;
import com.example.blog.mapper.TagMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {
    
    @Autowired
    private TagMapper tagMapper;
    
    public List<Tag> findAll() {
        return tagMapper.findAll();
    }
    
    public Tag findById(Long id) {
        return tagMapper.findById(id);
    }
    
    public Tag findByName(String name) {
        return tagMapper.findByName(name);
    }
    
    public List<Tag> findByPostId(Long postId) {
        return tagMapper.findByPostId(postId);
    }
    
    public Tag create(Tag tag) {
        // 检查标签名是否已存在
        if (findByName(tag.getName()) != null) {
            throw new RuntimeException("标签名已存在");
        }
        
        tagMapper.insert(tag);
        return tag;
    }
    
    public Tag update(Tag tag) {
        Tag existingTag = findById(tag.getId());
        if (existingTag == null) {
            throw new RuntimeException("标签不存在");
        }
        
        tagMapper.update(tag);
        return findById(tag.getId());
    }
    
    public void deleteById(Long id) {
        tagMapper.deleteById(id);
    }
}