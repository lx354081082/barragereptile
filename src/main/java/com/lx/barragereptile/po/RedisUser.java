package com.lx.barragereptile.po;

import lombok.Data;

@Data
public class RedisUser {
    public RedisUser(String uid, Integer level) {
        this.uid = uid;
        this.level = level;
    }

    private String uid;
    private Integer level;
}
