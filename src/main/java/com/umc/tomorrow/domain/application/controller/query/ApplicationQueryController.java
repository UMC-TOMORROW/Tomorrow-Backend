/**
 * 지원서 조회 컨트롤러
 * - 로그인한 사용자의 지원서 목록 조회 (전체 / 합격만)
 *
 * 작성자: 정여진
 * 생성일: 2025-07-24
 */
package com.umc.tomorrow.domain.application.controller.query;

import com.umc.tomorrow.domain.application.dto.response.ApplicationStatusListResponseDTO;
import com.umc.tomorrow.domain.application.service.query.ApplicationQueryService;
import com.umc.tomorrow.global.common.base.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Application", description = "지원서 관련 API")
@RequestMapping("/api/v1/applications")
public class ApplicationQueryController {

    private final ApplicationQueryService applicationQueryService;

    @Operation(
            summary = "지원 현황 목록 조회",
            description = "`type=all`이면 전체, `type=pass`이면 합격한 지원만 필터링합니다."
    )
    @GetMapping
    public ResponseEntity<BaseResponse<List<ApplicationStatusListResponseDTO>>> getApplications(
            @RequestParam(defaultValue = "all") String type,
            @AuthenticationPrincipal(expression = "user.id") Long userId
    ) {
        List<ApplicationStatusListResponseDTO> result = applicationQueryService.getApplicationsByType(userId, type);
        return ResponseEntity.ok(BaseResponse.onSuccess(result));
    }
}