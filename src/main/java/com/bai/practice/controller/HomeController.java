package com.bai.practice.controller;

import com.bai.practice.entity.DiscussPost;
import com.bai.practice.entity.Page;
import com.bai.practice.entity.User;
import com.bai.practice.service.DiscussPostService;
import com.bai.practice.service.LikeService;
import com.bai.practice.service.UserService;
import com.bai.practice.util.CommunitConstant;
import com.sun.xml.internal.ws.api.ha.StickyFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController implements CommunitConstant {

    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @RequestMapping(path = "/index",method = RequestMethod.GET)
    public String  getIndexPage (Model model, Page page,
                                 @RequestParam(name = "orderMode",defaultValue = "0") int orderMode) {
        // 方法调用之前，springmvc 自动实例化参数，并将page注入model
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index?orderMode="+orderMode);

        List<DiscussPost> list = discussPostService.findDiscussPosts(
                0,page.getOffset(),page.getLimit(),orderMode);
        List<Map<String,Object>> discussPost = new ArrayList<Map<String,Object>>();
        if (list != null) {
            for (DiscussPost post : list)  {
                Map<String,Object> map = new HashMap<String,Object>();
                map.put("post",post);
                User user = userService.findUserById(post.getUserId());
                map.put("user",user);

                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST,post.getId());
                map.put("likeCount",likeCount);
                discussPost.add(map);
            }
        }

        model.addAttribute("discussPosts",discussPost);
        model.addAttribute("orderMode",orderMode);
        System.out.println("数据访问完成");
        return "/index";
    }

    @RequestMapping(path = "/error",method = RequestMethod.GET)
    public String getErrorPage(){
        return "/error/500";
    }

    // 拒绝访问时提示页面
    @RequestMapping(path = "/denied", method = RequestMethod.GET)
    public String getDeniedPage() {
        return "/error/404";
    }
}
