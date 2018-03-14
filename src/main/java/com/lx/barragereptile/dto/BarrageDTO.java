package com.lx.barragereptile.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class BarrageDTO {
    private String uid;
    private String uname;
    private String txt;
    private String roomid;
    @JsonFormat(pattern="yyyy-MM-ddHH:mm:ss",timezone="GMT+8")
    private Date date;
}
