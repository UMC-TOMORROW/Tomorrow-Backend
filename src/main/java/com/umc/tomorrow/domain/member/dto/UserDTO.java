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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import com.umc.tomorrow.domain.member.enums.Gender;
import com.umc.tomorrow.global.common.base.BaseEntity;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO extends BaseEntity {
    private Long id; // 회원 고유 ID
    private String role; 
    private String username; // 사용자명(로그인 ID 또는 소셜 ID)

    @Email(message = "{user.email.invalid}")
    @NotBlank(message = "{user.email.notblank}")
    @Size(max = 30, message = "{user.email.size}")
    private String email;

    @NotBlank(message = "{user.name.notblank}")
    @Size(max = 10, message = "{user.name.size}")
    private String name; 
    @NotNull(message = "{user.gender.notnull}")
    private Gender gender; 

    @NotBlank(message = "{user.phoneNumber.notblank}")
    @Size(max = 20, message = "{user.phoneNumber.size}")
    private String phoneNumber; 

    @Size(max = 255, message = "{user.address.size}")
    private String address;

    @NotNull(message = "{user.status.notnull}")
    private String status; // 상태 (ACTIVE, INACTIVE)

    private LocalDateTime inactiveAt; // 비활성화 일시

    @NotNull(message = "{user.isOnboarded.notnull}")
    private Boolean isOnboarded; 

    @NotNull(message = "{user.provider.notnull}")
    private String provider; // 소셜 제공자 (KAKAO, NAVER, GOOGLE)

    @NotBlank(message = "{user.providerUserId.notblank}")
    @Size(max = 10, message = "{user.providerUserId.size}")
    private String providerUserId; 
    
    @NotNull(message = "{user.resumeId.notnull}")
    private Long resumeId; 

    @Size(max = 500, message = "{user.profileImageUrl.size}")
    private String profileImageUrl; 
}
