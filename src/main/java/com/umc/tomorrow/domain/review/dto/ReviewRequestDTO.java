/**
 * 후기(리뷰) ReviewRequestDTO
 *
 * 작성자: 정여진
 * 생성일: 2025-07-11
 */
package com.umc.tomorrow.domain.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Builder
public class ReviewRequestDTO {
    @NotNull(message = "{review.postId.notnull}")
    private final Long postId;

    @NotNull(message = "{review.stars.notnull}")
    @Min(value = 0, message = "{review.stars.min}")
    @Max(value = 5, message = "{review.stars.max}")
    private final int stars;

    @NotBlank(message = "{review.review.notnull}")
    private final String review;
}