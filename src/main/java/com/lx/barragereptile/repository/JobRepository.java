package com.lx.barragereptile.repository;

import com.lx.barragereptile.pojo.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, String> {

}
