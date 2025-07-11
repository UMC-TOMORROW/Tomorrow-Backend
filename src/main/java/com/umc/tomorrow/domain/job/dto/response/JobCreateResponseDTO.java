package com.umc.tomorrow.domain.job.dto.response;

import com.umc.tomorrow.domain.job.enums.RegistrantType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class JobCreateResponseDTO {
    private Long jobId;
}

//개인, 사업자 공통 응답