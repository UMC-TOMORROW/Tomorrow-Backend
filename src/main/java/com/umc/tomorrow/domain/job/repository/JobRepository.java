package com.umc.tomorrow.domain.job.repository;

import com.umc.tomorrow.domain.job.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

}