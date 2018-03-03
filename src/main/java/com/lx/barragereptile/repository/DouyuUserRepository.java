package com.lx.barragereptile.repository;

import com.lx.barragereptile.pojo.DouyuUsre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface DouyuUserRepository extends JpaRepository<DouyuUsre, String> {

    /**
     * 将弹幕表中的用户数据更新添加到user表中
     */
    @Modifying
    @Query(value = "REPLACE INTO `douyu_usre` (SELECT a.uid, a.`level`, a.uname FROM ( SELECT id, uid, uname, `level` FROM `douyu_barrage` LIMIT ?1,?2 ) a GROUP BY uid, uname)", nativeQuery = true)
    void narrageToUser(int start,int lens);
}
