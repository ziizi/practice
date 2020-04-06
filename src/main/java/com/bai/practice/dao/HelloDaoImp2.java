package com.bai.practice.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary // 更高的优先级
public class HelloDaoImp2 implements HelloDao {
    @Override
    public String select() {
        return "Mybatis";
    }
}
