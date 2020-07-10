package com.bai.practice.controller.interceptor;

import com.bai.practice.entity.User;
import com.bai.practice.service.DataCountService;
import com.bai.practice.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class DataInterceptor  implements HandlerInterceptor {
    @Autowired
    private DataCountService dataCountService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 统计uv
        String ip = request.getRemoteHost();
        dataCountService.recodeUv(ip);
        // 统计dau
        User user = hostHolder.getUser();
        if (user != null) {
            dataCountService.recodeDAU(user.getId());
        }

        return true;
    }
}
