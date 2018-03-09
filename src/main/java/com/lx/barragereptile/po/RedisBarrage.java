package com.lx.barragereptile.po;

import lombok.Data;

@Data
public class RedisBarrage{

    public RedisBarrage(String where, Object barrage) {
        this.where = where;
        this.barrage = barrage;
    }

    private String where;
    private Object barrage;

}
