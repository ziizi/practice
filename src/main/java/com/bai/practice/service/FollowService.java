package com.bai.practice.service;

import com.bai.practice.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class FollowService {
    @Autowired
    private RedisTemplate redisTemplate;

    // 关注
    public void follow (int userId,int entityType,int entityId) {

        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
                // 开启事务
                redisOperations.multi();
                redisOperations.opsForZSet().add(followeeKey,entityId,System.currentTimeMillis());
                redisOperations.opsForZSet().add(followerKey,userId,System.currentTimeMillis());

                return redisOperations.exec();
            }
        });
    }

    // 取消关注
    public void unFollow (int userId,int entityType,int entityId) {

        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
                // 开启事务
                redisOperations.multi();
                redisOperations.opsForZSet().remove(followeeKey,entityId);
                redisOperations.opsForZSet().remove(followerKey,userId);

                return redisOperations.exec();
            }
        });
    }

    // 查询某个用户关注实体的数量
    public long findFolloweeCount(int userId, int entityType) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
        return redisTemplate.opsForZSet().zCard(followeeKey);
    }

    // 查询实体的关注数量
    public long findFollowerCount(int entityType,int entitiId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType,entitiId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }


    // 查询用户是否关注某个实体
    public boolean hasFollowed(int userId,int entityType,int entityId){
        String followerKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
        return redisTemplate.opsForZSet().score(followerKey,entityId) != null;
    }
}
