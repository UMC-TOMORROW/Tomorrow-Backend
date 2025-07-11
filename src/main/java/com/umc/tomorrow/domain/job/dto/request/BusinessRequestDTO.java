package com.umc.tomorrow.domain.job.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    @Schema(
            description = "사업자 번호",
            example = "12345678",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "사업자 등록 번호는 필수입니다.")
    private String bizNumber;

    @Schema(
            description = "회사명",
            example = "내일",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "회사명은 필수입니다.")
    private String companyName;

    @Schema(
            description = "대표이름",
            example = "한지혜",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "대표 이름은 필수입니다.")
    private String ownerName;

    @Schema(
            description = "창립일",
            example = "2025-07-11",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "창립일은 필수입니다.")
    private LocalDate openingDate;

}
