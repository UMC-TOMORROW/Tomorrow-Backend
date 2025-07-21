/**
 * 지원자 서비스
 * - 지원자 관련 비즈니스 로직 처리
 *
 * 작성자: 정여진
 * 생성일: 2025-07-16
 */
package com.umc.tomorrow.domain.applicant.service;

import com.umc.tomorrow.domain.applicant.dto.request.UpdateApplicantStatusRequestDTO;
import com.umc.tomorrow.domain.applicant.dto.response.UpdateApplicantStatusResponseDTO;
import com.umc.tomorrow.domain.applicant.entity.Applicant;
import com.umc.tomorrow.domain.applicant.repository.ApplicantRepository;
import com.umc.tomorrow.domain.job.entity.Job;
import com.umc.tomorrow.domain.job.repository.JobRepository;
import com.umc.tomorrow.domain.member.entity.User;
import com.umc.tomorrow.domain.member.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApplicantService {
    
    private final ApplicantRepository applicantRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    
    /**
     * 지원자 상태 업데이트 (합격/불합격 처리)
     */
    @Transactional
    public UpdateApplicantStatusResponseDTO updateApplicantStatus(
            Long postId, 
            Long applicantId, 
            UpdateApplicantStatusRequestDTO requestDTO
    ) {
        // 지원자 조회
        Applicant applicant = applicantRepository.findById(applicantId)
                .orElseThrow(() -> new IllegalArgumentException("지원자를 찾을 수 없습니다."));
        
        // 공고 조회 및 검증
        Job job = jobRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("공고를 찾을 수 없습니다."));
        
        // 지원자가 해당 공고에 지원한 것인지 검증
        if (!applicant.getJob().getId().equals(postId)) {
            throw new IllegalArgumentException("해당 공고의 지원자가 아닙니다.");
        }
        
        // 상태 업데이트
        Applicant.ApplicationStatus newStatus;
        if ("합격".equals(requestDTO.getStatus())) {
            newStatus = Applicant.ApplicationStatus.ACCEPTED;
        } else if ("불합격".equals(requestDTO.getStatus())) {
            newStatus = Applicant.ApplicationStatus.REJECTED;
        } else {
            throw new IllegalArgumentException("유효하지 않은 상태입니다.");
        }
        
        applicant.updateStatus(newStatus);
        applicantRepository.save(applicant);
        
        return UpdateApplicantStatusResponseDTO.builder()
                .applicantId(applicantId)
                .status(requestDTO.getStatus())
                .build();
    }
} 