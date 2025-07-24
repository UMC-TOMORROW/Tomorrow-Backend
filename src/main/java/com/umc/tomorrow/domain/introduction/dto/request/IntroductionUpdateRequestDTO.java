package com.umc.tomorrow.domain.introduction.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntroductionUpdateRequestDTO {

    @Schema(description = "자기소개 본문",
            example = "수정할 문구를 적어주세요.")
    @NotBlank(message = "{introduction.content.notblank}")
    @Size(max = 100, message = "{introduction.content.size}")
    private String content;
}