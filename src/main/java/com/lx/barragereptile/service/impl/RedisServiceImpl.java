package com.lx.barragereptile.service.impl;

import com.alibaba.fastjson.JSON;
import com.lx.barragereptile.po.RedisBarrage;
import com.lx.barragereptile.service.RedisService;
import com.lx.barragereptile.util.BarrageConstant;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Log4j
public class RedisServiceImpl implements RedisService {
    @Autowired
    RedisTemplate redisTemplate;
//    @Override
//    public void saveBarrage(String s, String val) {
//        redisTemplate.convertAndSend("chat",val);
//    }

    /**
     * 消息入列
     */
    @Override
    public int lPush(String barrage, RedisBarrage redisBarrage) {
        Long aLong = redisTemplate.opsForList().leftPush(barrage, JSON.toJSONString(redisBarrage));
        return 0;
    }

    /**
     * 出列
     */
    @Override
    public RedisBarrage rpop(String barrage) {
        Object o = redisTemplate.opsForList().rightPop(barrage, 0, TimeUnit.SECONDS);
        RedisBarrage redisBarrage = JSON.parseObject(o.toString(), RedisBarrage.class);
        return redisBarrage;
    }

    /**
     * 根据key自增
     */
    @Override
    public void barrageAdd(String key) {
        redisTemplate.boundValueOps(key).increment(1);
    }

    @Override
    public Integer getval(String key) {
        /**
         * 在spring中redis的string 类型，当值采用JdkSerializationRedisSerializer序列化操作后
         * 使用get获取失败。这是redis的一个bug.有两种解决办法。
         * 第一，采用StringRedisSerializer序列化其值。
         * 第二，采用boundValueOps(key).get(0,-1)获取计数key的值
         */
        String s = redisTemplate.boundValueOps(key).get(0, -1);
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    @Override
    public void clean(String key) {
        redisTemplate.delete(key);
    }

}
