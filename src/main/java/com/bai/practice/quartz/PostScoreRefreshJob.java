package com.bai.practice.quartz;

import com.bai.practice.entity.DiscussPost;
import com.bai.practice.service.DiscussPostService;
import com.bai.practice.service.ElasticSearchService;
import com.bai.practice.service.LikeService;
import com.bai.practice.util.CommunitConstant;
import com.bai.practice.util.RedisKeyUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PostScoreRefreshJob implements Job, CommunitConstant {

    private static final Logger logger = LoggerFactory.getLogger(PostScoreRefreshJob.class);


    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private ElasticSearchService elasticSearchService;

    private static final Date epoch; //牛客纪云
    static {
        try {
            epoch = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-08-01 00:00:00");
        } catch (ParseException e) {
            throw new RuntimeException("牛客纪元初始化失败！");
        }
    }
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String redisKey = RedisKeyUtil.getPostScoreKey();
        BoundSetOperations operations = redisTemplate.boundSetOps(redisKey);
        if (operations.size() == 0){
            logger.info("不需要刷新帖子");
            return;
        }

        logger.info("任务开始，正在刷新帖子："+operations.size());

        while (operations.size() > 0){
            this.refresh((Integer)operations.pop());
        }
        logger.info("帖子刷新任务结束");
    }

    private void refresh (int postId){
        DiscussPost discussPost = discussPostService.findDiscussPostById(postId);
        if (discussPost == null) {
            logger.error("该帖子不存在，帖子id:"+postId);
            return;
        }

        // 加精
        boolean wonderful = discussPost.getStatus() == 1;
        // 评论数量
        int commentCount = discussPost.getCommentCount();
        // 点赞数量
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST,postId);

        // 计算权重
        double w = (wonderful ? 75 : 0) + commentCount * 10 + likeCount * 2;
        // 分数 = 帖子权重 + 距离天数
        double score = Math.log10(Math.max(w,1))
                + (discussPost.getCreateTime().getTime() - epoch.getTime())/(1000 * 3600 * 24);

        discussPostService.updateScore(score,postId);

        // 同步elasticsearch
        discussPost.setScore(score);
        elasticSearchService.saveDiscussPost(discussPost);
    }
}
