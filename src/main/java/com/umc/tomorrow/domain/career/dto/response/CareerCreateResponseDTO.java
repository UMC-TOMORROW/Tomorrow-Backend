package com.umc.tomorrow.domain.career.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "경력 추가, 수정 응답 DTO")
public class CareerCreateResponseDTO {


    @Schema(description = "경력ID", example = "4")
    private Long careerId;
}
