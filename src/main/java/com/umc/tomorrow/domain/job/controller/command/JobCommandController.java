package com.umc.tomorrow.domain.job.controller.command;

import com.umc.tomorrow.domain.job.dto.request.BusinessRequestDTO;
import com.umc.tomorrow.domain.job.dto.request.JobRequestDTO;
import com.umc.tomorrow.domain.job.dto.request.PersonalRequestDTO;
import com.umc.tomorrow.domain.job.dto.response.JobStepResponseDTO;
import com.umc.tomorrow.domain.job.service.command.JobCommandService;
import com.umc.tomorrow.domain.member.entity.User;
import com.umc.tomorrow.global.common.base.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Job", description = "일자리 관련 API")
@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
public class JobCommandController {

    private final JobCommandService jobCommandService;

    @Operation(summary = "일자리 등록 폼 작성", description = "검증된 사용자가 일자리 폼을 작성합니다.")
    @PostMapping
    public ResponseEntity<BaseResponse<JobStepResponseDTO>> saveJobStepOne(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody JobRequestDTO requestDTO,
            HttpSession session
    ) {
        JobStepResponseDTO result = jobCommandService.saveInitialJobStep(user.getId(), requestDTO, session);
        return ResponseEntity.ok(BaseResponse.onSuccess(result));
    }


    // 사업자 등록 API
    @Operation(summary = "사업자 인증", description = "일자리 등록 페이지에서 회사를 선택한 사람은 사업자 인증 페이지로 이동한다")
    @PostMapping("/business-verifications")
    public ResponseEntity<BaseResponse<Object>> saveBusinessVerification(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody BusinessRequestDTO requestDTO
    ) {
        jobCommandService.saveBusinessVerification(user.getId(), requestDTO);

        return ResponseEntity.ok(BaseResponse.onSuccess("verifications_suceess"));
    }

    // 개인 등록 API
    @Operation(summary = "개인 등록 사유", description = "일자리 등록 페이지에서 개인를 선택한 사람은 개인 등록 사유 페이지로 이동한다")
    @PostMapping("/personal-registrations")
    public ResponseEntity<BaseResponse<Object>> savePersonalRegistration(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody PersonalRequestDTO requestDTO
    ) {
        jobCommandService.savePersonalRegistration(user.getId(), requestDTO);

        return ResponseEntity.ok(BaseResponse.onSuccess("personal-registrations_suceess"));
    }
}
