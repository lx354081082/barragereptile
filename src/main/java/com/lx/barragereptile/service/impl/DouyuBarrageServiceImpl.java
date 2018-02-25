package com.lx.barragereptile.service.impl;

import com.lx.barragereptile.pojo.DouyuBarrage;
import com.lx.barragereptile.repository.DouyuBarrageRepository;
import com.lx.barragereptile.service.DouyuBarrageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DouyuBarrageServiceImpl implements DouyuBarrageService {
    @Autowired
    DouyuBarrageRepository douyuBarrageRepository;

    @Override
    public void save(DouyuBarrage douyuBarrage) {
        try {
            douyuBarrageRepository.save(douyuBarrage);
        } catch (Exception e) {
            //todo emoji表情保存异常
        }
    }
}
