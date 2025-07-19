/**
 * PreferenceConverter
 * - Preference Entity와 PreferencesDTO 간 변환 및 업데이트
 * 
 * 작성자: 정여진
 * 생성일: 2025-07-17
 */
package com.umc.tomorrow.domain.preferences.converter;

import com.umc.tomorrow.domain.preferences.entity.Preference;
import com.umc.tomorrow.domain.preferences.dto.PreferencesDTO;

public class PreferenceConverter {
    /**
     * Entity -> DTO 변환
     */
    public static PreferencesDTO toDTO(Preference entity) {
        return PreferencesDTO.builder()
            .preferences(entity.getPreferences())
            .build();
    }

    /**
     * DTO -> Entity 업데이트
     */
    public static void updateEntity(Preference entity, PreferencesDTO dto) {
        if (dto.getPreferences() != null) {
            entity.setPreferences(dto.getPreferences());
        }
    }
} 