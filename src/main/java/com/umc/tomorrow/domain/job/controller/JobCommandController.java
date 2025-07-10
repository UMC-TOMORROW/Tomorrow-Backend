package com.umc.tomorrow.domain.job.controller;

import com.umc.tomorrow.domain.job.dto.request.CreateJobRequestDTO;
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

}
