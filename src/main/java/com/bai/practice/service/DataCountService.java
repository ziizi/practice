package com.bai.practice.service;

import com.bai.practice.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class DataCountService {

    @Autowired
    private RedisTemplate redisTemplate;

    private SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

    // 将制定的IP记录UV
    public void recodeUv(String ip) {
        String redisKey = RedisKeyUtil.getUvKey(df.format(new Date()));
        redisTemplate.opsForHyperLogLog().add(redisKey,ip);
    }

    // 统计时间范围内UV
    public long calculateUv (Date start,Date end) {
        if (start == null || end == null){
            throw new IllegalArgumentException("参数不能为空");
        }

        // 整理范围内的日期
        List<String> list = new ArrayList();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        while (!calendar.getTime().after(end)){
            String redisKey = RedisKeyUtil.getUvKey(df.format(calendar.getTime()));
            list.add(redisKey);
            calendar.add(Calendar.DATE,1);
        }

        String redisKey = RedisKeyUtil.getUvKey(df.format(start),df.format(end));
        redisTemplate.opsForHyperLogLog().union(redisKey,list.toArray());

        return redisTemplate.opsForHyperLogLog().size(redisKey);
    }

    // 将指定用户记录到dau
    public void recodeDAU (int userId) {
        String key = RedisKeyUtil.getDauKey(df.format(new Date()));
        redisTemplate.opsForValue().setBit(key,userId,true);
    }

    // 统计范围内dau
    public long calculateDau(Date start,Date end){
        if (start == null || end == null){
            throw new IllegalArgumentException("参数不能为空");
        }

        // 整理范围内的日期
        List<byte[]> list = new ArrayList();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        while (!calendar.getTime().after(end)){
            String redisKey = RedisKeyUtil.getDauKey(df.format(calendar.getTime()));
            list.add(redisKey.getBytes());
            calendar.add(Calendar.DATE,1);
        }

        // or 运算
        return (long) redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection con) throws DataAccessException {
                String redisKey = RedisKeyUtil.getDauKey(df.format(start),df.format(end));
                con.bitOp(RedisStringCommands.BitOperation.OR,
                        redisKey.getBytes(),list.toArray(new byte[0][0])
                        );
                return con.bitCount(redisKey.getBytes());
            }
        });
    }
}
