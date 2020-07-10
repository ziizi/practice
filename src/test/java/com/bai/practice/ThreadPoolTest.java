package com.bai.practice;

import com.bai.practice.service.HelloService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@ContextConfiguration(classes = PracticeApplication.class)
public class ThreadPoolTest {

    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolTest.class);

    // jdk 普通线程池
    private ExecutorService executorService = Executors.newFixedThreadPool(5);
    private ScheduledExecutorService scheduleService = Executors.newScheduledThreadPool(5);

    // spring 普通的线程池
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    // spring 定时的线程池
    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    @Autowired
    private HelloService helloService;

    @Test
    public void testExecutorService () throws InterruptedException {
        Runnable task = new Runnable(){
            @Override
            public void run() {
                logger.debug("hello ExecutorService");
            }
        };
        for (int i = 0; i < 10; i++) {
            executorService.submit(task);
        }
        Thread.sleep(10000);
    }

    @Test
    public void testScheduleExecutorService () throws InterruptedException {
        Runnable task = new Runnable(){
            @Override
            public void run() {
                logger.debug("hello ScheduleExecutorService");
            }
        };
        scheduleService.scheduleAtFixedRate(task, 10000,3000,TimeUnit.MILLISECONDS);
        Thread.sleep(300000);
    }

    // spring 普通线程池

    @Test
    public void testSpringTaskExecutor () throws InterruptedException {
        Runnable task = new Runnable(){
            @Override
            public void run() {
                logger.debug("hello SpringTaskExecutor");
            }
        };
        for (int i = 0; i < 10; i++) {
            taskExecutor.submit(task);
        }
        Thread.sleep(10000);
    }

    // spring的定时任务
    @Test
    public void testSpringSchedulingTaskExecutor () throws InterruptedException {
        Runnable task = new Runnable(){
            @Override
            public void run() {
                logger.debug("hello SpringSchedulingTaskExecutor");
            }
        };

        Date start = new Date(System.currentTimeMillis() + 1000);
        taskScheduler.scheduleAtFixedRate(task,start,1000);
        Thread.sleep(30000);
    }

    // 调用@Async 注解的方法
    @Test
    public void testAsync () throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            helloService.execute();
        }
        Thread.sleep(30000);
    }

    // 调用@Scheduled 注解的方法
    @Test
    public void testScheduled () throws InterruptedException {
        Thread.sleep(30000);
    }
}
