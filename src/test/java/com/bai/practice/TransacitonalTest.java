package com.bai.practice;

import com.bai.practice.service.HelloService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;


@SpringBootTest
@ContextConfiguration(classes = PracticeApplication.class)
public class TransacitonalTest {

    @Autowired
    private HelloService service;

    @Test
    public void testSave1 () {
        Object obj = service.save1();
        System.out.println(obj);
    }

    @Test
    public void testSave2 () {
        Object obj = service.save2();
        System.out.println(obj);
    }

}
