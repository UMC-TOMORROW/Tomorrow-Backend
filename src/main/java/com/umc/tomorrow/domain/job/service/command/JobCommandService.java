package com.umc.tomorrow.domain.job.service.command;

import com.umc.tomorrow.domain.job.dto.request.BusinessRequestDTO;
import com.umc.tomorrow.domain.job.dto.request.JobRequestDTO;
import com.umc.tomorrow.domain.job.dto.request.PersonalRequestDTO;
import com.umc.tomorrow.domain.job.dto.response.GetRecommendationListResponse;
import com.umc.tomorrow.domain.job.dto.response.JobCreateResponseDTO;
import com.umc.tomorrow.domain.job.dto.response.JobStepResponseDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.transaction.annotation.Transactional;

public interface JobCommandService {

    //기본 일자리 정보 폼 저장 (세션에 보관)
    JobStepResponseDTO saveInitialJobStep(Long userId, JobRequestDTO requestDTO, HttpSession session);

    //개인 등록 시 Personal 정보 저장 + Job 생성
    JobCreateResponseDTO savePersonalRegistration(Long userId, PersonalRequestDTO requestDTO, HttpSession session);

    //사업자 등록이 이미 되어있는 경우 바로 Job 생성
    JobCreateResponseDTO createJobWithExistingBusiness(Long userId, HttpSession session);
    
    //사업자 등록이 안 되어 있는 경우 Business 등록 후 Job 생성
    JobCreateResponseDTO registerBusinessAndCreateJob(Long userId, BusinessRequestDTO requestDTO, HttpSession session);

    //사업자 등록
    void saveBusinessVerification(Long userId, BusinessRequestDTO requestDTO);

    // PATCH 공고 모집완료/모집전 처리하기
    @Transactional
    void updateJobStatus(Long userId, Long jobId, String status);
}
