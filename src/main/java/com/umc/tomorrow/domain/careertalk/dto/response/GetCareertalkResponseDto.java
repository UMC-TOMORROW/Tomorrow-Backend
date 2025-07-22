/**
 * 커리어톡 게시글 단일 조회 응답 DTO
 * 커리어톡 게시글 단일 조회 API의 응답 구조 정의
 * 작성자: 이승주
 * 생성일: 2025-07-10
 * 수정일: 2025-07-20
 */
package com.umc.tomorrow.domain.careertalk.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Builder
@Schema(description = "커리어톡 게시글 단일 조회 응답 DTO")
public class GetCareertalkResponseDto {

    @Schema(description = "게시글 ID", example = "1")
    private Long id;

    @Schema(description = "카테고리", example = "무료 자격증 추천")
    private String category;

    @Schema(description = "제목", example = "요양보호사 기초과정")
    private String title;

    @Schema(description = "작성일시", example = "2025-07-08T09:00:00")
    private LocalDateTime createdAt;

}
