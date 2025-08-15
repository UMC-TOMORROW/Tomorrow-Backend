/**
 * 사용자 생성용 DTO
 * - 사용자 등록 시 필수 필드들의 validation을 강제
 * - 모든 필수 필드가 반드시 입력되어야 함
 *
 * 작성자: 정여진
 * 생성일: 2025-08-15
 */
package com.umc.tomorrow.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.umc.tomorrow.domain.member.enums.Gender;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDTO {
    
    @Email(message = "{user.email.invalid}")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", 
             message = "{user.email.pattern}")
    @NotBlank(message = "{user.email.notblank}")
    @Size(max = 30, message = "{user.email.size}")
    private String email;

    @NotBlank(message = "{user.name.notblank}")
    @Size(max = 10, message = "{user.name.size}")
    private String name;

    @NotNull(message = "{user.gender.notnull}")
    private Gender gender;

    @NotBlank(message = "{user.phoneNumber.notblank}")
    @Pattern(regexp = "^01[0-9]-?[0-9]{3,4}-?[0-9]{4}$", 
             message = "{user.phoneNumber.pattern}")
    @Size(max = 20, message = "{user.phoneNumber.size}")
    private String phoneNumber;

    @Size(max = 255, message = "{user.address.size}")
    private String address;

    @NotNull(message = "{user.isOnboarded.notnull}")
    private Boolean isOnboarded;

    @NotNull(message = "{user.provider.notnull}")
    private String provider;

    @NotBlank(message = "{user.providerUserId.notblank}")
    @Size(max = 10, message = "{user.providerUserId.size}")
    private String providerUserId;
}
