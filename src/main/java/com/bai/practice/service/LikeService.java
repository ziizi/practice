package com.bai.practice.service;

import com.bai.practice.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    @Autowired
    private RedisTemplate redisTemplate;

    // 点赞操作
    public void like (int userId,int entityType,int entityId, int entityUserId){

        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);
                boolean isMember = redisOperations.opsForSet().isMember(entityLikeKey,userId);// 需要放在事务的外面
                redisOperations.multi(); // 开启事务
                if (isMember) {
                    redisOperations.opsForSet().remove(entityLikeKey,userId); // 在里面，就移除该点赞人
                    redisOperations.opsForValue().decrement(userLikeKey);
                } else {
                    redisOperations.opsForSet().add(entityLikeKey,userId);
                    redisOperations.opsForValue().increment(userLikeKey);
                }

                return redisOperations.exec(); // 事务的结束
            }
        });

    }

    // 查询点赞的数量
    public long findEntityLikeCount (int entityType,int entityId){
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    // 查询某个对某个实体的点赞状态
    public int findEntityLikeStatus (int userId, int entityType,int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey,userId) ? 1 : 0;
    }

    // 查询某个用户获得赞
    public int findUserCount(int userId) {
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer count = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        return count == null ? 0 : count.intValue();
    }
}
