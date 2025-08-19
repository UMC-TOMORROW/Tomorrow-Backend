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
import java.time.format.DateTimeFormatter;
public class UserResponseConverter {
    public static UserResponseDTO toResponseDTO(User user) {

        final DateTimeFormatter ISO_FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        if (user == null) return null;

        String role = (user.getMemberType() != null) ? user.getMemberType().name() : null;
        String status = (user.getStatus() != null) ? user.getStatus().name() : null;
        String inactiveAt = (user.getInactiveAt() != null) ? user.getInactiveAt().format(ISO_FMT) : null;
        String provider = (user.getProvider() != null) ? user.getProvider().name() : null;

        return new UserResponseDTO(
                user.getId(),
                role,
                user.getUsername(),
                user.getEmail(),
                user.getName(),
                user.getGender(),
                user.getPhoneNumber(),
                user.getAddress(),
                status,
                inactiveAt,
                user.getIsOnboarded(),
                provider,
                user.getProviderUserId(),
                user.getResumeId()
        );
    }

    public static void updateEntity(User user, UserUpdateDTO dto) {
        if (user == null || dto == null) return;

        if (dto.getEmail() != null)        user.setEmail(dto.getEmail());
        if (dto.getName() != null)         user.setName(dto.getName());
        if (dto.getGender() != null)       user.setGender(dto.getGender());
        if (dto.getPhoneNumber() != null)  user.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getAddress() != null)      user.setAddress(dto.getAddress());
        if (dto.getIsOnboarded() != null)  user.setIsOnboarded(dto.getIsOnboarded());
        if (dto.getResumeId() != null)     user.setResumeId(dto.getResumeId());
    }
} 