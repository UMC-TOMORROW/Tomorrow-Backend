/**
 * 커리어톡 게시글 생성 요청 DTO
 * 커리어톡 게시글 생성 API의 요청 구조 정의
 * 작성자: 이승주
 * 생성일: 2025-07-10
 * 수정일: 2025-07-20
 */
package com.umc.tomorrow.domain.careertalk.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "커리어톡 게시글 생성 요청 DTO")
public class CreateCareertalkRequestDto {

    @Schema(
            description = "게시글 카테고리",
            example = "INTERVIEW",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "careertalk.category.notblank")
    @Size(max = 20, message = "careertalk.category.size")
    private String category;

    @Schema(
            description = "게시글 제목",
            example = "커리어 인터뷰 준비 방법 공유합니다!",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "careertalk.title.notblank")
    @Size(max = 50, message = "careertalk.title.size")
    private String title;

    @Schema(
            description = "게시글 본문 내용",
            example = "안녕하세요! 저는 최근에 취업 준비하면서 이렇게 준비했어요..."
    )
    @NotBlank(message = "careertalk.content.notblank")
    private String content;

}
