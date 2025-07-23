/**
 * 지원서 서비스
 * - 지원서 관련 비즈니스 로직 처리
 *
 * 작성자: 정여진
 * 생성일: 2025-07-16
 */
package com.umc.tomorrow.domain.application.service.command;

import com.umc.tomorrow.domain.application.converter.ApplicationConverter;
import com.umc.tomorrow.domain.application.dto.request.UpdateApplicationStatusRequestDTO;
import com.umc.tomorrow.domain.application.dto.response.UpdateApplicationStatusResponseDTO;
import com.umc.tomorrow.domain.application.entity.Application;
import com.umc.tomorrow.domain.application.enums.ApplicationStatus;
import com.umc.tomorrow.domain.application.exception.ApplicationErrorStatus;
import com.umc.tomorrow.domain.application.repository.ApplicationRepository;
import com.umc.tomorrow.domain.job.entity.Job;
import com.umc.tomorrow.domain.job.exception.JobErrorStatus;
import com.umc.tomorrow.domain.job.repository.JobRepository;
import com.umc.tomorrow.global.common.exception.RestApiException;
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
                .orElseThrow(() -> new RestApiException(ApplicationErrorStatus.APPLICATION_NOT_FOUND));
        
        // 공고 조회 및 검증
        Job job = jobRepository.findById(postId)
                .orElseThrow(() -> new RestApiException(JobErrorStatus.JOB_NOT_FOUND));
        
        // 지원서가 해당 공고에 대한 것인지 검증
        if (!application.getJob().getId().equals(postId)) {
            throw new RestApiException(ApplicationErrorStatus.APPLICATION_JOB_MISMATCH);
        }

        ApplicationStatus status =  ApplicationConverter.toEnum(requestDTO);

        application.updateStatus(status);
        applicationRepository.save(application);
        
        return UpdateApplicationStatusResponseDTO.builder()
                .applicationId(applicationId)
                .status(requestDTO.getStatus())
                .build();
    }
} 