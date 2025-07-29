/**
 * 인증 관련 API 컨트롤러
 * - POST /api/v1/auth/refresh : 리프레시 토큰으로 액세스 토큰 재발급
 * 
 * 작성자: 정여진
 * 작성일: 2025-07-07
 * 수정일 : 2025-07-29
 */
package com.umc.tomorrow.domain.auth.controller;

import com.umc.tomorrow.domain.auth.exception.code.AuthErrorStatus;
import com.umc.tomorrow.domain.auth.jwt.JWTUtil;
import com.umc.tomorrow.domain.member.entity.User;
import com.umc.tomorrow.domain.member.repository.UserRepository;
import com.umc.tomorrow.global.common.exception.RestApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    public static final long REFRESH_EXP_MIN = 1000L * 60L * 60L * 24L * 14L; // 2주
    public static final long ACCRESS_EXP_MIN = 60 * 60 * 1000L; // 1시간

    public AuthController(JWTUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, @RequestHeader(value = "RefreshToken", required = false) String refreshTokenHeader) {
        // 1. 쿠키에서 refreshToken 추출
        String refreshToken = refreshTokenHeader;

        if (refreshToken == null || refreshToken.isBlank()) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                refreshToken = Arrays.stream(cookies)
                        .filter(cookie -> "RefreshToken".equals(cookie.getName()))
                        .map(Cookie::getValue)
                        .findFirst()
                        .orElse(null);
            }
        }

        System.out.println("DEBUG: RefreshToken value = '" + refreshToken + "'");
        // 2. 빈 문자열 체크
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new RestApiException(AuthErrorStatus.REFRESH_TOKEN_NOT_FOUND);
        }

        try {
            System.out.println("만료 여부 확인 전");
            // 토큰 만료 여부 먼저 확인해야 함.
            if (jwtUtil.isExpired(refreshToken)) {
                throw new RestApiException(AuthErrorStatus.REFRESH_TOKEN_EXPIRED); // AUTH4002
            }

            Long userId = jwtUtil.getUserId(refreshToken);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RestApiException(AuthErrorStatus.REFRESH_TOKEN_INVALID));

            if (user == null || user.getRefreshToken() == null || !user.getRefreshToken().equals(refreshToken)) { // 아 여기가 문제네.
                throw new RestApiException(AuthErrorStatus.REFRESH_TOKEN_INVALID);
            }

            // 새 accessToken, 새 refreshToken 생성
            String newAccessToken = jwtUtil.createJwt(user.getId(), user.getName(), ACCRESS_EXP_MIN);
            String newRefreshToken = jwtUtil.createRefreshToken(user.getId(), user.getName(), REFRESH_EXP_MIN); // 예: 14일

            // DB에 새 refreshToken 저장
            user.setRefreshToken(newRefreshToken);
            userRepository.save(user);

            // 응답에 둘 다 포함
            return ResponseEntity.ok().body(Map.of(
                    "accessToken", newAccessToken,
                    "refreshToken", newRefreshToken
            ));

        } catch (RestApiException e) {
            throw e; // 전체 Exception이 아니라 RestAPIException으로
        } catch (Exception e){
            System.err.println("리프레시 토큰 검증 로직 중 예외 발생: " + e.getMessage());
            throw new RestApiException(AuthErrorStatus.REFRESH_TOKEN_INVALID); // AUTH4003
        }
    }
} 