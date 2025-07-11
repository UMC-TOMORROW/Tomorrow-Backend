package com.umc.tomorrow.domain.job.dto;

import com.umc.tomorrow.domain.job.enums.RegistrantType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class JobSessionDTO { //임시
    private String title;
    private LocalDateTime deadline;
    private RegistrantType registrantType;
}