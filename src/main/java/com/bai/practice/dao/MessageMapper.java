package com.bai.practice.dao;

import com.bai.practice.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {

    // 查询当前会话的列表，针对每个会话，只显示最新的私信
    List<Message> selectConversations(int userId,int offset,int limit);

    // 查询当前用户的会话数量
    int selectConversationCount(int userId);

    //查询某个会话的私信列表
    List<Message> selectLetters(String conversationId,int offset,int limit);

    // 查询某个会话包含的数量
    int selectLetterCount(String conversationId);

    // 查询未读的私信的数量
    int selectUnreadLetters(int userId,String conversationId);

    int insertMessage (Message message);

    int updateStatus (List<Integer> ids,int status);


    // 查询某个主题下最新的通知
    Message selectLatestNotic  (int userId,String topic);
    // 查询某个主题通知的数量
    int selectNoticeCount(int userId,String topic) ;
    // 查询未读的通知数量
    int selectNoticeUnreadCount(int userId,String topic);

    List<Message> selectNotices (int userId,String topic,int offset,int limit);
}
