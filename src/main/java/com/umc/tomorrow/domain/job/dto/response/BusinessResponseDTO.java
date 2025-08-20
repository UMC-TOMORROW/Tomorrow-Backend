package com.umc.tomorrow.domain.job.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@Schema(description = "사업자 등록 확인 응답 DTO")
public class BusinessResponseDTO {

    @Schema(description = "사업자 등록 번호")
    private String bizNumber;

    @Schema(description = "회사명")
    private String companyName;

    @Schema(description = "대표자 이름")
    private String ownerName;

    @Schema(description = "개업일")
    private LocalDate openingDate;
}
