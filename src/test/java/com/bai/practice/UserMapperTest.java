package com.bai.practice;


import com.bai.practice.dao.DiscussPostMapper;
import com.bai.practice.dao.UserMapper;
import com.bai.practice.entity.DiscussPost;
import com.bai.practice.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;
import java.util.List;

@SpringBootTest
@ContextConfiguration(classes = PracticeApplication.class)
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Test
    public void testSelectUser() {
        User user = userMapper.selectById(101);
        System.out.println(user.toString());

        user = userMapper.selectByName("zhangfei");
        System.out.println(user.toString());

        user = userMapper.selectByEmail("nowcoder112@sina.com");
        System.out.println(user.toString());
    }


    @Test
    public void testInsertUser() {
       User user = new User();
       user.setUsername("test");
       user.setPassword("test");
       user.setSalt("system");
       user.setEmail("test@qq.com");
       user.setHeaderUrl("http://images.nowcoder.com/head/100t.png");
       user.setCreateTime(new Date());

       int rows = userMapper.insertUser(user);
       System.out.println(rows);
       System.out.println(user.getId()); // 获取生产的主键
    }


    @Test
    public void testUpdateUser() {
        int rows = userMapper.updateStatus(150,1);
        System.out.println(rows);

        rows = userMapper.updateHeader(150,"http://images.nowcoder.com/head/149t.png");
        System.out.println(rows);

        rows = userMapper.updatePassword(150,"0123456");
        System.out.println(rows);
    }


    @Test
    public void testSelectPosts() {
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(149,0,10);
        for (DiscussPost post: list) {
            System.out.println(post.toString());
        }

        int rows = discussPostMapper.selectDiscussPostRows(149);
        System.out.println(rows);
    }
}
