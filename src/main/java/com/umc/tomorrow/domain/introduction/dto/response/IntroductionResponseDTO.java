package com.umc.tomorrow.domain.introduction.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "자기소개 추가, 수정 응답 DTO")
public class IntroductionResponseDTO {

    @Schema(description = "자기소개ID", example = "1")
    private Long introductionId;


}
