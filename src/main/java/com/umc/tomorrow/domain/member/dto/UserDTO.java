/**
 * 회원 정보 DTO
 * - User Entity와 1:1 매핑되는 데이터 전송 객체
 * - Validation 어노테이션 및 메시지 properties 연동
 *
 * 작성자: 정여진
 * 생성일: 2025-07-11
 */
package com.umc.tomorrow.domain.member.dto;

import jakarta.persistence.EntityListeners;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import com.umc.tomorrow.domain.member.enums.Gender;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@Getter
@Builder
public class UserDTO {
    private final Long id; // 회원 고유 ID
    private final String role; // 권한 (예: ROLE_USER, ROLE_ADMIN)
    private final String username; // 사용자명(로그인 ID 또는 소셜 ID)

    @Email(message = "{user.email.invalid}")
    @NotBlank(message = "{user.email.notblank}")
    @Size(max = 50, message = "{user.email.size}")
    private final String email; // 이메일

    @NotBlank(message = "{user.name.notblank}")
    @Size(max = 10, message = "{user.name.size}")
    private final String name; // 이름

    @NotNull(message = "{user.gender.notnull}")
    private final Gender gender; // 성별 (MALE, FEMALE)

    @NotBlank(message = "{user.phoneNumber.notblank}")
    @Size(max = 20, message = "{user.phoneNumber.size}")
    private final String phoneNumber; // 휴대폰 번호

    @Size(max = 255, message = "{user.address.size}")
    private final String address; // 주소

    @NotNull(message = "{user.status.notnull}")
    private final String status; // 상태 (ACTIVE, INACTIVE)

    @LastModifiedDate
    private LocalDateTime inactiveAt; // 비활성화 일시

    @NotNull(message = "{user.isOnboarded.notnull}")
    private final Boolean isOnboarded; // 온보딩 여부

    @NotNull(message = "{user.provider.notnull}")
    private final String provider; // 소셜 제공자 (KAKAO, NAVER, GOOGLE)

    @NotBlank(message = "{user.providerUserId.notblank}")
    @Size(max = 10, message = "{user.providerUserId.size}")
    private final String providerUserId; // 소셜 제공자 ID

    @LastModifiedDate
    private LocalDateTime createdAt; // 생성일시
    @LastModifiedDate
    private LocalDateTime updatedAt; // 수정일시

    @NotNull(message = "{user.resumeId.notnull}")
    private final Long resumeId; // 이력서 ID
}
