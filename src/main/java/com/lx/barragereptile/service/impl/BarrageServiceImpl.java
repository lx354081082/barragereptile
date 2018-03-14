package com.lx.barragereptile.service.impl;

import com.lx.barragereptile.dto.BarrageDTO;
import com.lx.barragereptile.dto.UserBarrageDTO;
import com.lx.barragereptile.pojo.DouyuBarrage;
import com.lx.barragereptile.pojo.PandaBarrage;
import com.lx.barragereptile.repository.DouyuBarrageRepository;
import com.lx.barragereptile.repository.PandaBarrageRepository;
import com.lx.barragereptile.service.BarrageService;
import com.lx.barragereptile.util.BarrageConstant;
import com.lx.barragereptile.util.PageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
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
        if (where.equals(BarrageConstant.DOUYU)) {
            List<Object[]> objects = douyuBarrageRepository.selectByUid(uid, offset, limit);
            toDTO(userBarrageDTOList, objects);
            count = douyuBarrageRepository.selectCountByUid(uid);
        }
        if (where.equals(BarrageConstant.PANDA)) {
            List<Object[]> objects = pandaBarrageRepository.selectByUid(uid, offset, limit);
            toDTO(userBarrageDTOList, objects);
            count = pandaBarrageRepository.selectCountByUid(uid);
        }


        PageBean<UserBarrageDTO> pageBean = new PageBean<>();
        pageBean.setRows(userBarrageDTOList);
        pageBean.setTotal(count);
        return pageBean;
    }

    @Override
    public PageBean<UserBarrageDTO> selectBarrage(String where, Integer roomid, String who) {

//        PageRequest pageRequest = new PageRequest();

        return null;
    }

    @Override
    public PageBean<BarrageDTO> findByRoomid(String where, String roomid, Integer offset, Integer limit) {
        PageBean<BarrageDTO> userBarrageDTOPageBean = new PageBean<>();
        List<BarrageDTO> dtoList = new ArrayList<>();

        //计算页数
        Integer page = 1;
        if (offset != 0) {
            page = (offset + 1) / limit;
        }
        //排序
        Sort sort = new Sort(Sort.Direction.DESC,"date");
        //分页
        PageRequest pageRequest = new PageRequest(page, limit, sort);

        if (where.equals(BarrageConstant.DOUYU)) {
            //条件封装查询
            douyuFind(roomid, userBarrageDTOPageBean, dtoList, pageRequest);
        }
        if (where.equals(BarrageConstant.PANDA)) {
            //条件封装查询
            pandaFind(roomid, userBarrageDTOPageBean, dtoList, pageRequest);
        }

        userBarrageDTOPageBean.setRows(dtoList);

        return userBarrageDTOPageBean;
    }

    private void pandaFind(String roomid, PageBean<BarrageDTO> userBarrageDTOPageBean, List<BarrageDTO> dtoList, PageRequest pageRequest) {
        Specification<PandaBarrage> specification = new Specification<PandaBarrage>() {
            @Override
            public Predicate toPredicate(Root<PandaBarrage> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate roomid1 = criteriaBuilder.equal(root.get("roomid").as(String.class), roomid);
                return criteriaBuilder.and(roomid1);
            }
        };
        Page<PandaBarrage> all = pandaBarrageRepository.findAll(specification, pageRequest);
        //总页数
        userBarrageDTOPageBean.setTotal((int) all.getTotalElements());

        for (PandaBarrage p : all.getContent()) {
            BarrageDTO barrageDTO = new BarrageDTO();
            barrageDTO.setUid(p.getRid());
            barrageDTO.setDate(p.getDate());
            barrageDTO.setUname(p.getNickname());
            barrageDTO.setTxt(p.getContent());
            barrageDTO.setRoomid(p.getRoomid());

            dtoList.add(barrageDTO);
        }
    }

    private void douyuFind(String roomid, PageBean<BarrageDTO> userBarrageDTOPageBean, List<BarrageDTO> dtoList, PageRequest pageRequest) {
        Specification<DouyuBarrage> specification = new Specification<DouyuBarrage>() {
            @Override
            public Predicate toPredicate(Root<DouyuBarrage> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate roomid1 = criteriaBuilder.equal(root.get("roomid").as(String.class), roomid);
                return criteriaBuilder.and(roomid1);
            }
        };

        Page<DouyuBarrage> all = douyuBarrageRepository.findAll(specification, pageRequest);
        //总页数
        userBarrageDTOPageBean.setTotal((int) all.getTotalElements());
        //结果集
        List<DouyuBarrage> content = all.getContent();
        for (DouyuBarrage d : content) {
            BarrageDTO barrageDTO = new BarrageDTO();
            barrageDTO.setTxt(d.getTxt());
            barrageDTO.setDate(d.getDate());
            barrageDTO.setRoomid(d.getRoomid());
            barrageDTO.setUid(d.getUid());
            barrageDTO.setUname(d.getUname());

            dtoList.add(barrageDTO);
        }
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
