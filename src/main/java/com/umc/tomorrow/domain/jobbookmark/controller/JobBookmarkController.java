/**
 * 찜 컨트롤러
 * 작성자: 정여진
 * 작성일: 2025-08-05
 * GET, POST, DELETE /api/v1/saved-posts
 */
package com.umc.tomorrow.domain.jobbookmark.controller;

import com.umc.tomorrow.domain.auth.security.CustomOAuth2User;
import com.umc.tomorrow.domain.jobbookmark.dto.response.GetJobBookmarkListResponseDTO;
import com.umc.tomorrow.domain.jobbookmark.dto.response.JobBookmarkResponseDTO;
import com.umc.tomorrow.domain.jobbookmark.service.command.JobBookmarkCommandService;
import com.umc.tomorrow.domain.jobbookmark.service.query.JobBookmarkQueryService;
import com.umc.tomorrow.global.common.base.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "JobBookmark", description = "공고 찜 관련 API")
@RestController
@RequestMapping("/api/v1/job-bookmarks")
@RequiredArgsConstructor
public class JobBookmarkController {
    private final JobBookmarkCommandService commandService;
    private final JobBookmarkQueryService queryService;

    @PostMapping("/{jobId}")
    @Operation(summary = "공고 찜하기", description = "로그인한 사용자가 특정 공고를 찜합니다.")
    @ApiResponse(responseCode = "201", description = "찜하기 성공")
    public ResponseEntity<BaseResponse<JobBookmarkResponseDTO>> saveJobBookmark(
            @AuthenticationPrincipal CustomOAuth2User user,
            @PathVariable Long jobId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.onSuccessCreate(commandService.save(user.getUserDTO().getId(), jobId)));
    }

    @DeleteMapping("/{jobId}")
    @Operation(summary = "공고 찜 취소", description = "로그인한 사용자가 특정 공고의 찜을 취소합니다.")
    @ApiResponse(responseCode = "200", description = "찜 취소 성공")
    public ResponseEntity<BaseResponse<Void>> deleteJobBookmark(
            @AuthenticationPrincipal CustomOAuth2User user,
            @PathVariable Long jobId
    ) {
        commandService.delete(user.getUserDTO().getId(), jobId);
        return ResponseEntity.ok(BaseResponse.onSuccessDelete(null));
    }

    @GetMapping
    @Operation(summary = "찜한 공고 목록 조회", description = "로그인한 사용자가 찜한 공고 목록을 무한스크롤 방식으로 조회합니다.")
    @ApiResponse(responseCode = "200", description = "찜 목록 조회 성공")
    public ResponseEntity<BaseResponse<GetJobBookmarkListResponseDTO>> getBookmarkedJobs(
            @AuthenticationPrincipal CustomOAuth2User user,
            @Positive @RequestParam(required = false) Long cursor,
            @Positive @RequestParam(defaultValue = "8") int size
    ) {
        return ResponseEntity.ok(BaseResponse.onSuccess(queryService.getList(user.getUserDTO().getId(), cursor, size)));
    }
}
