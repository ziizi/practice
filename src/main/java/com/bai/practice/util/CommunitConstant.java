package com.bai.practice.util;

public interface CommunitConstant {
    int ACTIVATION_SUCCESS = 0; // 激活成功
    int ACTIVATION_REPEAT = 1;
    int ACTIVATION_FAIL = 2;

    // 默认状态下超时时间12 小时
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;
    /**
     * 记住我超时时间
     */
    int REMEMBER_EXPIRED_SECONDS = 3600 * 12 * 100;
}
