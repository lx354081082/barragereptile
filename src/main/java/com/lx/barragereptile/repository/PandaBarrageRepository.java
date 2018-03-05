package com.lx.barragereptile.repository;


import com.lx.barragereptile.pojo.PandaBarrage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PandaBarrageRepository extends JpaRepository<PandaBarrage, String> {
    /**
     * 模糊查询用户名
     * nativeQuery = true 原生sql查询
     * @param name 姓名
     * @param start 起始角标
     * @param limit 步长
     * @return 返回List<Object[]> (用户id,用户名)
     */
    @Query(value = "select rid,nickname from panda_barrage where nickname like %?1% GROUP BY nickname limit ?2,?3 ", nativeQuery = true)
    List<Object[]> selectByName(String username, Integer offset, Integer limit);

    /**
     * 模糊查询该名字数量
     */
    @Query(value = "SELECT COUNT(1) FROM (select nickname from panda_barrage AS a where nickname like %?1% GROUP BY nickname) AS b", nativeQuery = true)
    int selectCountByName(String username);

    @Query(value = "SELECT content,roomid,`date` FROM `panda_barrage` WHERE rid=?1 ORDER BY id DESC LIMIT ?2,?3", nativeQuery = true)
    List<Object[]> selectByUid(String uid, Integer offset, Integer limit);

    @Query(value = "SELECT COUNT(1) FROM `panda_barrage` WHERE rid=?1", nativeQuery = true)
    int selectCountByUid(String uid);
}
