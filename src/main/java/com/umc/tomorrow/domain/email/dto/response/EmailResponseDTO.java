/**
 * 이메일 응답 DTO
 * 작성자: 이승주
 * 작성일: 2025-07-27
 */
package com.umc.tomorrow.domain.email.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "이메일 메시지 응답 DTO")
public class EmailResponseDTO {

    @Schema(description = "이메일 수신자")
    private String to;
}
