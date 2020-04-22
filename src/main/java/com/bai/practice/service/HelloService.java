package com.bai.practice.service;

import com.bai.practice.dao.DiscussPostMapper;
import com.bai.practice.dao.HelloDao;
import com.bai.practice.dao.UserMapper;
import com.bai.practice.entity.DiscussPost;
import com.bai.practice.entity.User;
import com.bai.practice.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;

@Service
// @Scope("prototype")  // 默认是单例的（singleton）,prototype是多例
public class HelloService {

    @Autowired
    @Qualifier("Hiber")
    private HelloDao helloDao;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;


    public HelloService () {
        System.out.println("construct");
    }

    @PostConstruct // 构造器后初始化
    public void  init () {
        System.out.println("init method ...");
    }

    @PreDestroy // 销毁之前调用
    public void  destory () {
        System.out.println("destory method ...");
    }


    public String find () {
        return helloDao.select();
    }


    /** 声明式事务
    * isolation事务的隔离级别
     * propagation 事务传播机制
     *   1.REQUIRED:支持当前事务（外部事务，如果外部事务不存在，在创建新事务），
     *   2.REQUIRES_NEW : 不管外部事务，都创建新事务，如果有当前事务，则当前事务挂起
     *   3.NESTED : 如果当前事务存在，则嵌套执行，如果外部事务不存在，则和REQUIRED一样 */
    @Transactional(isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
    public Object save1 () {
        // 新增用户
        User user = new User();
        user.setUsername("hello");
        user.setSalt(CommunityUtil.genUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
        user.setEmail("hello@qq.com");
        user.setHeaderUrl("nowcoder12@sina.com");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        // 发布帖子
        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(user.getId());
        discussPost.setTitle("hello");
        discussPost.setContent("新人报道");
        discussPost.setCreateTime(new Date());

        Integer.valueOf("abc");

        return "ok";
    }

    // 编程式事务
    public Object save2 () {
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        return transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                // 新增用户
                User user = new User();
                user.setUsername("beta");
                user.setSalt(CommunityUtil.genUUID().substring(0,5));
                user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
                user.setEmail("beta@qq.com");
                user.setHeaderUrl("nowcoder123@sina.com");
                user.setCreateTime(new Date());
                userMapper.insertUser(user);

                // 发布帖子
                DiscussPost discussPost = new DiscussPost();
                discussPost.setUserId(user.getId());
                discussPost.setTitle("beta");
                discussPost.setContent("beta新人报道");
                discussPost.setCreateTime(new Date());

                Integer.valueOf("abc");

                return "ok";
            }
        });
    }
}
