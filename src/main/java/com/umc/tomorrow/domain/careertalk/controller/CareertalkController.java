/**
 * 커리어톡 API 컨트롤러
 * - /api/v1/careertalks
 * 작성자: 이승주
 * 생성일: 2025-07-10
 * 수정일: 2025-07-20
 */
package com.umc.tomorrow.domain.careertalk.controller;

import com.umc.tomorrow.domain.auth.security.CustomOAuth2User;
import com.umc.tomorrow.domain.careertalk.dto.request.CreateCareertalkRequestDto;
import com.umc.tomorrow.domain.careertalk.dto.response.CreateCareertalkResponseDto;
import com.umc.tomorrow.domain.careertalk.dto.response.GetCareertalkListResponseDto;
import com.umc.tomorrow.domain.careertalk.dto.response.GetCareertalkResponseDto;
import com.umc.tomorrow.domain.careertalk.service.command.CareertalkCommandService;
import com.umc.tomorrow.domain.careertalk.service.query.CareertalkQueryService;
import com.umc.tomorrow.global.common.base.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Careertalk", description = "커리어톡 관련 API")
@RestController
@RequestMapping("/api/v1/careertalks")
@RequiredArgsConstructor
public class CareertalkController {

    private final CareertalkCommandService careertalkCommandService;
    private final CareertalkQueryService careertalkQueryService;

    /**
     * 커리어톡 게시글 저장(POST)
     * @param customOAuth2User 인증된 사용자
     * @param requestDto 게시글 생성 요청 DTO
     * @return 커리어톡 생성 응답 DTO
     */
    @PostMapping
    @Operation(summary = "커리어톡 게시글 작성", description = "로그인한 사용자가 커리어톡 게시글을 작성합니다.")
    @ApiResponse(responseCode = "201", description = "게시글 생성 성공")
    public BaseResponse<CreateCareertalkResponseDto> createCareertalk(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            @Valid @RequestBody CreateCareertalkRequestDto requestDto
    ) {
        String username = customOAuth2User.getName();
        return BaseResponse.onSuccessCreate(careertalkCommandService.createCareertalk(username, requestDto));
    }

    /**
     * 커리어톡 게시글 목록 조회(GET)
     * @param cursor 이전 요청에서의 마지막 게시글 id
     * @param size 요청한 게시글 개수
     * @return 커리어톡 게시글 목록 DTO
     */
    @GetMapping
    @Operation(summary = "커리어톡 게시글 목록 조회 (무한 스크롤)", description = "커리어톡 게시글 목록을 무한 스크롤 방식으로 조회합니다.")
    @ApiResponse(responseCode = "200", description = "게시글 목록 조회 성공")
    public BaseResponse<GetCareertalkListResponseDto> getCareertalks(
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "8") int size
    ) {
        return BaseResponse.onSuccess(careertalkQueryService.getCareertalks(cursor, size));
    }

    /**
     * 커리어톡 게시글 상세 조회(GET)
     * @param careertalkId 조회하고자 하는 커리어톡 게시글 id
     * @return 해당하는 커리어톡 게시글 DTO
     */
    @GetMapping("/{careertalkId}")
    @Operation(summary = "커리어톡 게시글 상세 조회", description = "커리어톡 게시글을 상세 조회합니다.")
    @ApiResponse(responseCode = "200", description = "게시글 상세 조회 성공")
    public BaseResponse<GetCareertalkResponseDto> getCareertalk(
            @PathVariable Long careertalkId
    ) {
        return BaseResponse.onSuccess(careertalkQueryService.getCareertalk(careertalkId));
    }
}
