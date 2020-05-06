package com.bai.practice.service;

import com.bai.practice.dao.DiscussPostMapper;
import com.bai.practice.entity.DiscussPost;
import com.bai.practice.util.CommunityUtil;
import com.bai.practice.util.SensitiveFilter;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class DiscussPostService {

    private static final Logger logger = LoggerFactory.getLogger(DiscussPost.class);

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Value("${caffeine.posts.max-size}")
    private int max;

    @Value("${caffeine.posts.expire-seconds}")
    private int expireSeconds;

    // caffeine 核心接口Cache, LoadingCache ,AsyncLoadingCache

    // 帖子列表的缓存
    private LoadingCache<String,List<DiscussPost>> postListCache;

    // 帖子总数的缓存
    private LoadingCache<Integer,Integer> postRowsCache;

    @PostConstruct
    public void init (){
        // 初始化 帖子列表的缓存
        postListCache = Caffeine.newBuilder()
                .maximumSize(max)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<DiscussPost>>() {
                    @Nullable
                    @Override
                    public List<DiscussPost> load(@NonNull String key) throws Exception {
                        if (key == null || key.length() == 0){
                            throw new IllegalArgumentException("参数错误");
                        }

                        String[] parms = key.split(":");
                        if (parms == null || parms.length != 2){
                            throw new IllegalArgumentException("参数错误");
                        }

                        int offset = Integer.valueOf(parms[0]);
                        int limit = Integer.valueOf(parms[1]);

                        // 二级缓存访问redis -> mysql
                        logger.info("load post list from db");
                        return discussPostMapper.selectDiscussPosts(0,offset,limit,1);
                    }
                });

        // 初始化 帖子总数的缓存
        postRowsCache = Caffeine.newBuilder()
                .maximumSize(max)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<Integer, Integer>() {
                    @Nullable
                    @Override
                    public Integer load(@NonNull Integer key) throws Exception {
                        logger.info("load post list from db");
                        return discussPostMapper.selectDiscussPostRows(key);
                    }
                });
    }


    public List<DiscussPost> findDiscussPosts(int userid,int offset,int limit,int orderMode){
        if (userid == 0 && orderMode ==1){
            return postListCache.get(offset + ":" + limit);
        }
        logger.info("load post list from db");
        return discussPostMapper.selectDiscussPosts(userid,offset,limit,orderMode);
    }

    public int findDiscussPostRows(int userid){
        if (userid == 0){
            return postRowsCache.get(userid);
        }
        logger.info("load post rows from db");
        return discussPostMapper.selectDiscussPostRows(userid);
    }

    // 发布帖子，帖子的标题和内容需要过滤敏感词
    public int addDiscussPost(DiscussPost discussPost) {
        if (discussPost == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        // 转义html
        discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));
        discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));

        // 过滤敏感词
        discussPost.setTitle(sensitiveFilter.filter(discussPost.getTitle()));
        discussPost.setContent(sensitiveFilter.filter(discussPost.getContent()));

        return  discussPostMapper.insertDiscussPost(discussPost);
    }

    public DiscussPost findDiscussPostById (int id) {
        return discussPostMapper.selectDiscussPostById(id);
    }

    public int updateCommentCount(int id,int commentCount){
        return discussPostMapper.updateCommentCount(id,commentCount);
    }

    public int updateType(int type,int id){
        return discussPostMapper.updateType(type,id);
    }

    public int updateStatus(int status,int id){
        return discussPostMapper.updateStatus(status,id);
    }

    public int updateScore(double score,int id){
        return discussPostMapper.updateScore(score,id);
    }
}
