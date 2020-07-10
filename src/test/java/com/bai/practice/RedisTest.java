package com.bai.practice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.ContextConfiguration;

import java.util.concurrent.TimeUnit;

@SpringBootTest
@ContextConfiguration(classes = PracticeApplication.class)
public class RedisTest {

    @Autowired
    private RedisTemplate<String ,Object> template;

    @Test
    public void testString () {
        String key = "test:count";
        template.opsForValue().set(key,1);
        System.out.println(template.opsForValue().get(key));
        System.out.println(template.opsForValue().increment(key));
        System.out.println(template.opsForValue().decrement(key));
    }

    @Test
    public void testHash () {
        String key = "test:user";
        template.opsForHash().put(key,"id",1);
        template.opsForHash().put(key,"name","张三");
        System.out.println(template.opsForHash().get(key,"id"));
        System.out.println(template.opsForHash().get(key,"name"));
    }

    @Test
    public void testList () {
        String key = "test:ids";
        template.opsForList().leftPush(key,101);
        template.opsForList().leftPush(key,102);
        template.opsForList().leftPush(key,103);
        System.out.println(template.opsForList().size(key)); // 查看长度
        System.out.println(template.opsForList().index(key,0));
        System.out.println(template.opsForList().range(key,0,2));

        System.out.println(template.opsForList().rightPop(key));
        System.out.println(template.opsForList().rightPop(key));
        System.out.println(template.opsForList().rightPop(key));
    }

    @Test
    public void testSet () {
        String key = "test:teachers";
        template.opsForSet().add(key,"liubei","guanyu","zhangfei");
        System.out.println(template.opsForSet().size(key));
        System.out.println(template.opsForSet().pop(key)); // 随机弹出一个数
        System.out.println(template.opsForSet().members(key)); // 随机弹出一个数
    }

    @Test
    public void testSortSet () {
        String key = "test:students";
       template.opsForZSet().add(key,"wukong",90);
       template.opsForZSet().add(key,"shasen",70);
       template.opsForZSet().add(key,"bajie",80);
        template.opsForZSet().add(key,"bailong",50);

        System.out.println(template.opsForZSet().zCard(key)); // 有序集合有多少数据
        System.out.println(template.opsForZSet().score(key,"bajie")); // 查看分数
        System.out.println(template.opsForZSet().rank(key,"bajie")); // 查看排名，默认由小到大的排名
        System.out.println(template.opsForZSet().reverseRank(key,"bajie")); // 查看排名，由大到小的排名
        System.out.println(template.opsForZSet().range(key,0,2)); // 查看前三名 默认由小到大排
        System.out.println(template.opsForZSet().reverseRange(key,0,2)); // 查看前三名 默认由小到大排
    }


    @Test
    public void testAll () {
        String key = "test:students";
        template.delete(key);

        System.out.println(template.hasKey(key)); // 判断是否存在
        template.expire("test:teachers",10, TimeUnit.SECONDS);
    }

    // 多次访问同一个key，以绑定的方法操作
    @Test
    public void testBoundOperations(){
        String key = "test:count";
        BoundValueOperations operations = template.boundValueOps(key);
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        System.out.println(operations.get());
    }

    // 编程式事务
    @Test
    public void testTransactional(){

        Object obj = template.execute(new SessionCallback<Object>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> redisOperations) throws DataAccessException {
                String key = "test:tx";
                redisOperations.multi(); // 开启事务
                template.opsForSet().add(key,"id");
                template.opsForSet().add(key,"name");
                template.opsForSet().add(key,"pswor");
                template.opsForSet().add(key,"id2");

                // 此查询无效，底层是吧命令放在队列里面，在中间查询没有意义
                System.out.println(template.opsForSet().members(key));

                return redisOperations.exec();
            }
        });
        System.out.println(obj);
    }


    // test HyperLogLog
    @Test
    public void testHyperLogLog () {
        String reidsKey = "test:hll:01";
        for (int i = 1; i <= 10000; i++) {
            template.opsForHyperLogLog().add(reidsKey,i);
        }
        long size = template.opsForHyperLogLog().size();
        System.out.println(size);
    }

    //
    @Test
    public void testHyperLogLogUnion () {
        String redisKey2 = "test:hll:02";
        for (int i = 1; i <= 10000; i++) {
            template.opsForHyperLogLog().add(redisKey2,i);
        }

        String redisKey3 = "test:hll:03";
        for (int i = 5001; i <= 15000; i++) {
            template.opsForHyperLogLog().add(redisKey3,i);
        }

        String redisKey4 = "test:hll:04";
        for (int i = 10001; i <= 20000; i++) {
            template.opsForHyperLogLog().add(redisKey4,i);
        }

        String redisKeyUnion = "test:hll:union";
        template.opsForHyperLogLog().union(redisKeyUnion, redisKey2, redisKey3, redisKey4);
        System.out.println(template.opsForHyperLogLog().size(redisKeyUnion));
    }

    @Test
    public void testBitmap() {
        String redisKey = "test:bt:01";
        template.opsForValue().setBit(redisKey,0,true);
        template.opsForValue().setBit(redisKey,2,true);
        template.opsForValue().setBit(redisKey,4,true);

        System.out.println(template.opsForValue().getBit(redisKey,0));
        System.out.println(template.opsForValue().getBit(redisKey,2));
        System.out.println(template.opsForValue().getBit(redisKey,4));

        // 统计为true的个数
        Object obj = template.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                return redisConnection.bitCount(redisKey.getBytes());
            }
        });
        System.out.println(obj);
    }

    // 统计3组数据的boolan，做or运算
    @Test
    public void testBitmapOperation() {
        String redisKey2 = "test:bt:02";
        template.opsForValue().setBit(redisKey2,0,true);
        template.opsForValue().setBit(redisKey2,1,true);
        template.opsForValue().setBit(redisKey2,2,true);

        String redisKey3 = "test:bt:03";
        template.opsForValue().setBit(redisKey3,2,true);
        template.opsForValue().setBit(redisKey3,3,true);
        template.opsForValue().setBit(redisKey3,4,true);

        String redisKey4 = "test:bt:04";
        template.opsForValue().setBit(redisKey4,4,true);
        template.opsForValue().setBit(redisKey4,5,true);
        template.opsForValue().setBit(redisKey4,6,true);


        String redisKeyOr = "test:bt:or";
        Object obj = template.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                 redisConnection.bitOp(RedisStringCommands.BitOperation.OR,
                        redisKeyOr.getBytes(),redisKey2.getBytes(), redisKey3.getBytes(), redisKey4.getBytes());
                return redisConnection.bitCount(redisKeyOr.getBytes());
            }
        });
        System.out.println(obj);

        System.out.println(template.opsForValue().getBit(redisKeyOr,0));
        System.out.println(template.opsForValue().getBit(redisKeyOr,1));
        System.out.println(template.opsForValue().getBit(redisKeyOr,2));
        System.out.println(template.opsForValue().getBit(redisKeyOr,3));
        System.out.println(template.opsForValue().getBit(redisKeyOr,4));
        System.out.println(template.opsForValue().getBit(redisKeyOr,5));
        System.out.println(template.opsForValue().getBit(redisKeyOr,6));
    }

}

