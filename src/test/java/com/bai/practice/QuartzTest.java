package com.bai.practice;

import org.junit.jupiter.api.Test;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = PracticeApplication.class)
public class QuartzTest {

    @Autowired
    private Scheduler scheduler;

    @Test
    public void testDeleteJbo() throws SchedulerException {
        boolean b = scheduler.deleteJob(new JobKey("helloJob","helloGrop"));
        System.out.println(b);
    }
}
