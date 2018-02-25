package com.lx.barragereptile.service.impl;

import com.lx.barragereptile.pojo.Job;
import com.lx.barragereptile.repository.JobRepository;
import com.lx.barragereptile.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobServiceImpl implements JobService {
    @Autowired
    JobRepository jobRepository;

    @Override
    public Boolean isHave(String roomid) {
        Job one = jobRepository.findOne(roomid);
        if (one == null) {
            return false;
        }
        return true;
    }

    @Override
    public void save(Job job) {
        jobRepository.save(job);

    }

    @Override
    public Long getThreadIdByRoomId(String roomid) {
        Job one = jobRepository.getOne(roomid);
        if (one != null) {
            return one.getThreadid();
        }
        return null;
    }

    @Override
    public List<Job> getAll() {
        return jobRepository.findAll();
    }

    @Override
    public void update(Job job) {
        jobRepository.save(job);
    }
}
