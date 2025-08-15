/**
 * 커리어톡 API 컨트롤러
 * - /api/v1/careertalks
 * 작성자: 이승주
 * 생성일: 2025-07-10
 * 수정일: 2025-07-20
 */
package com.umc.tomorrow.domain.careertalk.controller;

import com.umc.tomorrow.domain.auth.security.CustomOAuth2User;
import com.umc.tomorrow.domain.careertalk.dto.request.CreateCareertalkRequestDTO;
import com.umc.tomorrow.domain.careertalk.dto.request.UpdateCareertalkRequestDTO;
import com.umc.tomorrow.domain.careertalk.dto.response.CareertalkResponseDto;
import com.umc.tomorrow.domain.careertalk.dto.response.GetCareertalkListResponseDto;
import com.umc.tomorrow.domain.careertalk.dto.response.GetCareertalkResponseDto;
import com.umc.tomorrow.domain.careertalk.service.command.CareertalkCommandService;
import com.umc.tomorrow.domain.careertalk.service.query.CareertalkQueryService;
import com.umc.tomorrow.global.common.base.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Careertalk", description = "커리어톡 관련 API")
@Validated
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
     * @return 커리어톡 응답 DTO
     */
    @PostMapping
    @Operation(summary = "커리어톡 게시글 작성", description = "로그인한 사용자가 커리어톡 게시글을 작성합니다.")
    @ApiResponse(responseCode = "201", description = "게시글 생성 성공")
    public ResponseEntity<BaseResponse<CareertalkResponseDto>> createCareertalk(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            @Valid @RequestBody CreateCareertalkRequestDTO requestDto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.onSuccessCreate(careertalkCommandService.createCareertalk(customOAuth2User.getUserResponseDTO().getId(), requestDto)));
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
    public ResponseEntity<BaseResponse<GetCareertalkListResponseDto>> getCareertalks(
            @Positive @RequestParam(required = false) Long cursor,
            @Positive @RequestParam(defaultValue = "8") int size
    ) {
        return ResponseEntity.ok(BaseResponse.onSuccess(careertalkQueryService.getCareertalks(cursor, size)));
    }

    /**
     * 커리어톡 게시글 상세 조회(GET)
     * @param careertalkId 조회하고자 하는 커리어톡 게시글 id
     * @param
     * @return 해당하는 커리어톡 게시글 DTO
     */
    @GetMapping("/{careertalkId}")
    @Operation(summary = "커리어톡 게시글 상세 조회", description = "커리어톡 게시글을 상세 조회합니다.")
    @ApiResponse(responseCode = "200", description = "게시글 상세 조회 성공")
    public ResponseEntity<BaseResponse<GetCareertalkResponseDto>> getCareertalk(
            @PathVariable Long careertalkId,
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User

    ) {
        Long userId = customOAuth2User.getUserResponseDTO().getId();
        return ResponseEntity.ok(BaseResponse.onSuccess(careertalkQueryService.getCareertalk(careertalkId, userId)));
    }

    /**
     * 커리어톡 게시글 수정 API
     * @param customOAuth2User 인증된 사용자
     * @param requestDto 게시글 수정 요청 DTO
     * @return 커리어톡 응답 DTO
     */
    @PutMapping("/{careertalkId}")
    @Operation(summary = "커리어톡 게시글 수정", description = "로그인한 사용자가 커리어톡 게시글을 수정합니다.")
    @ApiResponse(responseCode = "200", description = "게시글 수정 성공")
    public ResponseEntity<BaseResponse<CareertalkResponseDto>> updateCareertalk(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            @PathVariable Long careertalkId,
            @Valid @RequestBody UpdateCareertalkRequestDTO requestDto
    ){
        return ResponseEntity.ok(BaseResponse.onSuccess(careertalkCommandService.updateCareertalk(customOAuth2User.getUserResponseDTO().getId(),careertalkId,requestDto)));
    }

    /**
     * 커리어톡 게시글 삭제 API
     * @param customOAuth2User 인증된 사용자
     * @param careertalkId 게시글 Id
     * @return 커리어톡 응답 DTO
     */
    @DeleteMapping("/{careertalkId}")
    @Operation(summary = "커리어톡 게시글 삭제", description = "로그인한 사용자가 커리어톡 게시글을 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "게시글 삭제 성공")
    public ResponseEntity<BaseResponse<CareertalkResponseDto>> deleteCareertalk(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            @PathVariable Long careertalkId
    ){
        return ResponseEntity.ok(BaseResponse.onSuccessDelete(careertalkCommandService.deleteCareertalk(customOAuth2User.getUserResponseDTO().getId(),careertalkId)));
    }

    /**
     * 커리어톡 게시글 제목으로 검색 API
     * @param title 커리어톡 게시글
     * @param cursor 이전 요청에서의 마지막 게시글 id
     * @param size 요청한 게시글 개수
     * @return 커리어톡 게시글 목록 DTO
     */
    @GetMapping("/search/title")
    @Operation(summary = "커리어톡 게시글 제목으로 검색", description = "로그인한 사용자가 커리어톡 게시글을 제목으로 검색하여 조회합니다.")
    @ApiResponse(responseCode = "200", description = "게시글 조회 성공")
    public ResponseEntity<BaseResponse<GetCareertalkListResponseDto>> getCareertalksByTitle(
            @RequestParam String title,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "8") int size
    ){
        return ResponseEntity.ok(BaseResponse.onSuccess(careertalkQueryService.getCareertalksByTitle(title,cursor,size)));
    }

    /**
     * 커리어톡 게시글 카테고리로 검색 API
     * @param category 커리어톡 게시글 카테고리
     * @param cursor 이전 요청에서의 마지막 게시글 id
     * @param size 요청한 게시글 개수
     * @return 커리어톡 게시글 목록 DTO
     */
    @GetMapping("/search/category")
    @Operation(summary = "커리어톡 게시글 카테고리로 검색", description = "로그인한 사용자가 커리어톡 게시글을 카테고리로 검색하여 조회합니다.")
    @ApiResponse(responseCode = "200", description = "게시글 조회 성공")
    public ResponseEntity<BaseResponse<GetCareertalkListResponseDto>> getCareertalksByCategory(
            @RequestParam String category,
            @Positive @RequestParam(required = false) Long cursor,
            @Positive @RequestParam(defaultValue = "8") int size
    ){
        return ResponseEntity.ok(BaseResponse.onSuccess(careertalkQueryService.getCareertalksByCategory(category,cursor,size)));
    }
}
