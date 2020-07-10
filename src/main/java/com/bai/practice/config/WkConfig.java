package com.bai.practice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;

// 在服务启动的时候，初始化wk的文件存放的目录
@Configuration
public class WkConfig {


    private static final Logger logger = LoggerFactory.getLogger(WkConfig.class);

    @Value("${wk.image.storage}")
    private String wkStoragePath;

    @PostConstruct
    public void init () {
        // 创建wkmulu
        File file = new File(wkStoragePath);
        if (!file.exists()) {
            file.mkdir();
            logger.info("创建wk目录成功"+wkStoragePath);
        }
    }
}
