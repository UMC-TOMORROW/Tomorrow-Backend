package com.umc.tomorrow.domain.preferences.dto;

import com.umc.tomorrow.domain.preferences.entity.PreferenceType;
import lombok.Builder;
import lombok.Getter;
import java.util.Set;

/**
 * PreferencesDTO
 * - 사용자의 희망 조건 목록을 전달하는 DTO
 */
@Getter
@Builder
public class PreferencesDTO {
    /** 희망 조건 목록 */
    private final Set<PreferenceType> preferences;
} 