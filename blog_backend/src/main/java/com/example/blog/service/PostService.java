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

/**
 * 文章业务实现
 *
 * <p>
 * 封装文章的查询、分页、创建、更新、删除及统计逻辑，负责协调文章与标签之间的关联，并在必要时
 * 维护发布时间、置顶状态、删除标记等字段。
 * </p>
 */
@Service
public class PostService implements IPostService {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private PostTagMapper postTagMapper;

    @Override
    public List<Post> findAll() {
        return postMapper.findAll();
    }

    @Override
    public List<Post> findPublished() {
        return postMapper.findByStatus(1);
    }

    @Override
    public PageResult<Post> findPublishedWithPage(Integer page, Integer size, String keyword) {
        int offset = (page - 1) * size;
        List<Post> posts = postMapper.findByStatusWithPage(1, offset, size, keyword);
        Long total = postMapper.countByStatus(1, keyword);
        return PageResult.of(posts, total, page, size);
    }

    @Override
    public PageResult<Post> findAllWithPage(Integer page, Integer size, String keyword) {
        int offset = (page - 1) * size;
        List<Post> posts = postMapper.findAllWithPage(offset, size, keyword);
        Long total = postMapper.countAll(keyword);
        return PageResult.of(posts, total, page, size);
    }

    @Override
    public Post findById(Long id) {
        return postMapper.findById(id);
    }

    @Override
    public List<Post> findByAuthorId(Long authorId) {
        return postMapper.findByAuthorId(authorId);
    }

    @Override
    public List<Post> findByCategoryId(Long categoryId) {
        return postMapper.findByCategoryId(categoryId);
    }

    @Override
    public List<Post> findByTagId(Long tagId) {
        return postMapper.findByTagId(tagId);
    }

    @Override
    @Transactional
    public Post create(Post post, List<Long> tagIds) {
        // 设置默认值，避免数据库中出现 null 值影响查询
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

        // 关联标签：文章插入完成后才能拿到自增ID
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

    @Override
    @Transactional
    public Post update(Post post, List<Long> tagIds) {
        Post existingPost = findById(post.getId());

        // 如果从草稿改为发布，设置发布时间，实现“首次发布时间”记录
        if (existingPost != null && existingPost.getStatus() != 1 && post.getStatus() != null
                && post.getStatus() == 1) {
            post.setPublishedTime(java.time.LocalDateTime.now());
        }

        postMapper.update(post);

        // 更新标签关联：先清理旧数据，保持映射关系正确
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

    @Override
    @Transactional
    public void deleteById(Long id) {
        Post post = new Post();
        post.setId(id);
        post.setIsDeleted(1);
        postMapper.update(post);
    }

    @Override
    public void increaseViewCount(Long id) {
        postMapper.increaseViewCount(id);
    }

    @Override
    public void increaseLikeCount(Long id) {
        postMapper.increaseLikeCount(id);
    }
}