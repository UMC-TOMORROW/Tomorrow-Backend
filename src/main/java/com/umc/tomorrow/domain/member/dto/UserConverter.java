package com.umc.tomorrow.domain.member.dto;

import com.umc.tomorrow.domain.member.entity.User;

public class UserConverter {
    public static UserDTO toDTO(User user) {
        if (user == null) return null;
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setGender(user.getGender() != null ? user.getGender().name() : null);
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setAddress(user.getAddress());
        dto.setStatus(user.getStatus() != null ? user.getStatus().name() : null);
        dto.setInactiveAt(user.getInactiveAt());
        dto.setIsOnboarded(user.getIsOnboarded());
        dto.setProvider(user.getProvider() != null ? user.getProvider().name() : null);
        dto.setProviderUserId(user.getProviderUserId());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setResumeId(user.getResumeId());
        return dto;
    }

    public static void updateEntity(User user, UserDTO dto) {
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getName() != null) user.setName(dto.getName());
        if (dto.getGender() != null) user.setGender(User.Gender.valueOf(dto.getGender()));
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