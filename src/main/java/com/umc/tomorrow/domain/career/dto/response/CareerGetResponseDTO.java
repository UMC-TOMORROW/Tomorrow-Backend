package com.umc.tomorrow.domain.career.dto.response;

import com.umc.tomorrow.domain.career.enums.WorkPeriodType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "경력 조회 응답 DTO")
public class CareerGetResponseDTO {

    @Schema(description = "경력 ID", example = "4")
    private Long careerId;

    @Schema(description = "회사명", example = "(주)내일")
    private String company;

    @Schema(description = "했던 업무 설명", example = "저는 회계업무를 해왔습니다.")
    private String description;

    @Schema(description = "근무했던 년도", example ="2024")
    private int workedYear;

    @Schema(description = "일한 기간", example = "MORE_THAN_THREE_YEARS")
    private WorkPeriodType workedPeriod;
}
