package com.umc.tomorrow.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserDTO {
    private Long id;

    @Email(message = "{user.email.invalid}")
    @NotBlank(message = "{user.email.notblank}")
    @Size(max = 30, message = "{user.email.size}")
    private String email;

    @NotBlank(message = "{user.name.notblank}")
    @Size(max = 10, message = "{user.name.size}")
    private String name;

    @NotNull(message = "{user.gender.notnull}")
    private String gender; // ENUM("MALE", "FEMALE")

    @NotBlank(message = "{user.phoneNumber.notblank}")
    @Size(max = 20, message = "{user.phoneNumber.size}")
    private String phoneNumber;

    @Size(max = 255, message = "{user.address.size}")
    private String address;

    @NotNull(message = "{user.status.notnull}")
    private String status; // ENUM("ACTIVE", "INACTIVE")

    private LocalDateTime inactiveAt;

    @NotNull(message = "{user.isOnboarded.notnull}")
    private Boolean isOnboarded;

    @NotNull(message = "{user.provider.notnull}")
    private String provider; // ENUM("KAKAO", "NAVER", "GOOGLE")

    @NotBlank(message = "{user.providerUserId.notblank}")
    @Size(max = 10, message = "{user.providerUserId.size}")
    private String providerUserId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @NotNull(message = "{user.resumeId.notnull}")
    private Long resumeId;
}
