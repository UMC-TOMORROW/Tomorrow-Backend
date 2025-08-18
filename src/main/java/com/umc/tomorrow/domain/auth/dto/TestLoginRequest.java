package com.umc.tomorrow.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestLoginRequest {
    private Long userId;       // ex) 1
    private String name;       // ex) "테스트 유저"
    private String username;   // ex) "google_test"
    private String role;       // ex) "ROLE_USER"
}
