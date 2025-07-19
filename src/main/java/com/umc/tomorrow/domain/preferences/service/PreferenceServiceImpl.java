/**
 * PreferenceServiceImpl
 * - 희망 조건 저장/수정 비즈니스 로직 구현체
 * 작성자: 정여진
 * 생성일: 2025-07-17
 */
package com.umc.tomorrow.domain.preferences.service;

import com.umc.tomorrow.domain.preferences.dto.PreferencesDTO;
import com.umc.tomorrow.domain.preferences.entity.Preference;
import com.umc.tomorrow.domain.preferences.repository.PreferenceRepository;
import com.umc.tomorrow.domain.preferences.converter.PreferenceConverter;
import com.umc.tomorrow.domain.member.entity.User;
import com.umc.tomorrow.domain.member.repository.UserRepository;
import com.umc.tomorrow.global.common.exception.RestApiException;
import com.umc.tomorrow.global.common.exception.code.GlobalErrorStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PreferenceServiceImpl implements PreferenceService {
    private final PreferenceRepository preferenceRepository;
    private final UserRepository userRepository;

    /**
     * 희망 조건 저장
     */
    @Override
    @Transactional
    public PreferencesDTO savePreferences(Long userId, PreferencesDTO dto) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RestApiException(GlobalErrorStatus._NOT_FOUND));
        Preference entity = Preference.builder()
            .user(user)
            .preferences(dto.getPreferences())
            .build();
        preferenceRepository.save(entity);
        return PreferenceConverter.toDTO(entity);
    }

    /**
     * 희망 조건 수정
     */
    @Override
    @Transactional
    public PreferencesDTO updatePreferences(Long userId, PreferencesDTO dto) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RestApiException(GlobalErrorStatus._NOT_FOUND));
        Preference entity = preferenceRepository.findByUser(user)
            .orElseThrow(() -> new RestApiException(GlobalErrorStatus._NOT_FOUND));
        PreferenceConverter.updateEntity(entity, dto);
        preferenceRepository.save(entity);
        return PreferenceConverter.toDTO(entity);
    }
} 