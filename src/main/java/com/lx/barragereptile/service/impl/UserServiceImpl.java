package com.lx.barragereptile.service.impl;

import com.lx.barragereptile.dto.UserDTO;
import com.lx.barragereptile.pojo.DouyuBarrage;
import com.lx.barragereptile.repository.DouyuBarrageRepository;
import com.lx.barragereptile.service.UserService;
import com.lx.barragereptile.util.PageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    DouyuBarrageRepository douyuBarrageRepository;


    @Override
    public PageBean<UserDTO> selectByName(PageBean<UserDTO> pageBean, String username, Integer offset, Integer limit) {
        PageRequest pageRequest = new PageRequest(1, 10);
        List<DouyuBarrage> douyuBarrages = douyuBarrageRepository.selectByName(username, offset, limit);

        pageBean.setRows(toUserDTO(douyuBarrages));
        pageBean.setTotal(douyuBarrageRepository.selectCountByName(username));
//        pageBean.setTotal(douyuBarrages.);
        return pageBean;
    }

    private List<UserDTO> toUserDTO(List<DouyuBarrage> douyuBarrages) {
        ArrayList<UserDTO> userDTOS = new ArrayList<>();
        for (DouyuBarrage d : douyuBarrages) {
            UserDTO userDTO = new UserDTO();
            userDTO.setUserid(d.getUid());
            userDTO.setUsername(d.getUname());
            userDTO.setWhere("斗鱼");
            userDTOS.add(userDTO);
        }
        return userDTOS;
    }
}
