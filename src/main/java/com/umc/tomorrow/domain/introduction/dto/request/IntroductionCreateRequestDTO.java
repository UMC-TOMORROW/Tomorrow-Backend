package com.umc.tomorrow.domain.introduction.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "자기소개 등록 요청 DTO")
public class IntroductionCreateRequestDTO {

    @Schema(description = "자기소개 본문",
            example = "성장과정, 지원동기, 장점 등을 자유롭게 작성해주세요.")
    @NotBlank(message = "{introduction.content.notblank}")
    @Size(max = 100, message = "{introduction.content.size}")
    private String content;
}
