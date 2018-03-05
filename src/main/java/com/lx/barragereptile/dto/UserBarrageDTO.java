package com.lx.barragereptile.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class UserBarrageDTO {
    private String txt;
    @JsonFormat(pattern="yyyy-MM-ddHH:mm:ss",timezone="GMT+8")
    private Date date;
    private String roomid;
}
