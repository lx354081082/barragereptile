package com.lx.barragereptile.service.impl;

import com.lx.barragereptile.service.RedisService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Log4j
public class RedisServiceImpl implements RedisService {
    @Autowired
    RedisTemplate redisTemplate;
    @Override
    public void saveBarrage(String s, String val) {
        Long aLong = redisTemplate.opsForList().leftPush(s, val);
        log.info(aLong+"redis");
    }
}
