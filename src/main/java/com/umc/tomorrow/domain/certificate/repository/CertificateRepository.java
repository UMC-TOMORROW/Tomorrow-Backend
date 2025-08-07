/**
 * CertificateRepository
 * - 자격증 엔티티 데이터 접근 레이어
 * 작성자: 정여진
 * 생성일: 2025-07-20
 */
package com.umc.tomorrow.domain.certificate.repository;

import com.umc.tomorrow.domain.certificate.entity.Certificate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    List<Certificate> findByResumeId(Long resumeId);
} 