package com.lx.barragereptile.service;

import com.lx.barragereptile.po.RedisBarrage;

public interface RedisService {

    int lPush(String barrage, RedisBarrage redisBarrage);

    RedisBarrage rpop(String barrage);

    void barrageAdd(String key);

    Integer getval(String key);

    void clean(String key);
}
