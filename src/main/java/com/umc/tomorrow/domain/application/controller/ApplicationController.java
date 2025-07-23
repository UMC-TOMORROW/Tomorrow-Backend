/**
 * 지원서 컨트롤러
 * - 지원서 관련 API 엔드포인트
 *
 * 작성자: 정여진
 * 생성일: 2025-07-16
 */
package com.umc.tomorrow.domain.application.controller;

import com.umc.tomorrow.domain.application.dto.request.UpdateApplicationStatusRequestDTO;
import com.umc.tomorrow.domain.application.dto.response.UpdateApplicationStatusResponseDTO;
import com.umc.tomorrow.domain.application.service.command.ApplicationService;
import com.umc.tomorrow.global.common.base.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
} 