package com.umc.tomorrow.domain.careertalk.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Schema(description = "커리어톡 게시글 생성 요청 DTO")
public class CreateCareertalkRequestDto {

    @Schema(
            description = "게시글 카테고리",
            example = "INTERVIEW",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "카테고리 선택은 필수입니다.")
    private String category;

    @Schema(
            description = "게시글 제목",
            example = "커리어 인터뷰 준비 방법 공유합니다!",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @Schema(
            description = "게시글 본문 내용",
            example = "안녕하세요! 저는 최근에 취업 준비하면서 이렇게 준비했어요..."
    )
    @NotBlank(message = "내용은 필수입니다.")
    private String content;

}
