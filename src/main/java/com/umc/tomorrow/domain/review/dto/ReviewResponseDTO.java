package com.umc.tomorrow.domain.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "리뷰 조회 응답 DTO")
public class ReviewResponseDTO {

    @Schema(description = "평점", example = "4")
    private int stars;

    @Schema(description = "후기", example = "체계가 잘 잡혀있어서 근무하기 좋았습니다.")
    private String review;

    @Schema(description = "리뷰 작성일", example = "2025-08-01")
    private LocalDateTime createdAt;

}
