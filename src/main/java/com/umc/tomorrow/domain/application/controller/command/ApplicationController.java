/**
 * 지원서 컨트롤러
 * - 지원서 관련 API 엔드포인트
 *
 * 작성자: 정여진
 * 생성일: 2025-07-16
 */
package com.umc.tomorrow.domain.application.controller.command;

import com.umc.tomorrow.domain.application.dto.request.CreateApplicationRequestDTO;
import com.umc.tomorrow.domain.application.dto.request.UpdateApplicationStatusRequestDTO;
import com.umc.tomorrow.domain.application.dto.response.ApplicantListResponseDTO;
import com.umc.tomorrow.domain.application.dto.response.CreateApplicationResponseDTO;
import com.umc.tomorrow.domain.application.dto.response.ApplicationDetailsResponseDTO;
import com.umc.tomorrow.domain.application.dto.response.UpdateApplicationStatusResponseDTO;
import com.umc.tomorrow.domain.application.service.command.ApplicationCommandService;
import com.umc.tomorrow.domain.application.exception.ApplicationException;
import com.umc.tomorrow.domain.auth.security.CustomOAuth2User;
import com.umc.tomorrow.global.common.base.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@Tag(name = "Application", description = "지원서 관련 API")
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class ApplicationController {
    
    private final ApplicationCommandService applicationCommandService;
    
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
        UpdateApplicationStatusResponseDTO result = applicationCommandService.updateApplicationStatus(
                postId, applicationId, requestDTO
        );
        
        return ResponseEntity.ok(BaseResponse.onSuccess(result));
    }

    /**
     * 일자리 지원하기 API(POST)
     * @param postId 지원하고자 하는 일자리ID
     * @param user 지원하는 유저
     * @param requestDto 일자리 지원 요청 DTO
     * @return 일자리 지원 응답 DTO
     */
    @Operation(summary = "일자리 지원하기", description = "로그인한 사용자가 해당 일자리에 지원합니다.")
    @ApiResponse(responseCode = "201", description = "일자리 지원 성공")
    @PostMapping("/{postId}/applications")
    public ResponseEntity<BaseResponse<CreateApplicationResponseDTO>> createApplication(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomOAuth2User user,
            @Valid @RequestBody CreateApplicationRequestDTO requestDto
    ) {
        CreateApplicationResponseDTO result = applicationCommandService.createApplication(user.getUserDTO().getId(), postId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.onSuccessCreate(result));
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
        ApplicationDetailsResponseDTO result = applicationCommandService.getApplicantResume(
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
        List<ApplicantListResponseDTO> result = applicationCommandService.getApplicantsByPostAndStatus(postId, status);
        return ResponseEntity.ok(BaseResponse.onSuccess(result));
    }
} 