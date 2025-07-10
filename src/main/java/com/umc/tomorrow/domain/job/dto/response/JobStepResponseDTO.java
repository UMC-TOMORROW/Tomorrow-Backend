package com.umc.tomorrow.domain.job.dto.response;

import com.umc.tomorrow.domain.job.enums.RegistrantType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JobStepResponseDTO {
    private RegistrantType registrantType;
    private String step;
}