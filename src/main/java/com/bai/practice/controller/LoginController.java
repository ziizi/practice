package com.bai.practice.controller;

import com.bai.practice.entity.User;
import com.bai.practice.service.UserService;
import com.bai.practice.util.CommunitConstant;
import com.bai.practice.util.CommunityUtil;
import com.bai.practice.util.RedisKeyUtil;
import com.google.code.kaptcha.Producer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
public class LoginController implements CommunitConstant {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @RequestMapping(path = "/register",method = RequestMethod.GET)
    public String getRegestePage () {
        return "/site/register";
    }

    @RequestMapping(path = "/login",method = RequestMethod.GET)
    public String getLoginPage () {
        return "/site/login";
    }

    @RequestMapping(path = "/register",method = RequestMethod.POST)
    public String register (Model model, User user) {
        Map<String,Object> map = userService.register(user);
        if (map == null || map.isEmpty()) {
            model.addAttribute("msg","激活成功，激活邮件已发送，请去邮箱激活！");
            model.addAttribute("target","/index");
            return "/site/operate-result";
        } else {
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            return "/site/register";
        }
    }

    @RequestMapping(path = "/activation/{userId}/{code}",method = RequestMethod.GET)
    public String activation (Model model, @PathVariable("userId") int userId,@PathVariable("code") String code) {
        int result = userService.activation(userId,code);
        if (result == CommunitConstant.ACTIVATION_SUCCESS){
            model.addAttribute("msg","激活成功");
            model.addAttribute("target","/login");
        }else if (result == CommunitConstant.ACTIVATION_REPEAT) {
            model.addAttribute("msg","重复激活");
            model.addAttribute("target","/index");
        }else {
            model.addAttribute("msg","失败");
            model.addAttribute("target","/index");
        }
        return "/site/operate-result";
    }

    // 生产验证码
    @RequestMapping(path = "kaptcha",method = RequestMethod.GET)
    public void kaptcha (HttpServletResponse response/*, HttpSession session*/) {
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);
      /*  session.setAttribute("kaptcha",text);*/

        String kaptchaOwner = CommunityUtil.genUUID();
        Cookie cookie = new Cookie("kaptchaOwner",kaptchaOwner);
        cookie.setMaxAge(60);
        cookie.setPath(contextPath);
        response.addCookie(cookie);
        // 将验证码存在redis中
        String redisKey = RedisKeyUtil.getKaptcha(kaptchaOwner);
        redisTemplate.opsForValue().set(redisKey,text,60, TimeUnit.SECONDS);
        response.setContentType("image/png");

        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image,"png",os);
        } catch (IOException e) {
            logger.error("响应验证码失败" + e.getMessage());
        }
    }


    @RequestMapping(path = "login",method = RequestMethod.POST)
    public String login (Model model,String username,String password,String code,
                         boolean remenberme,/*HttpSession session,*/HttpServletResponse response,
                         @CookieValue("kaptchaOwner") String kaptchaOwner){
        // 判断验证码
        /*String kaptcha = (String) session.getAttribute("kaptcha");*/

        String kaptcha = null;
        if (StringUtils.isNotBlank(kaptchaOwner)){
            String redisKey = RedisKeyUtil.getKaptcha(kaptchaOwner);
            kaptcha = (String) redisTemplate.opsForValue().get(redisKey);
        }
        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !code.equalsIgnoreCase(kaptcha)){
            model.addAttribute("codeMsg","验证码不正确");
            return "/site/login";
        }

        // 检验账号密码
        int expiredSeconds = remenberme ? CommunitConstant.REMEMBER_EXPIRED_SECONDS : CommunitConstant.DEFAULT_EXPIRED_SECONDS;
        Map<String,Object> map = userService.loginTicket(username,password,expiredSeconds);
        if (map.containsKey("ticket")) {
            Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            return "redirect:index";
        } else {
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "/site/login";
        }
    }


    @RequestMapping (path = "/logout",method = RequestMethod.GET)
    public String logout (@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        SecurityContextHolder.clearContext();
        return "redirect:/login";
    }
}
