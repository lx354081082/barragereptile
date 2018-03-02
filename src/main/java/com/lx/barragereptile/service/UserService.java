package com.lx.barragereptile.service;

import com.lx.barragereptile.dto.UserDTO;
import com.lx.barragereptile.util.PageBean;

public interface UserService {
    PageBean<UserDTO> selectByName(PageBean<UserDTO> pageBean, String username, Integer offset, Integer limit);
}
