package com.umc.tomorrow.domain.careertalk.dto.response;

import com.umc.tomorrow.domain.careertalk.entity.Careertalk;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "커리어톡 게시글 생성 응답 DTO")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CreateCareertalkResponseDto {

    @Schema(description = "커리어톡 게시글 ID", example = "1")
    private Long id;

    @Schema(description = "게시글 카테고리", example = "무료 자격증 추천")
    private String category;

    @Schema(description = "게시글 제목", example = "요양보호사 기초과정")
    private String title;

    @Schema(description = "게시글 내용", example = "일은 하고 싶은데, 건강이 따라줄까 걱정이에요...")
    private String content;

    @Schema(description = "게시글 작성 일시", example = "2025-07-08T09:00:00")
    private String createdAt;

    /**
     * Careertalk 엔티티로부터 응답 DTO 생성
     *
     * @param careertalk Careertalk 엔티티
     * @return CreateCareertalkResponseDto 인스턴스
     */
    public static CreateCareertalkResponseDto fromEntity(Careertalk careertalk) {
        return CreateCareertalkResponseDto.builder()
                .id(careertalk.getId())
                .category(careertalk.getCategory())
                .title(careertalk.getTitle())
                .content(careertalk.getContent())
                .createdAt(careertalk.getCreatedAt().toString())
                .build();
    }

}
