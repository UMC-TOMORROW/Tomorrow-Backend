/**
 * 인증 관련 API 컨트롤러
 * - POST /api/v1/auth/refresh : 리프레시 토큰으로 액세스 토큰 재발급
 * - POST /api/v1/auth/logout  : 로그아웃
 *
 * 작성자: 정여진
 * 수정자: ChatGPT
 */
package com.umc.tomorrow.domain.auth.controller;

import com.umc.tomorrow.domain.auth.excpetion.code.AuthErrorStatus;
import com.umc.tomorrow.domain.auth.jwt.JWTUtil;
import com.umc.tomorrow.domain.auth.security.CustomOAuth2User;
import com.umc.tomorrow.domain.member.entity.User;
import com.umc.tomorrow.domain.member.exception.code.MemberErrorStatus;
import com.umc.tomorrow.domain.member.repository.UserRepository;
import com.umc.tomorrow.global.common.exception.RestApiException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;
    private final Environment env; // 현재 profile 가져오기용

    public static final long REFRESH_EXP_MS = 1000L * 60L * 60L * 24L * 14L; // 2주
    public static final long ACCESS_EXP_MS  = 1000L * 60L * 60L;             // 1시간

    public AuthController(JWTUtil jwtUtil, UserRepository userRepository, Environment env) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.env = env;
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        // 쿠키에서 RefreshToken 찾기
        String refreshToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("RefreshToken".equals(cookie.getName())) { // ✅ 핸들러에 맞춰 대문자
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken == null || refreshToken.isBlank()) {
            throw new RestApiException(AuthErrorStatus.REFRESH_TOKEN_NOT_FOUND);
        }

        try {
            if (jwtUtil.isExpired(refreshToken)) {
                throw new RestApiException(AuthErrorStatus.REFRESH_TOKEN_EXPIRED);
            }

            Long userId = jwtUtil.getUserId(refreshToken);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RestApiException(AuthErrorStatus.REFRESH_TOKEN_INVALID));

            if (user.getRefreshToken() == null || !user.getRefreshToken().equals(refreshToken)) {
                throw new RestApiException(AuthErrorStatus.REFRESH_TOKEN_INVALID);
            }

            // 새 토큰 발급
            String newAccessToken = jwtUtil.createJwt(user.getId(), user.getName(), ACCESS_EXP_MS);
            String newRefreshToken = jwtUtil.createRefreshToken(user.getId(), user.getName(), REFRESH_EXP_MS);

            // DB 갱신
            user.setRefreshToken(newRefreshToken);
            userRepository.save(user);

            // 새 refreshToken을 다시 쿠키에 심음
            addRefreshTokenCookie(response, newRefreshToken);

            return ResponseEntity.ok(Map.of(
                    "accessToken", newAccessToken
            ));
        } catch (RestApiException e) {
            throw e;
        } catch (Exception e) {
            throw new RestApiException(AuthErrorStatus.REFRESH_TOKEN_INVALID);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            HttpServletResponse response
    ) {
        Long userId = customOAuth2User.getUserResponseDTO().getId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestApiException(MemberErrorStatus.MEMBER_NOT_FOUND));

        // DB에서 Refresh Token 제거
        user.setRefreshToken(null);
        userRepository.save(user);

        // === 쿠키 제거 ===
        removeCookie(response, "RefreshToken");
        removeCookie(response, "Authorization");

        return ResponseEntity.ok("로그아웃 성공");
    }

    private void removeCookie(HttpServletResponse response, String name) {
        String setCookieHeader = String.format(
                "%s=; Max-Age=0; Path=/; HttpOnly; %s %s SameSite=None",
                name,
                isProd() ? "Domain=umctomorrow.shop;" : "",
                isProd() ? "Secure;" : ""
        );
        response.addHeader("Set-Cookie", setCookieHeader);
    }


    private void addRefreshTokenCookie(HttpServletResponse response, String token) {
        String setCookieHeader = String.format(
                "RefreshToken=%s; Max-Age=%d; Path=/; HttpOnly; %s %s SameSite=None",
                token,
                (int) (REFRESH_EXP_MS / 1000L),
                isProd() ? "Domain=umctomorrow.shop;" : "",
                isProd() ? "Secure;" : ""
        );
        response.addHeader("Set-Cookie", setCookieHeader);
    }



    private boolean isProd() {
        String[] profiles = env.getActiveProfiles();
        for (String p : profiles) {
            if ("prod".equalsIgnoreCase(p)) return true;
        }
        return false;
    }
}
