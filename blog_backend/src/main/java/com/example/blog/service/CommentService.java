package com.example.blog.service;

import com.example.blog.dto.PageResult;
import com.example.blog.entity.Comment;
import com.example.blog.mapper.CommentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    
    @Autowired
    private CommentMapper commentMapper;
    
    public List<Comment> findByPostId(Long postId) {
        return commentMapper.findByPostId(postId);
    }
    
    public List<Comment> findByUserId(Long userId) {
        return commentMapper.findByUserId(userId);
    }
    
    public Comment findById(Long id) {
        return commentMapper.findById(id);
    }
    
    public List<Comment> findByParentId(Long parentId) {
        return commentMapper.findByParentId(parentId);
    }
    
    /**
     * 管理后台：分页查询所有评论
     */
    public PageResult<Comment> findAllWithPage(Integer page, Integer size, Integer status) {
        int offset = (page - 1) * size;
        List<Comment> comments = commentMapper.findAllWithPage(offset, size, status);
        Long total = commentMapper.countAll(status);
        return PageResult.of(comments, total, page, size);
    }
    
    public Comment create(Comment comment) {
        // 默认状态为待审核
        if (comment.getStatus() == null) {
            comment.setStatus(0);
        }
        
        commentMapper.insert(comment);
        return comment;
    }
    
    public Comment update(Comment comment) {
        Comment existingComment = findById(comment.getId());
        if (existingComment == null) {
            throw new RuntimeException("评论不存在");
        }
        
        commentMapper.update(comment);
        return findById(comment.getId());
    }
    
    public void deleteById(Long id) {
        commentMapper.deleteById(id);
    }
    
    public Comment approve(Long id) {
        Comment comment = findById(id);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }
        
        comment.setStatus(1);
        commentMapper.update(comment);
        return comment;
    }
    
    public Comment reject(Long id) {
        Comment comment = findById(id);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }
        
        comment.setStatus(2);
        commentMapper.update(comment);
        return comment;
    }
}