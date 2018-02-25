package com.lx.barragereptile.pojo;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Data
@DynamicUpdate
public class PandaBarrage {
    @Id
    private String id;
    private String rid;
    private String nickname;
    private String content;
    private String roomid;
    private Date date;
    private Integer level;
    private Integer identity;
    private Integer spidentity;
}
