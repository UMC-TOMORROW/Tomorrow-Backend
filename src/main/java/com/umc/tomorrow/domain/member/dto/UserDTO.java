/**
 * 회원 정보 DTO
 * - User Entity와 1:1 매핑되는 데이터 전송 객체
 * - Validation 어노테이션 및 메시지 properties 연동
 *
 * 작성자: 정여진
 * 생성일: 2025-07-11
 */
package com.umc.tomorrow.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserDTO {
    private final Long id;

    @Email(message = "{user.email.invalid}")
    @NotBlank(message = "{user.email.notblank}")
    @Size(max = 30, message = "{user.email.size}")
    private final String email;

    @NotBlank(message = "{user.name.notblank}")
    @Size(max = 10, message = "{user.name.size}")
    private final String name;

    @NotNull(message = "{user.gender.notnull}")
    private final String gender; // ENUM("MALE", "FEMALE")

    @NotBlank(message = "{user.phoneNumber.notblank}")
    @Size(max = 20, message = "{user.phoneNumber.size}")
    private final String phoneNumber;

    @Size(max = 255, message = "{user.address.size}")
    private final String address;

    @NotNull(message = "{user.status.notnull}")
    private final String status; // ENUM("ACTIVE", "INACTIVE")

    private final LocalDateTime inactiveAt;

    @NotNull(message = "{user.isOnboarded.notnull}")
    private final Boolean isOnboarded;

    @NotNull(message = "{user.provider.notnull}")
    private final String provider; // ENUM("KAKAO", "NAVER", "GOOGLE")

    @NotBlank(message = "{user.providerUserId.notblank}")
    @Size(max = 10, message = "{user.providerUserId.size}")
    private final String providerUserId;

    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    @NotNull(message = "{user.resumeId.notnull}")
    private final Long resumeId;
}
