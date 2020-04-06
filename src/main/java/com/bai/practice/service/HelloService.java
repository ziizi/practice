package com.bai.practice.service;

import com.bai.practice.dao.HelloDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
// @Scope("prototype")  // 默认是单例的（singleton）,prototype是多例
public class HelloService {

    @Autowired
    @Qualifier("Hiber")
    private HelloDao helloDao;

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
}
