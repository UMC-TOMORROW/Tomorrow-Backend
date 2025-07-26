/**
 * 지원서 컨트롤러
 * - 지원서 관련 API 엔드포인트
 *
 * 작성자: 정여진
 * 생성일: 2025-07-16
 */
package com.umc.tomorrow.domain.application.controller;

import com.umc.tomorrow.domain.application.dto.request.UpdateApplicationStatusRequestDTO;
import com.umc.tomorrow.domain.application.dto.response.ApplicantListResponseDTO;
import com.umc.tomorrow.domain.application.dto.response.ApplicationStatusListResponseDTO;
import com.umc.tomorrow.domain.application.dto.response.ApplicationDetailsResponseDTO;
import com.umc.tomorrow.domain.application.dto.response.UpdateApplicationStatusResponseDTO;
import com.umc.tomorrow.domain.application.service.command.ApplicationService;
import com.umc.tomorrow.global.common.base.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@Tag(name = "Application", description = "지원서 관련 API")
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class ApplicationController {
    
    private final ApplicationService applicationService;
    
    @Operation(
        summary = "지원서 상태 업데이트", 
        description = "지원자의 합격/불합격 상태를 업데이트합니다."
    )
    @PatchMapping("/{postId}/applications/{applicationId}/status")
    public ResponseEntity<BaseResponse<UpdateApplicationStatusResponseDTO>> updateApplicationStatus(
            @Parameter(description = "공고 ID", example = "301")
            @PathVariable @Min(1) Long postId,
            
            @Parameter(description = "지원서 ID", example = "101")
            @PathVariable @Min(1) Long applicationId,
            
            @Valid @RequestBody UpdateApplicationStatusRequestDTO requestDTO
    ) {
        UpdateApplicationStatusResponseDTO result = applicationService.updateApplicationStatus(
                postId, applicationId, requestDTO
        );
        
        return ResponseEntity.ok(BaseResponse.onSuccess(result));
    }

    @Operation(
            summary = "개별 지원자 이력서 조회",
            description = "특정 공고에 지원한 개별 지원자의 이력서 정보를 조회합니다."
    )
    @GetMapping("/{postId}/applicants/{applicantId}/resume")
    public ResponseEntity<BaseResponse<ApplicationDetailsResponseDTO>> getApplicantResume(
            @Parameter(description = "공고 ID", example = "301")
            @PathVariable @Min(1) Long postId,

            @Parameter(description = "지원자 ID", example = "101")
            @PathVariable @Min(1) Long applicantId
    ) {
        // 여기서는 Path Variable만 사용하여 조회하도록 구현
        ApplicationDetailsResponseDTO result = applicationService.getApplicantResume(
                postId,
                applicantId
        );
        return ResponseEntity.ok(BaseResponse.onSuccess(result));
    }

    @Operation(
            summary = "지원자 목록 조회 (공고 기준)",
            description = "`status`가 없으면 전체, `status=open` 또는 `status=closed`로 필터링합니다."
    )
    @GetMapping("/{postId}/applicants")
    public ResponseEntity<BaseResponse<List<ApplicantListResponseDTO>>> getApplicantsByPost(
            @Parameter(description = "공고 ID", example = "301")
            @PathVariable @Min(1) Long postId,

            @Parameter(description = "지원 상태 필터 (생략 시 전체 조회)", example = "closed")
            @RequestParam(required = false) String status
    ) {
        List<ApplicantListResponseDTO> result = applicationService.getApplicantsByPostAndStatus(postId, status);
        return ResponseEntity.ok(BaseResponse.onSuccess(result));
    }
} 