package com.umc.tomorrow.domain.job.dto.response;

import com.umc.tomorrow.domain.job.enums.RegistrantType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "일자리 생성 응답 DTO")
public class JobCreateResponseDTO {
    @Schema(description = "일자리ID", example = "1")
    private Long jobId;
}

//개인, 사업자 공통 응답