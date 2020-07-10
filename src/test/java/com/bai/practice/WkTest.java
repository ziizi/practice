package com.bai.practice;


import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;


public class WkTest {
    public static void main(String[] args) {
        String cmd = "F:/study/wkhtmltopdf/bin/wkhtmltoimage --quality 75 https://www.nowcoder.com d:\\work\\data\\wk_image\\2.png";
        try {
            Runtime.getRuntime().exec(cmd);
            System.out.println("ok");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
