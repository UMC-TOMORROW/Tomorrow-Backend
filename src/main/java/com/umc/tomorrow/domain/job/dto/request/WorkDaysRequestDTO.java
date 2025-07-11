package com.umc.tomorrow.domain.job.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "근무 요일 DTO")
public class WorkDaysRequestDTO {

    @Schema(
            description = "요일 선택",
            example = "MON",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    @NotNull
    private Boolean MON;

    @Schema(
            description = "요일 선택",
            example = "TUE",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    @NotNull
    private Boolean TUE;

    @Schema(
            description = "요일 선택",
            example = "WED",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    @NotNull
    private Boolean WED;

    @Schema(
            description = "요일 선택",
            example = "THU",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    @NotNull
    private Boolean THU;

    @Schema(
            description = "요일 선택",
            example = "FRI",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    @NotNull
    private Boolean FRI;

    @Schema(
            description = "요일 선택",
            example = "SAT",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    @NotNull
    private Boolean SAT;

    @Schema(
            description = "요일 선택",
            example = "SUN",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    @NotNull
    private Boolean SUN;

    @Schema(
            description = "요일 협의 가능",
            example = "isDayNegotiable",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    @NotNull(message = "요일 협의 가능 여부는 필수입니다.")
    private Boolean isDayNegotiable;
}
