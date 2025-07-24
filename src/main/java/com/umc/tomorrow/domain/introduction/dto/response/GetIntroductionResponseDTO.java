package com.umc.tomorrow.domain.introduction.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "자기소개 조회 응답 DTO")
public class GetIntroductionResponseDTO {

    @Schema(description = "자기소개 내용", example = "안녕하세요 저는 ㅇㅇㅇ입니다.")
    private String content;
}
