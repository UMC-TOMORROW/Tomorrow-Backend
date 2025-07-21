/**
 * 지원자 컨트롤러
 * - 지원자 관련 API 엔드포인트
 *
 * 작성자: 정여진
 * 생성일: 2025-07-16
 */
package com.umc.tomorrow.domain.applicant.controller;

import com.umc.tomorrow.domain.applicant.dto.request.UpdateApplicantStatusRequestDTO;
import com.umc.tomorrow.domain.applicant.dto.response.UpdateApplicantStatusResponseDTO;
import com.umc.tomorrow.domain.applicant.service.ApplicantService;
import com.umc.tomorrow.global.common.base.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "지원자", description = "지원자 관련 API")
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class ApplicantController {
    
    private final ApplicantService applicantService;
    
    @Operation(
        summary = "지원자 상태 업데이트", 
        description = "지원자의 합격/불합격 상태를 업데이트합니다."
    )
    @PatchMapping("/{postId}/applicants/{applicantId}/status")
    public ResponseEntity<BaseResponse<UpdateApplicantStatusResponseDTO>> updateApplicantStatus(
            @Parameter(description = "공고 ID", example = "301")
            @PathVariable Long postId,
            
            @Parameter(description = "지원자 ID", example = "101")
            @PathVariable Long applicantId,
            
            @Valid @RequestBody UpdateApplicantStatusRequestDTO requestDTO
    ) {
        UpdateApplicantStatusResponseDTO result = applicantService.updateApplicantStatus(
                postId, applicantId, requestDTO
        );
        
        return ResponseEntity.ok(BaseResponse.onSuccess(result));
    }
} 