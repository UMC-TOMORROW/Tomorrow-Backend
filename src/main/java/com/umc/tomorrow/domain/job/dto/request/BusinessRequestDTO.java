package com.umc.tomorrow.domain.job.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "사업자 인증 요청 DTO")
public class BusinessRequestDTO {

    @Schema(description = "사업자 번호",
            example = "12345678",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{business.bizNumber.notblank}")
    private String bizNumber;

    @Schema(description = "회사명",
            example = "내일",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "{business.companyName.notnull}")
    private String companyName;

    @Schema(description = "대표이름",
            example = "한지혜",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{business.name.notblank}")
    private String ownerName;

    @Schema(description = "창립일",
            example = "2025-07-11",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "{business.openingDate.notnull}")
    private LocalDate openingDate;
}
