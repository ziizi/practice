package com.bai.practice.service;

import com.bai.practice.dao.DiscussPostMapper;
import com.bai.practice.entity.DiscussPost;
import com.bai.practice.util.CommunityUtil;
import com.bai.practice.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    public List<DiscussPost> findDiscussPosts(int userid,int offset,int limit){
        return discussPostMapper.selectDiscussPosts(userid,offset,limit);
    }

    public int findDiscussPostRows(int userid){
        return discussPostMapper.selectDiscussPostRows(userid);
    }

    // 发布帖子，帖子的标题和内容需要过滤敏感词
    public int addDiscussPost(DiscussPost discussPost) {
        if (discussPost == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        // 转义html
        discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));
        discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));

        // 过滤敏感词
        discussPost.setTitle(sensitiveFilter.filter(discussPost.getTitle()));
        discussPost.setContent(sensitiveFilter.filter(discussPost.getContent()));

        return  discussPostMapper.insertDiscussPost(discussPost);
    }

    public DiscussPost findDiscussPostById (int id) {
        return discussPostMapper.selectDiscussPostById(id);
    }

    public int updateCommentCount(int id,int commentCount){
        return discussPostMapper.updateCommentCount(id,commentCount);
    }
}
