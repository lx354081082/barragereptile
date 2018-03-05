package com.lx.barragereptile.service;

import com.lx.barragereptile.dto.UserDTO;
import com.lx.barragereptile.dto.UserDetailDTO;
import com.lx.barragereptile.util.PageBean;

public interface UserService {
    PageBean<UserDTO> selectPandaByName(PageBean<UserDTO> pageBean, String username, Integer offset, Integer limit);

    PageBean<UserDTO> selectDouyuByName(PageBean<UserDTO> pageBean, String username, Integer offset, Integer limit);

    int barrageToUser();

    UserDetailDTO selectByWhereAndUid(String where, String id);

    PageBean<UserDTO> selectUserByWhere(String where, String username, Integer offset, Integer limit);
}
