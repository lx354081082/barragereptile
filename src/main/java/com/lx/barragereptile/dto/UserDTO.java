package com.lx.barragereptile.dto;

import lombok.Data;

@Data
public class UserDTO {
    public static final String DOUYU = "douyu";
    public static final String PANDA = "panda";

    private String userid;
    private String username;
    private String where;
}
