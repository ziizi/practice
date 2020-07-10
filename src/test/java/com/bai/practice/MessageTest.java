package com.bai.practice;


import com.bai.practice.dao.MessageMapper;
import com.bai.practice.entity.Message;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

@SpringBootTest
@ContextConfiguration(classes = PracticeApplication.class)
public class MessageTest {
    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void selectConversationsTest(){
        List<Message> list = messageMapper.selectConversations(111,0,20);
        for (Message message : list){
            System.out.println(message.toString());
        }

        int rows = messageMapper.selectConversationCount(111);
        System.out.println(rows);

        List<Message> letters = messageMapper.selectLetters("111_112",0,10);
        for (Message message : letters){
            System.out.println(message.toString());
        }

        int count = messageMapper.selectUnreadLetters(131,"111_113");
        System.out.println(count);

    }
}
