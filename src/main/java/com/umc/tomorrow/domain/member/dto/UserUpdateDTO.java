/**
 * 사용자 수정용 DTO
 * - 사용자 정보 수정 시 선택적 필드들의 validation
 * - 모든 필드가 선택적 (null 허용)
 * - 입력된 필드만 validation 수행
 *
 * 작성자: 정여진
 * 생성일: 2025-08-15
 */
package com.umc.tomorrow.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.umc.tomorrow.domain.member.enums.Gender;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateDTO {
    
    @Email(message = "{user.email.invalid}")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", 
             message = "{user.email.pattern}")
    @Size(max = 30, message = "{user.email.size}")
    private String email;

    @Size(max = 10, message = "{user.name.size}")
    private String name;

    private Gender gender;

    @Pattern(regexp = "^01[0-9]-?[0-9]{3,4}-?[0-9]{4}$", 
             message = "{user.phoneNumber.pattern}")
    @Size(max = 20, message = "{user.phoneNumber.size}")
    private String phoneNumber;

    @Size(max = 255, message = "{user.address.size}")
    private String address;

    private Boolean isOnboarded;

    private String provider;

    @Size(max = 10, message = "{user.providerUserId.size}")
    private String providerUserId;

    private Long resumeId;

    @Size(max = 500, message = "{user.profileImageUrl.size}")
    private String profileImageUrl;
}
