package com.bai.practice.controller;

import com.alibaba.fastjson.JSONObject;
import com.bai.practice.entity.Message;
import com.bai.practice.entity.Page;
import com.bai.practice.entity.User;
import com.bai.practice.service.MessageService;
import com.bai.practice.service.UserService;
import com.bai.practice.util.CommunitConstant;
import com.bai.practice.util.CommunityUtil;
import com.bai.practice.util.HostHolder;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

@Controller
public class MessageController implements CommunitConstant {
    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/letter/list",method = RequestMethod.GET)
    public String getLetterList (Model model, Page page) {
        User user = hostHolder.getUser();
        page.setLimit(5);
        page.setPath("/letter/list");
        List<Message> conversationList = messageService.findConversations(user.getId(),
                page.getOffset(),page.getLimit());

        List<Map<String,Object>> conversations = new ArrayList<>();
        if (conversationList != null){
            for (Message message : conversationList) {
                Map<String,Object> map = new HashMap<>();
                map.put("conversation",message);
                map.put("letterCount",messageService.findLetterCount(message.getConversationId()));
                map.put("unreadCount",messageService.findUnreadLetters(user.getId(),message.getConversationId()));
                int targetId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                map.put("target",userService.findUserById(targetId));
                conversations.add(map);
            }

        }
        model.addAttribute("conversations",conversations);

        // 查询所有未读消息数量
        int letterUnread = messageService.findUnreadLetters(user.getId(),null);
        model.addAttribute("letterUnreadCount",letterUnread);

        int noticeUnread = messageService.findNoticeUnreadCount(user.getId(),null);
        model.addAttribute("noticeUnread",noticeUnread);

        return "/site/letter";
    }

    @RequestMapping(path = "/letter/detail/{conversationId}", method = RequestMethod.GET)
    public String getLetterDetail (@PathVariable("conversationId") String conversationId,Page page,Model model) {
        page.setLimit(5);
        page.setPath("/letter/detail/"+conversationId);
        page.setRows(messageService.findLetterCount(conversationId));

        // 私信列表
        List<Message> letterList = messageService.findLetters(conversationId,page.getOffset(),page.getLimit());

        List<Map<String,Object>> letters = new ArrayList<>();
        if (letterList != null) {
            for (Message message : letterList){
                Map<String ,Object> map = new HashMap<>();
                map.put("letter",message);
                map.put("fromUser",userService.findUserById(message.getFromId()));
                letters.add(map);
            }
        }

        model.addAttribute("letters",letters);
        model.addAttribute("target",getLetterTarget(conversationId));

        List<Integer> ids = getletterIds(letterList);
        if (!ids.isEmpty()) {
            messageService.readMessge(ids);
        }

        return "/site/letter-detail";
    }

    private User getLetterTarget (String conversationId) {
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);

        if (hostHolder.getUser().getId() == id0) {
            return userService.findUserById(id1);
        }
        return  userService.findUserById(id0);
    }

    private List<Integer> getletterIds (List<Message> letterList) {
        List<Integer> list = new ArrayList();
        if (letterList != null) {
            for (Message message : letterList){
                if (hostHolder.getUser().getId() == message.getToId() && message.getStatus() == 0) {
                    list.add(message.getId());
                }
            }
        }
        return list;
    }

    @RequestMapping(path = "/letter/send",method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter (String toName,String content) {
        User target = userService.findUserByName(toName);
        if (target == null) {
            return CommunityUtil.getJSONString(1,"目标用户不存在");
        }

        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(target.getId());
        if (message.getFromId() < message.getToId()){
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        }else {
            message.setConversationId(message.getToId()+ "_" + message.getFromId());
        }
        message.setContent(content);
        message.setCreateTime(new Date());
        messageService.addMessage(message);

        return CommunityUtil.getJSONString(0);
    }


    @RequestMapping(path = "/notice/list")
    public String getNoticeList(Model model) {
        User user = hostHolder.getUser();
        // 评论类通知
        Message message = messageService.findLatestNotic(user.getId(),TOPIC_COMMENT);

        if (message != null){
            Map<String,Object> messageVo = new HashMap<>();
            messageVo.put("message",message);
            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map <String,Object> data = JSONObject.parseObject(content,HashMap.class);

            messageVo.put("user",userService.findUserById((Integer) data.get("userID")));
            messageVo.put("entityType",data.get("entityType"));
            messageVo.put("entityId",data.get("entityId"));
            messageVo.put("postId",data.get("postId"));

            int count = messageService.findNoticeCount(user.getId(),TOPIC_COMMENT);
            messageVo.put("read",count);
            int unread = messageService.findNoticeUnreadCount(user.getId(),TOPIC_COMMENT);
            messageVo.put("unread",unread);
            model.addAttribute("commentNotice",messageVo);
        }


        // 点赞类通知
        message = messageService.findLatestNotic(user.getId(),TOPIC_LIKE);

        if (message != null){
            Map messageVo = new HashMap<>();
            messageVo.put("message",message);
            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map <String,Object> data = JSONObject.parseObject(content,HashMap.class);

            messageVo.put("user",userService.findUserById((Integer) data.get("userID")));
            messageVo.put("entityType",data.get("entityType"));
            messageVo.put("entityId",data.get("entityId"));
            messageVo.put("postId",data.get("postId"));

            int count = messageService.findNoticeCount(user.getId(), TOPIC_LIKE);
            messageVo.put("read",count);
            int unread = messageService.findNoticeUnreadCount(user.getId(), TOPIC_LIKE);
            messageVo.put("unrad",unread);
            model.addAttribute("likeNotice",messageVo);
        }


        // 关注类通知
        message = messageService.findLatestNotic(user.getId(),TOPIC_FOLLOW);

        if (message != null){
            Map messageVo = new HashMap<>();
            messageVo.put("message",message);
            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map <String,Object> data = JSONObject.parseObject(content,HashMap.class);

            messageVo.put("user",userService.findUserById((Integer) data.get("userID")));
            messageVo.put("entityType",data.get("entityType"));
            messageVo.put("entityId",data.get("entityId"));

            int count = messageService.findNoticeCount(user.getId(), TOPIC_FOLLOW);
            messageVo.put("read",count);
            int unread = messageService.findNoticeUnreadCount(user.getId(), TOPIC_FOLLOW);
            messageVo.put("unrad",unread);
            model.addAttribute("followNotice",messageVo);
        }


        // 查询未读消息
        int letterUnreadCount = messageService.findUnreadLetters(user.getId(),null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);

        int noticeUnread = messageService.findNoticeUnreadCount(user.getId(),null);
        model.addAttribute("noticeUnread",noticeUnread);

        return "/site/notice";
    }


    @RequestMapping(path = "/notice/detail/{topic}",method = RequestMethod.GET)
    public String getNoticeDetail (@PathVariable("topic") String topic, Page page,Model model) {
        User user = hostHolder.getUser();

        page.setLimit(5);
        page.setPath("/notice/detail/" + topic);
        page.setRows(messageService.findNoticeCount(user.getId(),topic));

        List<Message> noticeList = messageService.findNotices(user.getId(),topic,page.getOffset(),page.getLimit());
        List<Map<String,Object>>  noticeVoList = new ArrayList<>();
        if (noticeList != null) {
            for (Message notice : noticeList){
                Map<String,Object> map = new HashMap<>();
                map.put("notice",notice);
                String content = HtmlUtils.htmlUnescape(notice.getContent());

                Map<String,Object> data = JSONObject.parseObject(content,Map.class);
                map.put("user",userService.findUserById((Integer) data.get("userId")));
                map.put("entityType",data.get("entityType"));
                map.put("entityId",data.get("entityId"));
                map.put("postId",data.get("postId"));

                // 通知的作者
                map.put("fromUser",userService.findUserById(notice.getFromId()));
                noticeVoList.add(map);

            }
        }

        model.addAttribute("notices",noticeVoList);

        // 设置已读
        List<Integer> ids = getletterIds(noticeList);
        if (!ids.isEmpty()) {
            messageService.readMessge(ids);
        }
        return "/site/notice-detail";
    }
}
