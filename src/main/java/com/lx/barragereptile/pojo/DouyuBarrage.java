package com.lx.barragereptile.pojo;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Data
@DynamicUpdate
public class DouyuBarrage {
    @Id
    private String id;
    private String uid;
    private String uname;
    private String txt;
    private String roomid;
    private Date date;
    private Integer level;
}
