package com.umc.tomorrow.domain.job.dto;

import com.umc.tomorrow.domain.job.enums.RegistrantType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "일자리 등록 임시저장 DTO")
public class JobSessionDTO { //임시
    private String title;
    private LocalDateTime deadline;
    private RegistrantType registrantType;
}