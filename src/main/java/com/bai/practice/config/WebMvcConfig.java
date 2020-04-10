package com.bai.practice.config;

import com.bai.practice.controller.interceptor.HelloInterceptor;
import com.bai.practice.controller.interceptor.LoginRequiredInterceptor;
import com.bai.practice.controller.interceptor.LoginTicketInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private HelloInterceptor helloInterceptor;

    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;

    @Autowired
    private LoginRequiredInterceptor loginRequiredInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(helloInterceptor).excludePathPatterns("/* */*.css","/* */*.png")
                .addPathPatterns("/login","/register");

        registry.addInterceptor(loginTicketInterceptor).excludePathPatterns("/* */*.css","/* */*.pnn");

        registry.addInterceptor(loginRequiredInterceptor).excludePathPatterns("/* */*.css","/* */*.png")
        .addPathPatterns("/user/setting","/user/upload");
    }

}
