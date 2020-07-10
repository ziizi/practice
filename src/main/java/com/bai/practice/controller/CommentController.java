package com.bai.practice.controller;

import com.bai.practice.entity.Comment;
import com.bai.practice.entity.DiscussPost;
import com.bai.practice.entity.Event;
import com.bai.practice.event.EventProducer;
import com.bai.practice.service.CommentService;
import com.bai.practice.service.DiscussPostService;
import com.bai.practice.util.CommunitConstant;
import com.bai.practice.util.HostHolder;
import com.bai.practice.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController implements CommunitConstant {

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private RedisTemplate redisTemplate;


    @RequestMapping(path = "/add/{discussPostId}",method = RequestMethod.POST)
    public String addComment (@PathVariable("discussPostId") int discussPostId, Comment comment) {
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);

        // 出发评论事件，发送系统通知
        Event event = new Event()
                .setTopic(TOPIC_COMMENT)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData("postId",discussPostId);
        if (comment.getEntityType() == ENTITY_TYPE_POST){
            DiscussPost target = discussPostService.findDiscussPostById(discussPostId);
            event.setEntityUserId(target.getUserId());
        } else if (comment.getEntityType() == ENTITY_TYPE_COMMENT) {
            Comment target = commentService.findCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }
        eventProducer.fireEvent(event);


        if (comment.getEntityType() == ENTITY_TYPE_POST){
             event = new Event().setTopic(TOPIC_PUBLISH)
                    .setUserId(comment.getUserId())
                    .setEntityType(ENTITY_TYPE_POST)
                    .setUserId(discussPostId);
            eventProducer.fireEvent(event);

            // 计算帖子score
            String redisKey = RedisKeyUtil.getPostScoreKey();
            redisTemplate.opsForSet().add(redisKey,discussPostId);
        }

        return "redirect:/discuss/detail/"+discussPostId;

    }
}
