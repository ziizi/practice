package com.bai.practice.service;

import com.bai.practice.dao.LoginTicketMapper;
import com.bai.practice.dao.UserMapper;
import com.bai.practice.entity.LoginTicket;
import com.bai.practice.entity.User;
import com.bai.practice.util.CommunitConstant;
import com.bai.practice.util.CommunityUtil;
import com.bai.practice.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.jws.soap.SOAPBinding;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunitConstant {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;



    public User findUserById(int userid) {
        return  userMapper.selectById(userid);
    }


    public Map<String,Object> register (User user) {
        Map map = new HashMap<String,Object>();
        if (user == null) {
            throw new IllegalArgumentException("非法参数！");
        }

        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg","账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg","邮箱不能为空");
            return map;
        }

        // 验证账号
        User u = userMapper.selectByName(user.getUsername());
        if (u != null) {
            map.put("usernameMsg","账号已经存在");
            return map;
        }
        u = userMapper.selectByEmail(user.getEmail());
        if (u != null) {
            map.put("emailMsg","邮箱已经注册");
            return map;
        }

        // 注册用户
        user.setSalt(CommunityUtil.genUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.genUUID());
        user.setHeaderUrl(String.format("http://images.newcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        // 激活邮件
        Context context = new Context();
        context.setVariable("email",user.getEmail());
        String url = domain + contextPath + "/activation/"+ user.getId() +"/" +user.getActivationCode();
        context.setVariable("url",url);
        String content = templateEngine.process("/mail/activation",context);
        mailClient.sendMail(user.getEmail(),"激活账号",content);

        return map;
    }

    public int activation (int userId,String code) {
        User user = userMapper.selectById(userId);
        if (user.getStatus() == 1) {
            return CommunitConstant.ACTIVATION_REPEAT;
        }else if (user.getActivationCode().equals(code)){
            userMapper.updateStatus(userId,1);
            return CommunitConstant.ACTIVATION_SUCCESS;
        } else {
            return CommunitConstant.ACTIVATION_FAIL;
        }
    }


    public Map<String,Object> loginTicket (String userName,String passWord,int expiredSeconds) {
        Map<String ,Object> map = new HashMap<String ,Object>();
        if (userName == null){
            map.put("usernameMsg","账号不能为空！");
            return map;
        }
        if (passWord == null){
            map.put("passwordMsg","密码不能为空！");
            return map;
        }

        // 验证账号是否有
        User user = userMapper.selectByName(userName);
        if (user == null) {
            map.put("usernameMsg","账号不正确！");
            return map;
        }

        // 验证密码
        passWord = CommunityUtil.md5(passWord + user.getSalt());
        if (!passWord.equals(user.getPassword())) {
            map.put("passwordMsg","密码输入不正确");
            return map;
        }

        // 生产凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setStatus(0);
        loginTicket.setTicket(CommunityUtil.genUUID());
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        loginTicketMapper.insertLoginTicket(loginTicket);
        map.put("ticket",loginTicket.getTicket());
        return map;
    }

    public void logout (String ticket) {
        loginTicketMapper.updateStatus(ticket,1);
    }

    public LoginTicket findLoginTicket (String ticket) {
        return loginTicketMapper.selectByTicket(ticket);
    }

    public int updateHeader (User user,String headerUrl) {
        return userMapper.updateHeader(user.getId(),headerUrl);
    }


}
