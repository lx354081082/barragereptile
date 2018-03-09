package com.lx.barragereptile.repository;

import com.lx.barragereptile.pojo.DouyuUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DouyuUserRepository extends JpaRepository<DouyuUser, String> {
//    /**
//     * 将弹幕表中的用户数据更新添加到user表中
//     * @param start 起始角标
//     * @param lens 步长
//     */
//    @Modifying
//    @Query(value = "REPLACE INTO `douyu_user` (SELECT a.uid, a.`level`, a.uname FROM ( SELECT id, uid, uname, `level` FROM `douyu_barrage` LIMIT ?1,?2 ) a GROUP BY uid, uname)", nativeQuery = true)
//    void barrageToUser(int start, int lens);

    @Query(value = "SELECT u_id,u_name FROM `douyu_user` WHERE u_name LIKE %?1% LIMIT ?2,?3", nativeQuery = true)
    List<Object[]> selectByName(String username, Integer offset, Integer limit);

    @Query(value = "SELECT COUNT(1) FROM `douyu_user` WHERE u_name LIKE %?1%", nativeQuery = true)
    int selectCountByName(String username);
}
