package com.example.blog.service;

import com.example.blog.dto.PageResult;
import com.example.blog.entity.Comment;
import com.example.blog.mapper.CommentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService implements ICommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Override
    public List<Comment> findByPostId(Long postId) {
        return commentMapper.findByPostId(postId);
    }

    @Override
    public List<Comment> findByUserId(Long userId) {
        return commentMapper.findByUserId(userId);
    }

    @Override
    public Comment findById(Long id) {
        return commentMapper.findById(id);
    }

    @Override
    public List<Comment> findByParentId(Long parentId) {
        return commentMapper.findByParentId(parentId);
    }

    @Override
    public PageResult<Comment> findAllWithPage(Integer page, Integer size, Integer status) {
        int offset = (page - 1) * size;
        List<Comment> comments = commentMapper.findAllWithPage(offset, size, status);
        Long total = commentMapper.countAll(status);
        return PageResult.of(comments, total, page, size);
    }

    @Override
    public Comment create(Comment comment) {
        // 默认状态为待审核
        if (comment.getStatus() == null) {
            comment.setStatus(0);
        }

        commentMapper.insert(comment);
        return comment;
    }

    @Override
    public Comment update(Comment comment) {
        Comment existingComment = findById(comment.getId());
        if (existingComment == null) {
            throw new RuntimeException("评论不存在");
        }

        commentMapper.update(comment);
        return findById(comment.getId());
    }

    @Override
    public void deleteById(Long id) {
        commentMapper.deleteById(id);
    }

    @Override
    public Comment approve(Long id) {
        Comment comment = findById(id);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }

        comment.setStatus(1);
        commentMapper.update(comment);
        return comment;
    }

    @Override
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