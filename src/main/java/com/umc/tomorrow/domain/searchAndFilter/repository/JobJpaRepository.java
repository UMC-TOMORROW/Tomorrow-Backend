package com.umc.tomorrow.domain.searchAndFilter.repository;

import com.umc.tomorrow.domain.job.entity.Job;
import com.umc.tomorrow.domain.job.enums.PostStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobJpaRepository extends JpaRepository<Job, Long> {
    List<Job> findByStatus(PostStatus status);
}
