package com.example.blog.mapper;

import com.example.blog.entity.PostTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostTagMapper {

    void insertBatch(@Param("postTags") List<PostTag> postTags);

    void deleteByPostId(Long postId);

    List<Long> findTagIdsByPostId(Long postId);
}
