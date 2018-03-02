package com.lx.barragereptile.repository;

import com.lx.barragereptile.pojo.DouyuBarrage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DouyuBarrageRepository extends JpaRepository<DouyuBarrage, String> {
    //nativeQuery = true 原生sql查询
    @Query(value = "select * from douyu_barrage where uname like %?1% GROUP BY uname limit ?2,?3 ", nativeQuery = true)
    public List<DouyuBarrage> selectByName(String name, int start, int limit);
    @Query(value = "SELECT COUNT(1) FROM (select uname from douyu_barrage AS a where uname like %?1% GROUP BY uname) AS b", nativeQuery = true)
    public int selectCountByName(String name);
}
