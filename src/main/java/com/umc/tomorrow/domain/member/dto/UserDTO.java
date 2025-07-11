package com.umc.tomorrow.domain.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

    private Long id;
    private String role;
    private String name;
    private String username;
    private String refreshToken;

    public static UserDTO from(com.umc.tomorrow.domain.member.entity.User user) {
        if (user == null) return null;
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setName(user.getName());
        dto.setRole(user.getRole());
        dto.setRefreshToken(user.getRefreshToken());
        return dto;
    }
}
