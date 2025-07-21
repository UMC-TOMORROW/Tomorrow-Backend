/**
 * 지원자 Repository
 * - 지원자 데이터 접근 레이어
 *
 * 작성자: 정여진
 * 생성일: 2025-07-16
 */
package com.umc.tomorrow.domain.applicant.repository;

import com.umc.tomorrow.domain.applicant.entity.Applicant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ApplicantRepository extends JpaRepository<Applicant, Long> {
    
    /**
     * 특정 공고의 지원자 목록 조회
     */
    @Query("SELECT a FROM Applicant a WHERE a.job.id = :jobId")
    List<Applicant> findByJobId(@Param("jobId") Long jobId);
    
    /**
     * 특정 사용자의 지원 목록 조회
     */
    @Query("SELECT a FROM Applicant a WHERE a.user.id = :userId")
    List<Applicant> findByUserId(@Param("userId") Long userId);
    
    /**
     * 특정 공고에 특정 사용자가 지원했는지 확인
     */
    @Query("SELECT a FROM Applicant a WHERE a.job.id = :jobId AND a.user.id = :userId")
    Optional<Applicant> findByJobIdAndUserId(@Param("jobId") Long jobId, @Param("userId") Long userId);
    
    /**
     * 특정 공고의 특정 상태 지원자 목록 조회
     */
    @Query("SELECT a FROM Applicant a WHERE a.job.id = :jobId AND a.status = :status")
    List<Applicant> findByJobIdAndStatus(@Param("jobId") Long jobId, @Param("status") Applicant.ApplicationStatus status);
} 