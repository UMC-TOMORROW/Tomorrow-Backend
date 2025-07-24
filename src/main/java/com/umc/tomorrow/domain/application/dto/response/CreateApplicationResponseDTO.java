/**
 * 일자리 지원 응답 DTO
 * 작성자: 이승주
 * 생성일: 2025-07-24
 */
package com.umc.tomorrow.domain.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "일자리 지원서 응답 요청 DTO")
public class CreateApplicationResponseDTO {

    @Schema(description = "지원서 ID", example = "1")
    private Long id;
}
