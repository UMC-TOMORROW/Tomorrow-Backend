package com.umc.tomorrow.domain.job.controller;

import com.umc.tomorrow.domain.job.dto.request.BusinessRequestDTO;
import com.umc.tomorrow.domain.job.dto.request.CreateJobRequestDTO;
import com.umc.tomorrow.domain.job.dto.request.PersonalRequestDTO;
import com.umc.tomorrow.domain.job.dto.response.JobStepResponseDTO;
import com.umc.tomorrow.domain.job.service.command.JobCommandService;
import com.umc.tomorrow.global.common.base.BaseResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
public class JobCommandController {

    private final JobCommandService jobCommandService;

    @PostMapping
    public ResponseEntity<BaseResponse<JobStepResponseDTO>> saveJobStepOne(
            @Valid @RequestBody CreateJobRequestDTO requestDTO,
            HttpSession session
    ) {
        JobStepResponseDTO result = jobCommandService.saveInitialJobStep(requestDTO, session);
        return ResponseEntity.ok(BaseResponse.onSuccess(result));
    }

    // 사업자 등록 API
    @PostMapping("/business-verifications")
    public ResponseEntity<BaseResponse<Object>> saveBusinessVerification(
            @Valid @RequestBody BusinessRequestDTO requestDTO
    ) {
        jobCommandService.saveBusinessVerification(requestDTO);

        return ResponseEntity.ok(BaseResponse.onSuccess("verifications_suceess"));
    }

    // 개인 등록 API
    @PostMapping("/personal-registrations")
    public ResponseEntity<BaseResponse<Object>> savePersonalRegistration(
            @Valid @RequestBody PersonalRequestDTO requestDTO
    ) {
        jobCommandService.savePersonalRegistration(requestDTO);

        return ResponseEntity.ok(BaseResponse.onSuccess("personal-registrations_suceess"));
    }
}
