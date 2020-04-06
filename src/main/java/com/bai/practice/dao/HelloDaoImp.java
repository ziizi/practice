package com.bai.practice.dao;

import org.springframework.stereotype.Repository;

@Repository("Hiber")
public class HelloDaoImp implements HelloDao {
    @Override
    public String select() {
        return "Hibernate";
    }
}
