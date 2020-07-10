package com.bai.practice.controller;

import com.bai.practice.entity.*;
import com.bai.practice.event.EventProducer;
import com.bai.practice.service.CommentService;
import com.bai.practice.service.DiscussPostService;
import com.bai.practice.service.LikeService;
import com.bai.practice.service.UserService;
import com.bai.practice.util.CommunitConstant;
import com.bai.practice.util.CommunityUtil;
import com.bai.practice.util.HostHolder;
import com.bai.practice.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunitConstant{
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private CommentService commentService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private RedisTemplate redisTemplate;


    @RequestMapping(path = "/add",method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost (String title,String content){
        User user = hostHolder.getUser();
        if (user == null) {
            return CommunityUtil.getJSONString(403,"没有登录");
        }
        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(user.getId());
        discussPost.setContent(content);
        discussPost.setTitle(title);
        discussPost.setCreateTime(new Date());
        discussPostService.addDiscussPost(discussPost);

        // 触发发帖事件
        Event event = new Event().setTopic(TOPIC_PUBLISH)
                .setUserId(user.getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(discussPost.getId());
        eventProducer.fireEvent(event);

        // 计算帖子score
        String redisKey = RedisKeyUtil.getPostScoreKey();
        redisTemplate.opsForSet().add(redisKey,discussPost.getId());
        // 报错的情况统一处理
        return CommunityUtil.getJSONString(0,"成功");
    }

    @RequestMapping(path = "/detail/{discussPostId}",method = RequestMethod.GET)
    public String getDiscussPost (@PathVariable("discussPostId") int discussPostid, Model model, Page page){
        DiscussPost post = discussPostService.findDiscussPostById(discussPostid);
        model.addAttribute("post",post);
        // 帖子作者
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user",user);

        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST,post.getId());
        model.addAttribute("likeCount",likeCount);
        int likeStatus = hostHolder.getUser() == null ? 0 : likeService.findEntityLikeStatus(user.getId(),ENTITY_TYPE_POST,post.getId());
        model.addAttribute("likeStatus",likeStatus);

        // 评论分页信息
        page.setLimit(5);
        page.setPath("/discuss/detail/" + discussPostid);
        page.setRows(post.getCommentCount());

        // 评论： 给帖子的评论
        // 回复： 给评论的评论
        List<Comment> commentList = commentService.findComentByEntity(ENTITY_TYPE_POST,post.getId(),
                page.getOffset(),page.getLimit());
        List<Map<String,Object>> commentVoList = new ArrayList<>();
        if (commentList != null){
            for (Comment comment : commentList){
                Map<String,Object> comVo = new HashMap<>();
                comVo.put("comment",comment);
                comVo.put("user",userService.findUserById(comment.getUserId()));

                likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT,comment.getId());
                comVo.put("likeCount",likeCount);
                likeStatus = hostHolder.getUser() == null ? 0 : likeService.findEntityLikeStatus(user.getId(),ENTITY_TYPE_COMMENT,comment.getId());
                comVo.put("likeStatus",likeStatus);

                //回复列表
                List<Comment> replyList = commentService.findComentByEntity(ENTITY_TYPE_POST,comment.getId(),
                        0,Integer.MAX_VALUE);

                List<Map<String,Object>> replyVoList = new ArrayList<>();
                if (replyList != null){
                    for (Comment reply : replyList){
                        Map<String,Object> replyVo = new HashMap<>();
                        replyVo.put("reply",reply);
                        replyVo.put("user",userService.findUserById(reply.getUserId()));
                        likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT,reply.getId());
                        replyVo.put("likeCount",likeCount);
                        likeStatus = hostHolder.getUser() == null ? 0 : likeService.findEntityLikeStatus(user.getId(),ENTITY_TYPE_COMMENT,reply.getId());
                        replyVo.put("likeStatus",likeStatus);
                        // 处理回复的目标target_id
                        User target = reply.getTargetId() == 0 ? null: userService.findUserById(reply.getTargetId());
                        replyVo.put("target",target);
                        replyVoList.add(replyVo);
                    }
                }
                comVo.put("replys",replyVoList);
                // 回复数量
                int relpCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT,comment.getId());
                comVo.put("replyCount",relpCount);
                commentVoList.add(comVo);
            }

        }
        model.addAttribute("comments",commentVoList);
        return "/site/discuss-detail";
    }

    // 置顶
    @RequestMapping(path = "/top",method = RequestMethod.POST)
    @ResponseBody
    public String setTop (int id){
        discussPostService.updateType(1,id);
        // 同步到elasticsearch
        // 触发发帖事件
        Event event = new Event().setTopic(TOPIC_PUBLISH)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);
        return CommunityUtil.getJSONString(0);
    }

    // 加精
    @RequestMapping(path = "/wonderful",method = RequestMethod.POST)
    @ResponseBody
    public String setWonderful (int id){
        discussPostService.updateStatus(1,id);
        // 同步到elasticsearch
        // 触发发帖事件
        Event event = new Event().setTopic(TOPIC_PUBLISH)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);

        // 计算帖子score
        String redisKey = RedisKeyUtil.getPostScoreKey();
        redisTemplate.opsForSet().add(redisKey,id);

        return CommunityUtil.getJSONString(0);
    }


    // 删除
    @RequestMapping(path = "/delete",method = RequestMethod.POST)
    @ResponseBody
    public String setDelete (int id){
        discussPostService.updateStatus(2,id);
        // 同步到elasticsearch
        // 触发删除事件
        Event event = new Event().setTopic(TOPIC_DELETE)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);
        return CommunityUtil.getJSONString(0);
    }
}
