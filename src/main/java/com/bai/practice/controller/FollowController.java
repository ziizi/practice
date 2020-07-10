package com.bai.practice.controller;

import com.bai.practice.entity.Event;
import com.bai.practice.entity.User;
import com.bai.practice.event.EventProducer;
import com.bai.practice.service.FollowService;
import com.bai.practice.util.CommunitConstant;
import com.bai.practice.util.CommunityUtil;
import com.bai.practice.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FollowController implements CommunitConstant {

    @Autowired
    private FollowService followService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @RequestMapping(path = "/follow",method = RequestMethod.POST)
    @ResponseBody
    public String follow (int entityType,int entityId) {
        User user = hostHolder.getUser();
        followService.follow(user.getId(),entityType,entityId);

        // 触发系统通知
        Event event = new Event()
                .setTopic(TOPIC_FOLLOW)
                .setUserId(user.getId())
                .setEntityType(entityType)
                .setEntityId(entityId)
                .setEntityUserId(entityId);
        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(0,"已关注");
    }

    @RequestMapping(path = "/unfollow",method = RequestMethod.POST)
    @ResponseBody
    public String unfollow (int entityType,int entityId) {
        User user = hostHolder.getUser();
        followService.unFollow(user.getId(),entityType,entityId);

        return CommunityUtil.getJSONString(0,"已取消关注");
    }

}
