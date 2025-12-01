package com.example.blog.service;

import com.example.blog.dto.PageResult;
import com.example.blog.entity.Post;
import com.example.blog.entity.PostTag;
import com.example.blog.mapper.PostMapper;
import com.example.blog.mapper.PostTagMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class PostService {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private PostTagMapper postTagMapper;

    public List<Post> findAll() {
        return postMapper.findAll();
    }

    public List<Post> findPublished() {
        return postMapper.findByStatus(1);
    }
    
    /**
     * 分页查询已发布文章
     */
    public PageResult<Post> findPublishedWithPage(Integer page, Integer size, String keyword) {
        int offset = (page - 1) * size;
        List<Post> posts = postMapper.findByStatusWithPage(1, offset, size, keyword);
        Long total = postMapper.countByStatus(1, keyword);
        return PageResult.of(posts, total, page, size);
    }
    
    /**
     * 管理后台：分页查询所有文章
     */
    public PageResult<Post> findAllWithPage(Integer page, Integer size, String keyword) {
        int offset = (page - 1) * size;
        List<Post> posts = postMapper.findAllWithPage(offset, size, keyword);
        Long total = postMapper.countAll(keyword);
        return PageResult.of(posts, total, page, size);
    }

    public Post findById(Long id) {
        return postMapper.findById(id);
    }

    public List<Post> findByAuthorId(Long authorId) {
        return postMapper.findByAuthorId(authorId);
    }

    public List<Post> findByCategoryId(Long categoryId) {
        return postMapper.findByCategoryId(categoryId);
    }

    public List<Post> findByTagId(Long tagId) {
        return postMapper.findByTagId(tagId);
    }

    @Transactional
    public Post create(Post post, List<Long> tagIds) {
        // 设置默认值
        if (post.getIsDeleted() == null) {
            post.setIsDeleted(0);
        }
        if (post.getIsTop() == null) {
            post.setIsTop(0);
        }
        // 如果是发布状态，设置发布时间
        if (post.getStatus() != null && post.getStatus() == 1) {
            post.setPublishedTime(java.time.LocalDateTime.now());
        }
        
        postMapper.insert(post);

        // 关联标签
        if (tagIds != null && !tagIds.isEmpty()) {
            List<PostTag> postTags = new ArrayList<>();
            for (Long tagId : tagIds) {
                PostTag postTag = new PostTag();
                postTag.setPostId(post.getId());
                postTag.setTagId(tagId);
                postTags.add(postTag);
            }
            postTagMapper.insertBatch(postTags);
        }

        return post;
    }

    @Transactional
    public Post update(Post post, List<Long> tagIds) {
        Post existingPost = findById(post.getId());
        
        // 如果从草稿改为发布，设置发布时间
        if (existingPost != null && existingPost.getStatus() != 1 && post.getStatus() != null && post.getStatus() == 1) {
            post.setPublishedTime(java.time.LocalDateTime.now());
        }
        
        postMapper.update(post);

        // 更新标签关联
        if (tagIds != null) {
            // 先删除旧的关联
            postTagMapper.deleteByPostId(post.getId());

            // 如果有新标签，则添加
            if (!tagIds.isEmpty()) {
                List<PostTag> postTags = new ArrayList<>();
                for (Long tagId : tagIds) {
                    PostTag postTag = new PostTag();
                    postTag.setPostId(post.getId());
                    postTag.setTagId(tagId);
                    postTags.add(postTag);
                }
                postTagMapper.insertBatch(postTags);
            }
        }

        return findById(post.getId());
    }

    /**
     * 软删除文章
     */
    @Transactional
    public void deleteById(Long id) {
        Post post = new Post();
        post.setId(id);
        post.setIsDeleted(1);
        postMapper.update(post);
    }

    public void increaseViewCount(Long id) {
        postMapper.increaseViewCount(id);
    }

    public void increaseLikeCount(Long id) {
        postMapper.increaseLikeCount(id);
    }
}