/**
 * User <-> UserDTO 변환
 * - User Entity와 UserDTO 간 변환 및 업데이트
 *
 * 작성자: 정여진
 * 생성일: 2025-07-13
 */
package com.umc.tomorrow.domain.member.dto;

import com.umc.tomorrow.domain.member.entity.User;

public class UserConverter {
    public static UserDTO toDTO(User user) {
        if (user == null) return null;
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .gender(user.getGender() != null ? com.umc.tomorrow.domain.member.enums.Gender.valueOf(user.getGender().name()) : null)
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .status(user.getStatus() != null ? user.getStatus().name() : null)
                .inactiveAt(user.getInactiveAt())
                .isOnboarded(user.getIsOnboarded())
                .provider(user.getProvider() != null ? user.getProvider().name() : null)
                .providerUserId(user.getProviderUserId())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .resumeId(user.getResumeId())
                .build();
    }

    public static void updateEntity(User user, UserDTO dto) {
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getName() != null) user.setName(dto.getName());
        if (dto.getGender() != null) user.setGender(User.Gender.valueOf(String.valueOf(dto.getGender())));
        if (dto.getPhoneNumber() != null) user.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getAddress() != null) user.setAddress(dto.getAddress());
        if (dto.getStatus() != null) user.setStatus(User.Status.valueOf(dto.getStatus()));
        if (dto.getInactiveAt() != null) user.setInactiveAt(dto.getInactiveAt());
        if (dto.getIsOnboarded() != null) user.setIsOnboarded(dto.getIsOnboarded());
        if (dto.getProvider() != null) user.setProvider(User.Provider.valueOf(dto.getProvider()));
        if (dto.getProviderUserId() != null) user.setProviderUserId(dto.getProviderUserId());
        if (dto.getCreatedAt() != null) user.setCreatedAt(dto.getCreatedAt());
        if (dto.getUpdatedAt() != null) user.setUpdatedAt(dto.getUpdatedAt());
        if (dto.getResumeId() != null) user.setResumeId(dto.getResumeId());
    }
} 