package com.bai.practice.service;

import com.bai.practice.dao.CommentMapper;
import com.bai.practice.entity.Comment;
import com.bai.practice.entity.DiscussPost;
import com.bai.practice.util.CommunitConstant;
import com.bai.practice.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Component
public class CommentService implements CommunitConstant {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private DiscussPostService discussPostService;

    public List<Comment> findComentByEntity (int entityType, int entityId, int offset, int limit){
        return commentMapper.selectByEntity(entityType,entityId,offset,limit);
    }

    public int findCommentCount (int entityType,int entityId){
        return commentMapper.selectCountByEntity(entityType,entityId);
    }

    // 添加评论
    @Transactional(isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
    public int addComment (Comment comment){
        if (comment == null){
            throw new IllegalArgumentException("参数不能为空");
        }
        //添加评论
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        int rows = commentMapper.insertComment(comment);

        // 更新帖子的评论数量，不是回复
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            int count = commentMapper.selectCountByEntity(comment.getEntityType(),comment.getEntityId());
            discussPostService.updateCommentCount(comment.getEntityId(),count);
        }


        return rows;
    }

    public Comment findCommentById (int id){
        return commentMapper.selectCommentById(id);
    }
}
