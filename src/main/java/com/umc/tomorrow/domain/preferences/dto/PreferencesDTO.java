/**
 * PreferencesDTO
 * - 사용자의 희망 조건 목록을 전달하는 DTO
 * 
 * 작성자: 정여진
 * 생성일: 2025-07-17
 */
package com.umc.tomorrow.domain.preferences.dto;

import com.umc.tomorrow.domain.preferences.entity.PreferenceType;
import lombok.Builder;
import lombok.Getter;
import java.util.Set;


@Getter
@Builder
public class PreferencesDTO {
    /** 희망 조건 목록 */
    private final Set<PreferenceType> preferences;
} 