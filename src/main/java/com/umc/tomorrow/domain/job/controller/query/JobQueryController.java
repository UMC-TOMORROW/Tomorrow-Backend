package com.umc.tomorrow.domain.job.controller.query;

import com.umc.tomorrow.domain.auth.security.CustomOAuth2User;
import com.umc.tomorrow.domain.job.dto.request.MyPostResponseDTO;
import com.umc.tomorrow.domain.job.dto.response.GetRecommendationListResponse;
import com.umc.tomorrow.domain.job.dto.response.JobDetailResponseDTO;
import com.umc.tomorrow.domain.job.service.query.JobQueryService;
import com.umc.tomorrow.global.common.base.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "MyPosts", description = "내가 작성한 공고 조회 API")
public class JobQueryController {

    private final JobQueryService jobQueryService;

    @Operation(summary = "내 모집중 공고 조회", description = "사용자가 작성한 모집중인 공고를 조회합니다.")
    @GetMapping("/my-posts/open")
    public ResponseEntity<BaseResponse<List<MyPostResponseDTO>>> getOpenPosts(
            @AuthenticationPrincipal CustomOAuth2User user
    ) {
        List<MyPostResponseDTO> result = jobQueryService.getMyPosts(user.getUserResponseDTO().getId(), "open");
        return ResponseEntity.ok(BaseResponse.onSuccess(result));
    }

    @Operation(summary = "내 모집완료 공고 조회", description = "사용자가 작성한 모집완료된 공고를 조회합니다.")
    @GetMapping("/my-posts/closed")
    public ResponseEntity<BaseResponse<List<MyPostResponseDTO>>> getClosedPosts(
            @AuthenticationPrincipal CustomOAuth2User user
    ) {
        List<MyPostResponseDTO> result = jobQueryService.getMyPosts(user.getUserResponseDTO().getId(), "closed");
        return ResponseEntity.ok(BaseResponse.onSuccess(result));
    }

    @Operation(summary = "일자리 공고 상세페이지 조회", description = "해당하는 jobId의 공고를 상세페이지로 조회합니다.")
    @GetMapping("/jobs/{jobId}")
    public ResponseEntity<BaseResponse<JobDetailResponseDTO>> getJobDetail(
            @PathVariable Long jobId) {
        JobDetailResponseDTO result = jobQueryService.getJobDetail(jobId);

        return ResponseEntity.ok(BaseResponse.onSuccess(result));
    }

    @GetMapping("recommendations")
    @Operation(summary = "내일 추천 게시글 목록 조회 (무한 스크롤)", description = "내일 추천 게시글 목록을 무한 스크롤 방식으로 조회합니다.")
    @ApiResponse(responseCode = "200", description = "내일 추천 목록 조회 성공")
    public ResponseEntity<BaseResponse<GetRecommendationListResponse>> getTomorrowRecommendations(
            @AuthenticationPrincipal CustomOAuth2User user,
            @Positive @RequestParam(required = false) Long cursor,
            @Positive @RequestParam(defaultValue = "8") int size
    ){
        Long userId = user.getUserResponseDTO().getId();
        GetRecommendationListResponse result = jobQueryService.getTomorrowRecommendations(userId, cursor,size);
        return ResponseEntity.ok(BaseResponse.onSuccess(result));
    }

}
