package com.lx.barragereptile.service;

import com.lx.barragereptile.dto.UserBarrageDTO;
import com.lx.barragereptile.util.PageBean;

public interface BarrageService {

    PageBean<UserBarrageDTO> selectByUid(String where, String uid, Integer offset, Integer limit);

    PageBean<UserBarrageDTO> selectBarrage(String where, Integer roomid, String who);
}
