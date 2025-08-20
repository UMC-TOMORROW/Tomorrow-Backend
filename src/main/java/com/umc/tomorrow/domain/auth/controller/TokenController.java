package com.umc.tomorrow.domain.auth.controller;

import com.umc.tomorrow.domain.auth.dto.TokenRefreshRequest;
import com.umc.tomorrow.domain.auth.dto.TokenRefreshResponse;
import com.umc.tomorrow.domain.auth.service.TokenRefreshService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class TokenController {

    private final TokenRefreshService tokenRefreshService;

    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponse> refreshToken(@RequestBody TokenRefreshRequest request) {
        TokenRefreshResponse response = tokenRefreshService.refreshToken(request);
        
        if (response.getAccessToken() != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}
