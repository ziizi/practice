package com.bai.practice.config;

import com.bai.practice.quartz.HelloQuartz;
import com.bai.practice.quartz.PostScoreRefreshJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

// 配置 -》 第一次调用时读取到数据库的表中
@Configuration
public class QuartzConfig {
   // @Bean
    public JobDetailFactoryBean helloJobDetail() {
        JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
        jobDetailFactoryBean.setJobClass(HelloQuartz.class);
        jobDetailFactoryBean.setName("helloQuartz");
        jobDetailFactoryBean.setGroup("helloGroup");
        jobDetailFactoryBean.setDurability(true);
        jobDetailFactoryBean.setRequestsRecovery(true);
        return jobDetailFactoryBean;
    }

   // @Bean
    public SimpleTriggerFactoryBean helloTrigger(JobDetail helloJobDetail) {
        SimpleTriggerFactoryBean simpleTriggerFactoryBean = new SimpleTriggerFactoryBean();
        simpleTriggerFactoryBean.setJobDetail(helloJobDetail);
        simpleTriggerFactoryBean.setName("helloTrigger");
        simpleTriggerFactoryBean.setGroup("helloGroup");
        simpleTriggerFactoryBean.setRepeatInterval(3000);
        simpleTriggerFactoryBean.setJobDataMap(new JobDataMap());
        return simpleTriggerFactoryBean;
    }

    // 刷新贴子分数任务
    @Bean
    public JobDetailFactoryBean postScoreRefreshJob() {
        JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
        jobDetailFactoryBean.setJobClass(PostScoreRefreshJob.class);
        jobDetailFactoryBean.setName("postScoreRefreshJob");
        jobDetailFactoryBean.setGroup("communityGroup");
        jobDetailFactoryBean.setDurability(true);
        jobDetailFactoryBean.setRequestsRecovery(true);
        return jobDetailFactoryBean;
    }

     @Bean
    public SimpleTriggerFactoryBean postScoreRefreshTrigger(JobDetail postScoreRefreshJob) {
        SimpleTriggerFactoryBean simpleTriggerFactoryBean = new SimpleTriggerFactoryBean();
        simpleTriggerFactoryBean.setJobDetail(postScoreRefreshJob);
        simpleTriggerFactoryBean.setName("postScoreRefreshTrigger");
        simpleTriggerFactoryBean.setGroup("communityGroup");
        simpleTriggerFactoryBean.setRepeatInterval(1000 * 60 * 5);
        simpleTriggerFactoryBean.setJobDataMap(new JobDataMap());
        return simpleTriggerFactoryBean;
    }

}
