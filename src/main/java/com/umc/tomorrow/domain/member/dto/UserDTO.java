package com.umc.tomorrow.domain.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

    private String role;
    private String name;
    private String username;
    private String refreshToken;
}
