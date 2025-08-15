/**
 * User <-> UserResponseDTO 변환
 * - User Entity와 UserResponseDTO 간 변환
 *
 * 작성자: 정여진
 * 생성일: 2025-07-13
 * 수정일: 2025-08-15
 */
package com.umc.tomorrow.domain.member.dto;

import com.umc.tomorrow.domain.member.entity.User;
import com.umc.tomorrow.domain.member.enums.Gender;
import com.umc.tomorrow.domain.member.enums.Provider;
import com.umc.tomorrow.domain.member.enums.UserStatus;

public class UserResponseConverter {
    public static UserResponseDTO toResponseDTO(User user) {
        if (user == null) return null;
        return new UserResponseDTO(
                user.getId(),
                user.getMemberType() != null ? user.getMemberType().name() : null,
                user.getUsername(),
                user.getEmail(),
                user.getName(),
                user.getGender() != null ? com.umc.tomorrow.domain.member.enums.Gender.valueOf(user.getGender().name()) : null,
                user.getPhoneNumber(),
                user.getAddress(),
                user.getStatus() != null ? user.getStatus().name() : null,
                user.getInactiveAt() != null ? user.getInactiveAt().toString() : null,
                user.getIsOnboarded(),
                user.getProvider() != null ? user.getProvider().name() : null,
                user.getProviderUserId(),
                user.getResumeId(),
                user.getProfileImageUrl()
        );
    }

    public static void updateEntity(User user, UserUpdateDTO dto) {
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getName() != null) user.setName(dto.getName());
        if (dto.getGender() != null) user.setGender(Gender.valueOf(String.valueOf(dto.getGender())));
        if (dto.getPhoneNumber() != null) user.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getAddress() != null) user.setAddress(dto.getAddress());
        if (dto.getIsOnboarded() != null) user.setIsOnboarded(dto.getIsOnboarded());
        if (dto.getProvider() != null) user.setProvider(Provider.valueOf(dto.getProvider()));
        if (dto.getProviderUserId() != null) user.setProviderUserId(dto.getProviderUserId());
        if (dto.getResumeId() != null) user.setResumeId(dto.getResumeId());
        if (dto.getProfileImageUrl() != null) user.setProfileImageUrl(dto.getProfileImageUrl());
    }
} 