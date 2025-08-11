/**
 * 지원서 Repository
 * - 지원서 데이터 접근 레이어
 *
 * 작성자: 정여진
 * 생성일: 2025-07-16
 */
package com.umc.tomorrow.domain.application.repository;

import com.umc.tomorrow.domain.application.entity.Application;
import com.umc.tomorrow.domain.application.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    
    /**
     * 특정 공고의 지원서 목록 조회
     */
    @Query("SELECT a FROM Application a WHERE a.job.id = :jobId")
    List<Application> findByJobId(@Param("jobId") Long jobId);
    
    /**
     * 특정 사용자의 지원서 목록 조회
     */
    @Query("SELECT a FROM Application a WHERE a.user.id = :userId")
    List<Application> findByUserId(@Param("userId") Long userId);
    
    /**
     * 특정 공고에 특정 사용자가 지원했는지 확인
     */
    @Query("SELECT a FROM Application a WHERE a.job.id = :jobId AND a.user.id = :userId")
    Optional<Application> findByJobIdAndUserId(@Param("jobId") Long jobId, @Param("userId") Long userId);
    
    /**
     * 특정 공고에 특정 사용자가 지원했는지 확인 (Resume 포함)
     */
    @Query("SELECT a FROM Application a " +
           "LEFT JOIN FETCH a.resume " +
           "WHERE a.job.id = :jobId AND a.user.id = :userId")
    Optional<Application> findByJobIdAndUserIdWithResume(@Param("jobId") Long jobId, @Param("userId") Long userId);
    
    /**
     * 특정 공고의 특정 상태 지원서 목록 조회
     */
    @Query("SELECT a FROM Application a WHERE a.job.id = :jobId AND a.status = :status")
    List<Application> findByJobIdAndStatus(@Param("jobId") Long jobId, @Param("status") Boolean status);

    /**
     * 사용자의 ID에 따라 공고 조회 (전체)
     */
    List<Application> findAllByUserId(Long userId);

    /**
     * 사용자의 ID, 상태(합격/불합)에 따라 공고 조회
     */
    List<Application> findAllByUserIdAndStatus(Long userId, ApplicationStatus status);

    /**
     * 직업 id에 따라 모든 직업 조회
     * */
    List<Application> findAllByJobId(Long jobId);
}