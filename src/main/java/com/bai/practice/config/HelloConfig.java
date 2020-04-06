package com.bai.practice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

@Configuration
public class HelloConfig {

    @Bean // 方法名就是bean的名字
    public SimpleDateFormat simpleDateFormat () {
        return  new SimpleDateFormat("yyyy/mm/dd");
    }
}
