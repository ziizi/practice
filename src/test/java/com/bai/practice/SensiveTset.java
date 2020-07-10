package com.bai.practice;

import com.bai.practice.util.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = PracticeApplication.class)
public class SensiveTset {
    @Autowired
    private SensitiveFilter filter;

    @Test
    public void test (){
        String string = "可以嫖娼和吸毒";
        System.out.println(filter.filter(string));
    }
}
