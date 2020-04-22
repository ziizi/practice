package com.bai.practice.config;

import com.bai.practice.controller.interceptor.HelloInterceptor;
import com.bai.practice.controller.interceptor.LoginRequiredInterceptor;
import com.bai.practice.controller.interceptor.LoginTicketInterceptor;
import com.bai.practice.controller.interceptor.MessageInterceptor;
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

   /* @Autowired
    private LoginRequiredInterceptor loginRequiredInterceptor;
*/
    @Autowired
    private MessageInterceptor messageInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(helloInterceptor).excludePathPatterns("/**/*.css","/**/*.png","/**/*.js","/**/*.jpg","/**/*.jpeg")
                .addPathPatterns("/login","/register");

        registry.addInterceptor(loginTicketInterceptor).excludePathPatterns("/**/*.css","/**/*.png","/**/*.js","/**/*.jpg","/**/*.jpeg");

        //registry.addInterceptor(loginRequiredInterceptor).excludePathPatterns("/**/*.css","/**/*.png","/**/*.js","/**/*.jpg","/**/*.jpeg")
       // .addPathPatterns("/user/setting","/user/upload");

        registry.addInterceptor(messageInterceptor).excludePathPatterns("/**/*.css","/**/*.png","/**/*.js","/**/*.jpg","/**/*.jpeg");
    }

}
