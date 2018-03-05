package com.lx.barragereptile.repository;

import com.lx.barragereptile.dto.UserDTO;
import com.lx.barragereptile.pojo.DouyuBarrage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface DouyuBarrageRepository extends JpaRepository<DouyuBarrage, String> {

    /**
     * 模糊查询用户名
     * nativeQuery = true 原生sql查询
     * @param name 姓名
     * @param start 起始角标
     * @param limit 步长
     * @return 返回List<Object[]> (用户id,用户名)
     */
    @Query(value = "select uid,uname from douyu_barrage where uname like %?1% GROUP BY uname limit ?2,?3 ", nativeQuery = true)
    List<Object[]> selectByName(String name, int start, int limit);

    /**
     * 模糊查询该名字数量
     */
    @Query(value = "SELECT COUNT(1) FROM (select uname from douyu_barrage AS a where uname like %?1% GROUP BY uname) AS b", nativeQuery = true)
    int selectCountByName(String name);


    @Query(value = "SELECT txt,roomid,`date` FROM `douyu_barrage` WHERE uid=?1 ORDER BY id DESC LIMIT ?2,?3", nativeQuery = true)
    List<Object[]> selectByUid(String uid, int start, int limit);

    @Query(value = "SELECT COUNT(1) FROM `douyu_barrage` WHERE uid=?1", nativeQuery = true)
    int selectCountByUid(String uid);
}
