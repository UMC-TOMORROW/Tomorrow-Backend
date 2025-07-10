/**
 * 인증 관련 API 컨트롤러
 * - POST /api/v1/auth/refresh : 리프레시 토큰으로 액세스 토큰 재발급
 * 
 * 작성자: 정여진
 * 생성일: 2025-07-07
 */
package com.umc.tomorrow.domain.auth.controller;

import com.umc.tomorrow.domain.auth.jwt.JWTUtil;
import com.umc.tomorrow.domain.member.entity.User;
import com.umc.tomorrow.domain.member.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    public AuthController(JWTUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, @RequestHeader(value = "RefreshToken", required = false) String refreshTokenHeader) {
        // 1. 쿠키에서 refreshToken 추출
        String refreshToken = refreshTokenHeader;
        if (refreshToken == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                refreshToken = Arrays.stream(cookies)
                        .filter(cookie -> "RefreshToken".equals(cookie.getName()))
                        .map(Cookie::getValue)
                        .findFirst()
                        .orElse(null);
            }
        }
        if (refreshToken == null) {
            return ResponseEntity.badRequest().body("Refresh token not found");
        }
        // 2. 토큰 유효성 검사 및 사용자 조회
        try {
            // 만료 여부 확인
            if (jwtUtil.isExpired(refreshToken)) {
                return ResponseEntity.status(401).body("Refresh token expired");
            }
            // username 추출
            String username = jwtUtil.getUsername(refreshToken);
            // DB에서 사용자 조회
            User user = userRepository.findByUsername(username);
            if (user == null || user.getRefreshToken() == null || !user.getRefreshToken().equals(refreshToken)) {
                return ResponseEntity.status(401).body("Invalid refresh token");
            }
            // accessToken 재발급
            String newAccessToken = jwtUtil.createJwt(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getRole(),
                60*60*60L
            ); // 기존과 동일한 만료시간
            return ResponseEntity.ok().body(newAccessToken);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid refresh token");
        }
    }
} 