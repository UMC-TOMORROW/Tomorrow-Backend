/**
 * PreferenceService
 * - 희망 조건 저장/수정 비즈니스 로직 인터페이스
 * 작성자: 정여진
 * 생성일: 2025-07-17
 */
package com.umc.tomorrow.domain.preferences.service;

import com.umc.tomorrow.domain.preferences.dto.PreferencesDTO;

public interface PreferenceService {
    /**
     * 희망 조건 저장
     */
    void savePreferences(Long userId, PreferencesDTO dto);

    /**
     * 희망 조건 수정
     */
    void updatePreferences(Long userId, PreferencesDTO dto);
} 