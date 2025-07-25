package com.umc.tomorrow.domain.job.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RegistrantType {

    PERSONAL("개인"),
    BUSINESS("사업자");

    private final String description;
}