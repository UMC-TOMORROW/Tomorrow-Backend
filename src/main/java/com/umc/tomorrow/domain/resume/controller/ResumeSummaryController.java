/**
 * 이력서 요약 조회 API 컨트롤러
 * - /api/v1/resumes/summary GET
 * 작성자: 정여진
 * 생성일: 2025-07-20
 */
package com.umc.tomorrow.domain.resume.controller;

import com.umc.tomorrow.domain.resume.dto.response.ResumeSummaryResponseDTO;
import com.umc.tomorrow.domain.resume.service.ResumeSummaryService;
import com.umc.tomorrow.global.common.base.BaseResponse;
import com.umc.tomorrow.domain.auth.security.CustomOAuth2User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "resume-summary-controller", description = "이력서 요약 API")
@RestController
@RequestMapping("/api/v1/resumes")
public class ResumeSummaryController {
    private final ResumeSummaryService resumeSummaryService;

    public ResumeSummaryController(ResumeSummaryService resumeSummaryService) {
        this.resumeSummaryService = resumeSummaryService;
    }

    /**
     * 이력서 요약 조회 (GET)
     * @param user 인증된 사용자
     * @return 이력서 요약 정보
     */
    @Operation(summary = "이력서 요약 조회", description = "사용자의 이력서 요약 정보를 조회합니다.")
    @GetMapping("/summary")
    public ResponseEntity<BaseResponse<ResumeSummaryResponseDTO>> getResumeSummary(
            @AuthenticationPrincipal CustomOAuth2User user) {
        Long userId = user.getUserResponseDTO().getId();
        ResumeSummaryResponseDTO result = resumeSummaryService.getResumeSummary(userId);
        return ResponseEntity.ok(BaseResponse.onSuccess(result));
    }
} 