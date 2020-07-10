package com.bai.practice.dao;

import com.bai.practice.entity.Comment;
import com.bai.practice.util.CommunitConstant;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {

    // 查询帖子，需要支持分页
    List<Comment> selectByEntity(int entityType,int entityId,int offset,int limit);

    // 查询总条数
    int selectCountByEntity(int entityType,int entityId);

    int insertComment (Comment comment);

    Comment selectCommentById (int id);
}
