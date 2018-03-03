package com.lx.barragereptile.service.impl;

import com.lx.barragereptile.dto.UserDTO;
import com.lx.barragereptile.pojo.DouyuBarrage;
import com.lx.barragereptile.pojo.Notes;
import com.lx.barragereptile.pojo.PandaBarrage;
import com.lx.barragereptile.repository.DouyuBarrageRepository;
import com.lx.barragereptile.repository.DouyuUserRepository;
import com.lx.barragereptile.repository.NotesRepository;
import com.lx.barragereptile.repository.PandaBarrageRepository;
import com.lx.barragereptile.service.UserService;
import com.lx.barragereptile.util.PageBean;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Log4j
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    DouyuBarrageRepository douyuBarrageRepository;
    @Autowired
    PandaBarrageRepository pandaBarrageRepository;
    @Autowired
    DouyuUserRepository douyuUserRepository;
    @Autowired
    NotesRepository notesRepository;


    @Override
    public PageBean<UserDTO> selectPandaByName(PageBean<UserDTO> pageBean, String username, Integer offset, Integer limit) {
        List<PandaBarrage> pandaBarrages = pandaBarrageRepository.selectByName(username, offset, limit);
        ArrayList<UserDTO> userDTOS = new ArrayList<>();
        for (PandaBarrage p : pandaBarrages) {
            UserDTO userDTO = new UserDTO();
            userDTO.setUserid(p.getRid());
            userDTO.setUsername(p.getNickname());
            userDTO.setWhere("熊猫");
            userDTOS.add(userDTO);
        }
        pageBean.setRows(userDTOS);
        pageBean.setTotal(pandaBarrageRepository.selectCountByName(username));
        return pageBean;
    }

    @Override
    public PageBean<UserDTO> selectDouyuByName(PageBean<UserDTO> pageBean, String username, Integer offset, Integer limit) {
        List<DouyuBarrage> douyuBarrages = douyuBarrageRepository.selectByName(username, offset, limit);
        ArrayList<UserDTO> userDTOS = new ArrayList<>();
        for (DouyuBarrage d : douyuBarrages) {
            UserDTO userDTO = new UserDTO();
            userDTO.setUserid(d.getUid());
            userDTO.setUsername(d.getUname());
            userDTO.setWhere("斗鱼");
            userDTOS.add(userDTO);
        }
        pageBean.setRows(userDTOS);
        pageBean.setTotal(douyuBarrageRepository.selectCountByName(username));
        return pageBean;
    }

    @Override
    @Transactional(readOnly = false)
    public int barrageToUser() {
        Notes notes = notesRepository.findOne("douyuBarrage");
        if (notes == null) {
            notes = new Notes();
            notes.setKey("douyuBarrage");
            notes.setVal("0");
        }
        long count = douyuBarrageRepository.count();

        Integer val = Integer.parseInt(notes.getVal())+100000;
        if (count <= val) {
            return 0;
        }

        notes.setVal(val.toString());
        log.info("=======>"+val);
        douyuUserRepository.narrageToUser(Integer.parseInt(notes.getVal()), 100000);

        notesRepository.save(notes);

        return 1;
    }

}
