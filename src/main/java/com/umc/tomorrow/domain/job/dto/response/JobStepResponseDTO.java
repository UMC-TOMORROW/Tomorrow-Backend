package com.umc.tomorrow.domain.job.dto.response;

import com.umc.tomorrow.domain.job.enums.RegistrantType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "일자리 생성 응답 DTO")
public class JobStepResponseDTO {

    @Schema(description = "개인 or 회사", example = "BUSINESS")
    private RegistrantType registrantType;

    @Schema(description = "일자리 폼 저장 상태", example = "job_form_saved")
    private String step;
}