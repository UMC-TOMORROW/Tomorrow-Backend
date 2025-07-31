package com.umc.tomorrow.domain.job.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentType {

    HOURLY("시급"),
    PER_TASK("건당"),
    DAILY("일급"),
    MONTHLY("월급");

    private final String description;
}