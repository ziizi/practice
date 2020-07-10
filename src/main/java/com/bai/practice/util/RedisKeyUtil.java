package com.bai.practice.util;

public class RedisKeyUtil {

    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";

    private static final String PREFIX_FOLLOWEE = "folowee";
    private static final String PREFIX_FOLLOWER = "folower";

    private static final String PREFIX_KAPTCHA = "kaptcha";

    private static final String PREFIX_TICKET = "ticket";

    private static final String PREFIX_USER = "user";

    private static final String PREFIX_UV = "uv"; // 每天访问
    private static final String PREFIX_DAU = "dau"; // 日活跃

    private static final String PREFIX_POST = "post"; // 热帖排行

    // 点赞的人存在 -> set (userId)
    public static String getEntityLikeKey(int entityType,int entityID){
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityID;
    }

    // 某个用户的赞
    // like:user:userid
    public static String getUserLikeKey(int userId){
        return PREFIX_USER_LIKE + SPLIT + PREFIX_USER_LIKE;
    }

    // 某个用户关注的实体
    // followee:userId:entityType -> zset(entityid,date())
    public static String getFolloweeKey(int userId,int entityType){
        return PREFIX_FOLLOWEE + SPLIT +userId + SPLIT + entityType;
    }

    // 某个实体拥有的粉丝
    // follower:entityType:entityId -> zset(userId,date)
    public static String getFollowerKey (int entityType,int entityId){
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT +entityId;
    }


    // 登录验证吗
    public static  String getKaptcha (String owner) {
        return PREFIX_KAPTCHA + SPLIT +owner;
    }

    // 登录凭证
    public static  String getTickeKey (String ticket) {
        return PREFIX_TICKET + SPLIT +ticket;
    }


    // 登录凭证
    public static  String getUserKey (int userId) {
        return PREFIX_USER + SPLIT +userId;
    }


    // 单日 uv
    public static  String getUvKey (String date) {
        return PREFIX_UV + SPLIT + date;
    }

    // 区间 uv
    public static  String getUvKey (String startDate,String endDate) {
        return PREFIX_UV + SPLIT + startDate + SPLIT + endDate;
    }


    // 单日活跃数 dau
    public static  String getDauKey (String date) {
        return PREFIX_DAU + SPLIT + date;
    }

    // 区间 活跃数  dau
    public static  String getDauKey (String startDate,String endDate) {
        return PREFIX_DAU + SPLIT + startDate + SPLIT + endDate;
    }

    // 区间 活跃数  dau
    public static  String getPostScoreKey () {
        return PREFIX_POST + SPLIT + "score";
    }

}
