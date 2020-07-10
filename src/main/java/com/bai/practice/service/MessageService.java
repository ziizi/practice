package com.bai.practice.service;

import com.bai.practice.dao.MessageMapper;
import com.bai.practice.entity.Message;
import com.bai.practice.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    public List<Message> findConversations (int userId,int offset,int limit) {
        return messageMapper.selectConversations(userId,offset,limit);
    }

    public int findConversationCount (int userId) {
        return messageMapper.selectConversationCount(userId);
    }

    public List<Message> findLetters (String conversationId,int offset,int limit) {
        return messageMapper.selectLetters(conversationId,offset,limit);
    }

    public int findLetterCount (String conversationId){
        return messageMapper.selectLetterCount(conversationId);
    }

    public int findUnreadLetters (int userId,String conversationId){
        return messageMapper.selectUnreadLetters(userId,conversationId);
    }

    // 添加消息
    public int addMessage(Message message){
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.filter(message.getContent()));
        return messageMapper.insertMessage(message);
    }

    // 将消息置位已读
    public int readMessge (List<Integer> ids) {
        return messageMapper.updateStatus(ids,1);
    }

    public Message findLatestNotic (int userId,String topic){
        return messageMapper.selectLatestNotic(userId,topic);
    }

    public int findNoticeCount (int userId,String topic) {
        return messageMapper.selectNoticeCount(userId,topic);
    }

    public int findNoticeUnreadCount (int userId,String topic) {
        return messageMapper.selectNoticeUnreadCount(userId,topic);
    }

    public List<Message> findNotices (int userId,String topic,int offset,int limit) {
        return messageMapper.selectNotices(userId,topic,offset,limit);
    }
}
