package com.lx.barragereptile.repository;


import com.lx.barragereptile.pojo.PandaBarrage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PandaBarrageRepository extends JpaRepository<PandaBarrage, String> {

    @Query(value = "select * from panda_barrage where nickname like %?1% GROUP BY nickname limit ?2,?3 ", nativeQuery = true)
    List<PandaBarrage> selectByName(String username, Integer offset, Integer limit);
    @Query(value = "SELECT COUNT(1) FROM (select nickname from panda_barrage AS a where nickname like %?1% GROUP BY nickname) AS b", nativeQuery = true)
    int selectCountByName(String username);
}
