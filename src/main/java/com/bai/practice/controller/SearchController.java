package com.bai.practice.controller;

import com.bai.practice.entity.DiscussPost;
import com.bai.practice.entity.Page;
import com.bai.practice.service.ElasticSearchService;
import com.bai.practice.service.LikeService;
import com.bai.practice.service.UserService;
import com.bai.practice.util.CommunitConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController implements CommunitConstant {

    @Autowired
    private ElasticSearchService elasticSearchService;

    @Autowired
    UserService userService;

    @Autowired
    private LikeService likeService;

    // /search?keyword=***
    @RequestMapping(path = "/search",method = RequestMethod.GET)
    public String search(String keyword, Page page, Model model){
        org.springframework.data.domain.Page<DiscussPost>  searchResult =
            elasticSearchService.searchDiscussPost(keyword,page.getCurrent() - 1, page.getLimit());

        List<Map<String, Object>>  discussPosts = new ArrayList<>();
        if (searchResult != null) {
           for (DiscussPost post : searchResult) {
               Map<String, Object> map = new HashMap<>();
                map.put("post",post);
                map.put("user",userService.findUserById(post.getUserId()));
                map.put("likeCount",likeService.findEntityLikeCount(ENTITY_TYPE_POST,post.getId()));
               discussPosts.add(map);
           }
        }

        model.addAttribute("discussPosts",discussPosts);
        model.addAttribute("keyword",keyword);

        // 分页信息
        page.setPath("/search?keyword="+keyword);
        page.setRows(searchResult == null ? 0 : (int)searchResult.getTotalElements());
        return "/site/search";
    }
}
