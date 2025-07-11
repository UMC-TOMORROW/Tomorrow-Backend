package com.umc.tomorrow.domain.job.dto;

import com.umc.tomorrow.domain.job.enums.RegistrantType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class JobSessionDTO {
    private String title;
    private String jobCategory;
    private LocalDateTime deadline;
    private RegistrantType registrantType;
}