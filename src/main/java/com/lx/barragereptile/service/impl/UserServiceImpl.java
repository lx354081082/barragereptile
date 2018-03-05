package com.lx.barragereptile.service.impl;

import com.lx.barragereptile.dto.UserDTO;
import com.lx.barragereptile.dto.UserDetailDTO;
import com.lx.barragereptile.pojo.DouyuUser;
import com.lx.barragereptile.pojo.NoteDate;
import com.lx.barragereptile.pojo.PandaUser;
import com.lx.barragereptile.repository.DouyuBarrageRepository;
import com.lx.barragereptile.repository.DouyuUserRepository;
import com.lx.barragereptile.repository.NoteDateRepository;
import com.lx.barragereptile.repository.PandaBarrageRepository;
import com.lx.barragereptile.repository.PandaUserRepository;
import com.lx.barragereptile.service.UserService;
import com.lx.barragereptile.util.PageBean;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
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
    NoteDateRepository noteDateRepository;
    @Autowired
    PandaUserRepository pandaUserRepository;


    /**
     * 查找用户
     */
    @Override
    public PageBean<UserDTO> selectPandaByName(PageBean<UserDTO> pageBean, String username, Integer offset, Integer limit) {
        List<Object[]> objects = pandaBarrageRepository.selectByName(username, offset, limit);
        List<UserDTO> userDTOS = new ArrayList<>();
        toDTO(objects, userDTOS, "熊猫");
        pageBean.setRows(userDTOS);
        pageBean.setTotal(pandaBarrageRepository.selectCountByName(username));
        return pageBean;
    }

    @Override
    public PageBean<UserDTO> selectDouyuByName(PageBean<UserDTO> pageBean, String username, Integer offset, Integer limit) {
        List<Object[]> objects = douyuBarrageRepository.selectByName(username, offset, limit);
        List<UserDTO> userDTOS = new ArrayList<>();
        toDTO(objects, userDTOS, "斗鱼");
        pageBean.setRows(userDTOS);
        pageBean.setTotal(douyuBarrageRepository.selectCountByName(username));
        return pageBean;
    }

    /**
     * 返回值封装到DTO
     */
    private void toDTO(List<Object[]> objects, List<UserDTO> userDTOS, String where) {
        for (Object[] o : objects) {
            UserDTO userDTO = new UserDTO();
            userDTO.setUserid((String) o[0]);
            userDTO.setUsername((String) o[1]);
            userDTO.setWhere(where);
            userDTOS.add(userDTO);
        }
    }

    /**
     * 从弹幕表更新用户信息到用户表
     */
    @Override
    public int barrageToUser() {
        NoteDate noteDateDouyu = noteDateRepository.findOne("douyuBarrage");
        NoteDate noteDatePanda = noteDateRepository.findOne("pandaBarrage");


        //数据总量
        Integer countDouyu = Math.toIntExact(douyuBarrageRepository.count());
        Integer countPanda = Math.toIntExact(pandaBarrageRepository.count());

        Integer douyuVal=0;
        Integer pandaVal = 0;
        if (noteDateDouyu != null) {
            douyuVal = Integer.parseInt(noteDateDouyu.getVal());
        }
        if (noteDatePanda != null) {
            pandaVal = Integer.parseInt(noteDatePanda.getVal());
        }

        douyuUserRepository.barrageToUser(douyuVal, countDouyu-douyuVal);
        pandaUserRepository.barrageToUser(pandaVal, countPanda - pandaVal);

        NoteDate newNoteDateDouyu = new NoteDate();
        newNoteDateDouyu.setNoteId("douyuBarrage");
        newNoteDateDouyu.setVal(countDouyu.toString());
        newNoteDateDouyu.setDate(new Date());

        NoteDate newNoteDatePanda = new NoteDate();
        newNoteDatePanda.setNoteId("pandaBarrage");
        newNoteDatePanda.setVal(countPanda.toString());
        newNoteDatePanda.setDate(new Date());

        noteDateRepository.save(newNoteDateDouyu);
        noteDateRepository.save(newNoteDatePanda);

        return (countDouyu - douyuVal) + (countPanda - pandaVal);
    }

    /**
     * 通过用户id查询用户信息
     */
    @Override
    public UserDetailDTO selectByWhereAndUid(String where, String id) {
        UserDetailDTO userDetailDTO = null;
        try {

            if (where.equals(UserDTO.DOUYU)) {
                DouyuUser one = douyuUserRepository.findOne(id);
                userDetailDTO = new UserDetailDTO();
                userDetailDTO.setLevel(one.getLevel());
                userDetailDTO.setUid(one.getUId());
                userDetailDTO.setUname(one.getUName());
                userDetailDTO.setWhere(UserDTO.DOUYU);
            }
            if (where.equals(UserDTO.PANDA)) {
                PandaUser one = pandaUserRepository.findOne(id);
                userDetailDTO = new UserDetailDTO();
                userDetailDTO.setLevel(one.getLevel());
                userDetailDTO.setUid(one.getUId());
                userDetailDTO.setUname(one.getUName());
                userDetailDTO.setWhere(UserDTO.PANDA);
            }
        } catch (Exception e) {

        }

        //todo if==null 去弹幕表查

        if (userDetailDTO == null) {
            userDetailDTO = new UserDetailDTO();
        }
        return userDetailDTO;
    }

    /**
     * 用户表模糊查询 用户信息
     * @param where
     * @param username
     * @param offset
     * @param limit
     */
    @Override
    public PageBean<UserDTO> selectUserByWhere(String where, String username, Integer offset, Integer limit) {
        PageBean<UserDTO> pageBean = new PageBean<>();
        List<UserDTO> userDTOS = new ArrayList<>();
        if (where.equals(UserDTO.DOUYU)) {
            List<Object[]> objects = douyuUserRepository.selectByName(username, offset, limit);
            toDTO(objects,userDTOS,"斗鱼");
            pageBean.setRows(userDTOS);
            pageBean.setTotal(douyuUserRepository.selectCountByName(username));
        }
        if (where.equals(UserDTO.PANDA)) {
            List<Object[]> objects = pandaUserRepository.selectByName(username, offset, limit);
            toDTO(objects,userDTOS,"熊猫");
            pageBean.setRows(userDTOS);
            pageBean.setTotal(pandaUserRepository.selectCountByName(username));
        }
        return pageBean;
    }

}
