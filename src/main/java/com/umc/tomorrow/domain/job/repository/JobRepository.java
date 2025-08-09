/**
 * 공고 도메인 repository
 * 작성자: 정여진
 * 생성일: 2025-07-25
 */
package com.umc.tomorrow.domain.job.repository;

import com.umc.tomorrow.domain.job.entity.Job;
import com.umc.tomorrow.domain.job.enums.PostStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByUserIdAndStatus(Long userId, PostStatus status);
}