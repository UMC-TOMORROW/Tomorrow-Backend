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
    List<Review> findByJobId(@Param("jobId") Long jobId);
    @Query("SELECT COUNT(r) FROM Review r WHERE r.job.id = :jobId")
    long countByJobId(@Param("jobId") Long jobId);


    // 여러 jobId에 대한 리뷰 수를 한 번에 조회 (GROUP BY)
    @Query(
            "select r.job.id as jobId, count(r) as cnt " +
                    "from Review r " +
                    "where r.job.id in :jobIds " +
                    "group by r.job.id"
    )
    List<JobReviewCount> countByJobIdInGroupByJob(@Param("jobIds") List<Long> jobIds);

    // (jobId, cnt) 형태로 반환하는 인터페이스 프로젝션
    interface JobReviewCount {
        Long getJobId();
        Long getCnt();
    }
}