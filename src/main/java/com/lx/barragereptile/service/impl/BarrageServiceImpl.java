package com.lx.barragereptile.service.impl;

import com.lx.barragereptile.dto.UserBarrageDTO;
import com.lx.barragereptile.dto.UserDTO;
import com.lx.barragereptile.repository.DouyuBarrageRepository;
import com.lx.barragereptile.repository.PandaBarrageRepository;
import com.lx.barragereptile.service.BarrageService;
import com.lx.barragereptile.util.PageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class BarrageServiceImpl implements BarrageService {
    @Autowired
    DouyuBarrageRepository douyuBarrageRepository;
    @Autowired
    PandaBarrageRepository pandaBarrageRepository;

    @Override

    public PageBean<UserBarrageDTO> selectByUid(String where, String uid, Integer offset, Integer limit) {
        List<UserBarrageDTO> userBarrageDTOList = new ArrayList<>();
        int count = 0;
        if (where.equals(UserDTO.DOUYU)) {
            List<Object[]> objects = douyuBarrageRepository.selectByUid(uid, offset, limit);
            toDTO(userBarrageDTOList, objects);
            count = douyuBarrageRepository.selectCountByUid(uid);
        }
        if (where.equals(UserDTO.PANDA)) {
            List<Object[]> objects = pandaBarrageRepository.selectByUid(uid, offset, limit);
            toDTO(userBarrageDTOList, objects);
            count = pandaBarrageRepository.selectCountByUid(uid);
        }


        PageBean<UserBarrageDTO> pageBean = new PageBean<>();
        pageBean.setRows(userBarrageDTOList);
        pageBean.setTotal(count);
        return pageBean;
    }

    private void toDTO(List<UserBarrageDTO> userBarrageDTOList, List<Object[]> objects) {
        for (Object[] o : objects) {
            UserBarrageDTO userBarrageDTO = new UserBarrageDTO();
            userBarrageDTO.setTxt((String) o[0]);
            userBarrageDTO.setRoomid((String) o[1]);
            userBarrageDTO.setDate((Date) o[2]);
            userBarrageDTOList.add(userBarrageDTO);
        }
    }
}
