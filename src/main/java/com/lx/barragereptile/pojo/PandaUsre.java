package com.lx.barragereptile.pojo;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
@DynamicUpdate
public class PandaUsre {
    @Id
    private String uId;
    private String uName;
    private String level;
}
