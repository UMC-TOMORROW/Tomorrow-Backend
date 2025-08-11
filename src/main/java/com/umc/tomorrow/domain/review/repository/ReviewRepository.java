/**
 * ReviewRepository
 *
 * 작성자: 정여진
 * 생성일: 2025-07-17
 */
package com.umc.tomorrow.domain.review.repository;

import com.umc.tomorrow.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT r FROM Review r WHERE r.job.id = :jobId")
    List<Review> findByJobId(@Param("jobId") Long postId);
    @Query("SELECT COUNT(r) FROM Review r WHERE r.job.id = :jobId")
    long countByJobId(@Param("jobId") Long jobId);
}