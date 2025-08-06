/**
 * ResumeRepository
 * - 이력서 엔티티 데이터 접근 레이어
 * 작성자: 정여진
 * 생성일: 2025-07-20
 */
package com.umc.tomorrow.domain.resume.repository;

import com.umc.tomorrow.domain.resume.entity.Resume;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResumeRepository extends JpaRepository<Resume, Long> {
    Optional<Resume> findFirstByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<Resume> findByIdAndUserId(Long resumeId, Long userId);
}

