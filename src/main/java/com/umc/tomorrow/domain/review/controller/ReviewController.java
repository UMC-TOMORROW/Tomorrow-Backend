/**
 * ReviewController
 * - 후기 작성 API 컨트롤러
 *
 * 작성자: 정여진진
 * 생성일: 2025-07-17
 */
package com.umc.tomorrow.domain.review.controller;

import com.umc.tomorrow.domain.review.dto.ReviewRequestDTO;
import com.umc.tomorrow.domain.review.service.ReviewService;
import com.umc.tomorrow.domain.auth.security.CustomOAuth2User;
import com.umc.tomorrow.global.common.base.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "review-controller", description = "후기 관련 API")
@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /**
     * 후기 작성 (POST)
     * @param user 인증된 사용자
     * @param dto 후기 요청 DTO
     * @return 저장 결과
     */
    @Operation(summary = "후기 작성", description = "공고에 대한 후기를 작성합니다.")
    @PostMapping
    public ResponseEntity<BaseResponse> saveReview(
            @AuthenticationPrincipal CustomOAuth2User user,
            @Valid @RequestBody ReviewRequestDTO dto) {
        // 실제 DB에 후기 저장
        Long userId = user.getUserDTO().getId();
        reviewService.saveReview(userId, dto);
        return ResponseEntity.ok(
                BaseResponse.onSuccess(Map.of("saved", true))
        );
    }
}