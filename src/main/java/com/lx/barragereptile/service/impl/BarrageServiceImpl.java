package com.lx.barragereptile.service.impl;

import com.lx.barragereptile.pojo.PandaBarrage;
import com.lx.barragereptile.repository.PandaBarrageRepository;
import com.lx.barragereptile.service.BarrageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BarrageServiceImpl implements BarrageService {
    @Autowired
    PandaBarrageRepository pandaBarrageRepository;

    @Override
    public int save(PandaBarrage pandaBarrage) {
        pandaBarrageRepository.save(pandaBarrage);
        return 1;
    }
}
