package com.umc.tomorrow.domain.searchAndFilter.repository;

import com.umc.tomorrow.domain.job.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobJpaRepository extends JpaRepository<Job, Long> {
    List<Job> findByIsActiveTrue();
}
