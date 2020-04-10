package com.bai.practice.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

public class CommunityUtil {

    // 生产随机数
    public static String genUUID() {
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    // MD5 只能加密不能解密
    public static String md5(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }
}
