package com.umc.tomorrow.domain.career.dto.request;

import com.umc.tomorrow.domain.career.enums.WorkPeriodType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "이력서 - 경력 수정 요청 DTO")
public class CareerUpdateRequestDTO {

    @Schema(
            description = "일했던 곳 회사명",
            example = "(주)내일",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "career.company.notnull")
    private String company;

    @Schema(
            description = "일했던 직무 설명",
            example = "일반 사무 업무를 주로 했습니다.",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "career.description.notnull")
    private String description;

    @Schema(
            description = "일했던 년도",
            example = "2024",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "career.workedYear.notnull")
    private int workedYear;

    @Schema(
            description = "일했던 기간",
            example = "LESS_THAN_3_MONTHS",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "career.workedPeriod.notnull")
    private WorkPeriodType workedPeriod;
}
