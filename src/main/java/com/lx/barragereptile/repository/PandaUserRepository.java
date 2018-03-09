package com.lx.barragereptile.repository;

import com.lx.barragereptile.pojo.PandaUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PandaUserRepository extends JpaRepository<PandaUser, String> {
//    /**
//     * 将弹幕表中的用户数据更新添加到user表中
//     * @param start 起始角标
//     * @param lens 步长
//     */
//    @Modifying
//    @Query(value = "REPLACE INTO `panda_user` (SELECT a.rid, a.`level`, a.nickname FROM ( SELECT rid,nickname,`level` FROM `panda_barrage` LIMIT ?1,?2 ) a GROUP BY rid, nickname)", nativeQuery = true)
//    void barrageToUser(int start, int lens);

    @Query(value = "SELECT u_id,u_name FROM `panda_user` WHERE u_name LIKE %?1% LIMIT ?2,?3", nativeQuery = true)
    List<Object[]> selectByName(String username, Integer offset, Integer limit);

    @Query(value = "SELECT COUNT(1) FROM `panda_user` WHERE u_name LIKE %?1%", nativeQuery = true)
    int selectCountByName(String username);
}
