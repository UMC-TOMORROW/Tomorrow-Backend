/**
 * 이력서 요약 서비스
 * - 이력서 요약 데이터 조회 비즈니스 로직
 * 작성자: 정여진
 * 생성일: 2025-07-20
 */
package com.umc.tomorrow.domain.resume.service;

import com.umc.tomorrow.domain.resume.dto.response.ResumeSummaryResponseDTO;
import com.umc.tomorrow.domain.resume.entity.Resume;
import com.umc.tomorrow.domain.resume.exception.ResumeException;
import com.umc.tomorrow.domain.resume.exception.code.ResumeErrorStatus;
import com.umc.tomorrow.domain.resume.repository.ResumeRepository;
import com.umc.tomorrow.domain.resume.converter.ResumeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResumeSummaryService {
    private final ResumeRepository resumeRepository;

    @Autowired
    public ResumeSummaryService(ResumeRepository resumeRepository) {
        this.resumeRepository = resumeRepository;
    }

    /**
     * 이력서 요약 정보 조회
     * @param userId 사용자 ID
     * @return 이력서 요약 응답 DTO
     */
    public ResumeSummaryResponseDTO getResumeSummary(Long userId) {
        Resume latestResume = resumeRepository.findFirstByUserIdOrderByCreatedAtDesc(userId)
                .orElseThrow(() -> new ResumeException(ResumeErrorStatus.RESUME_NOT_FOUND));

        return ResumeConverter.toSummaryDTO(latestResume);
    }
} 