/**
 * 이메일 요청 DTO 클래스
 * 작성자: 이승주
 * 작성일:2025-07-28
 */
package com.umc.tomorrow.domain.email.dto.request;

import com.umc.tomorrow.domain.email.enums.EmailType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "이메일 발송 요청 DTO")
public class EmailRequestDTO {

    @Schema(
            description = "메일 타입 (지원 완료, 합격 안내, 불합격 안내 등)",
            example = "JOB_APPLY",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "email.type.notnull")
    private EmailType type;

    @Schema(
            description = "지원한 공고 ID",
            example = "12",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "email.jobId.notnull")
    private Long jobId;

}
