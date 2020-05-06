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

    /**
     * 实体类型 帖子
     */
    int ENTITY_TYPE_POST = 1;
    /**
     * 实体类型 评论
     */
    int ENTITY_TYPE_COMMENT = 2;

    /**
     * 实体类型 用户
     */
    int ENTITY_TYPE_USER = 3;

    /**
     * kafka主题 评论
     */
    String TOPIC_COMMENT = "comment";

    /**
     * kafka主题 点赞
     */
    String TOPIC_LIKE = "like";

    /**
     * kafka主题 关注
     */
    String TOPIC_FOLLOW = "follow";


    // 删除贴子
    String TOPIC_DELETE = "delete";

    String TOPIC_PUBLISH = "publish";

    // 分享
    String TOPIC_SHARE = "share";

    /**
     * 系统的id
     */

    int SYSTEM_ID = 1;

    /**
     *
     * 权限 普通用户
     */
    String AUTHORITY_USER = "user";

    /**
     *
     * 权限 管理员
     */
    String AUTHORITY_ADMIN = "admin";

    /**
     *
     * 权限 版主
     */
    String AUTHORITY_MODERATOR = "moderator";
}

