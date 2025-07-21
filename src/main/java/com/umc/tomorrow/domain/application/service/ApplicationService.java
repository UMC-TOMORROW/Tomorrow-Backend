/**
 * 지원서 서비스
 * - 지원서 관련 비즈니스 로직 처리
 *
 * 작성자: 정여진
 * 생성일: 2025-07-16
 */
package com.umc.tomorrow.domain.application.service;

import com.umc.tomorrow.domain.application.dto.request.UpdateApplicationStatusRequestDTO;
import com.umc.tomorrow.domain.application.dto.response.UpdateApplicationStatusResponseDTO;
import com.umc.tomorrow.domain.application.entity.Application;
import com.umc.tomorrow.domain.application.repository.ApplicationRepository;
import com.umc.tomorrow.domain.job.entity.Job;
import com.umc.tomorrow.domain.job.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApplicationService {
    
    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    
    /**
     * 지원서 상태 업데이트 (합격/불합격 처리)
     */
    @Transactional
    public UpdateApplicationStatusResponseDTO updateApplicationStatus(
            Long postId, 
            Long applicationId, 
            UpdateApplicationStatusRequestDTO requestDTO
    ) {
        // 지원서 조회
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("지원서를 찾을 수 없습니다."));
        
        // 공고 조회 및 검증
        Job job = jobRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("공고를 찾을 수 없습니다."));
        
        // 지원서가 해당 공고에 대한 것인지 검증
        if (!application.getJob().getId().equals(postId)) {
            throw new IllegalArgumentException("해당 공고의 지원서가 아닙니다.");
        }
        
        // 상태 업데이트
        Boolean newStatus;
        if ("합격".equals(requestDTO.getStatus())) {
            newStatus = true;
        } else if ("불합격".equals(requestDTO.getStatus())) {
            newStatus = false;
        } else {
            throw new IllegalArgumentException("유효하지 않은 상태입니다.");
        }
        
        application.updateStatus(newStatus);
        applicationRepository.save(application);
        
        return UpdateApplicationStatusResponseDTO.builder()
                .applicationId(applicationId)
                .status(requestDTO.getStatus())
                .build();
    }
} 