package com.bai.practice;


import com.bai.practice.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@SpringBootTest
@ContextConfiguration(classes = PracticeApplication.class)
public class MailTest {

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void send (){
        mailClient.sendMail("baiguangquan@an-lang.com","Test","Welcome");
    }

    @Test
    public void sendThymeleaf (){
        Context context = new Context();
        context.setVariable("username","sunday");
        String string = templateEngine.process("/mail/demo",context);
        System.out.println(string);
        mailClient.sendMail("baiguangquan@an-lang.com","HTML",string);
    }
}
