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
import com.umc.tomorrow.global.redis.RedisService;
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
    private final RedisService redisService;

    public static final long REFRESH_EXP_MS = 1000L * 60L * 60L * 24L * 14L; // 2주
    public static final long ACCESS_EXP_MS  = 1000L * 60L * 60L;             // 1시간

    public AuthController(JWTUtil jwtUtil, UserRepository userRepository, Environment env, RedisService redisService) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.env = env;
        this.redisService = redisService;
    }

    @GetMapping("/check")
    public ResponseEntity<?> checkAuthStatus(@AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        if (customOAuth2User == null) {
            return ResponseEntity.status(401).body(Map.of(
                "code", "AUTH_401",
                "message", "Unauthorized",
                "redirectUrl", "/login"
            ));
        }
        
        return ResponseEntity.ok(Map.of(
            "code", "AUTH_200",
            "message", "Authenticated",
            "user", customOAuth2User.getUserResponseDTO()
        ));
    }

    @GetMapping("/login")
    public ResponseEntity<?> getLoginInfo() {
        return ResponseEntity.ok(Map.of(
            "oauth2Urls", Map.of(
                "google", "/oauth2/authorization/google",
                "naver", "/oauth2/authorization/naver",
                "kakao", "/oauth2/authorization/kakao"
            ),
            "message", "Please choose an OAuth2 provider to login",
            "swaggerTest", "Use these URLs in Swagger or browser to test OAuth2 login"
        ));
    }

    @GetMapping("/oauth2/authorization/{provider}")
    public ResponseEntity<?> redirectToOAuth2(@PathVariable String provider) {
        String redirectUrl = "/oauth2/authorization/" + provider;
        return ResponseEntity.ok(Map.of(
            "message", "Redirecting to " + provider + " OAuth2",
            "redirectUrl", redirectUrl,
            "note", "This endpoint will redirect to the OAuth2 provider's login page"
        ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractRefreshCookie(request);
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new RestApiException(AuthErrorStatus.REFRESH_TOKEN_NOT_FOUND);
        }
        if (jwtUtil.isExpired(refreshToken)) {
            throw new RestApiException(AuthErrorStatus.REFRESH_TOKEN_EXPIRED);
        }

        try {
            Long userId = jwtUtil.getUserId(refreshToken);

            // Redis에서 검증
            String key = buildRtKey(userId, refreshToken);
            String saved = redisService.get(key);
            if (saved == null || !saved.equals(refreshToken)) {
                throw new RestApiException(AuthErrorStatus.REFRESH_TOKEN_INVALID);
            }

            // 로테이션: 기존 키 삭제 → 새 refresh 발급·저장
            redisService.delete(key);
            // username, name, role은 기존 JWT에서 꺼냄
            String username = jwtUtil.getUsername(refreshToken);
            String name     = jwtUtil.getName(refreshToken);
            String role     = jwtUtil.getRole(refreshToken);

            String newRefresh = jwtUtil.createRefreshToken(userId, username, REFRESH_EXP_MS);
            String newKey = buildRtKey(userId, newRefresh);
            redisService.set(newKey, newRefresh, java.time.Duration.ofMillis(REFRESH_EXP_MS));

            String newAccess = jwtUtil.createJwt(userId, name, username, role, ACCESS_EXP_MS);

            // 쿠키 교체
            addRefreshTokenCookie(response, request, newRefresh);

            return ResponseEntity.ok(Map.of("accessToken", newAccess));

        } catch (RestApiException e) {
            throw e;
        } catch (Exception e) {
            throw new RestApiException(AuthErrorStatus.REFRESH_TOKEN_INVALID);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
                                    HttpServletResponse response,
                                    HttpServletRequest request) {
        Long userId = customOAuth2User.getUserResponseDTO().getId();

        String refreshToken = extractRefreshCookie(request);
        if (refreshToken != null && !refreshToken.isBlank()) {
            redisService.delete(buildRtKey(userId, refreshToken));
        }

        removeCookie(response, request,"RefreshToken");
        removeCookie(response, request,"Authorization");

        return ResponseEntity.ok("로그아웃 성공");
    }

    private String extractRefreshCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if ("RefreshToken".equals(cookie.getName())) return cookie.getValue();
        }
        return null;
    }

    private void removeCookie(HttpServletResponse response,
                              HttpServletRequest request,
                              String name) {
        boolean isLocal = isLocalRequest(request);
        String setCookieHeader = isLocal
                ? String.format("%s=; Max-Age=0; Path=/; HttpOnly; SameSite=Lax", name)
                : String.format("%s=; Max-Age=0; Path=/; HttpOnly; Secure; SameSite=None", name);
        response.addHeader("Set-Cookie", setCookieHeader);
    }
    private void addRefreshTokenCookie(HttpServletResponse response,
                                       HttpServletRequest request,
                                       String token) {
        boolean isLocal = isLocalRequest(request);
        String setCookieHeader = isLocal
                // 로컬: Secure 없이, SameSite=Lax (HTTP에서도 전송됨)
                ? String.format("RefreshToken=%s; Max-Age=%d; Path=/; HttpOnly; SameSite=Lax",
                token, (int) (REFRESH_EXP_MS / 1000L))
                // 운영: 교차사이트 위해 None + Secure
                : String.format("RefreshToken=%s; Max-Age=%d; Path=/; HttpOnly; Secure; SameSite=None",
                token, (int) (REFRESH_EXP_MS / 1000L));

        response.addHeader("Set-Cookie", setCookieHeader);
    }

    private boolean isLocalRequest(HttpServletRequest request) {
        String host = request.getServerName();
        return "localhost".equalsIgnoreCase(host) || host.startsWith("127.0.0.1");
    }

    private String buildRtKey(Long userId, String refreshToken) {
        return "rt:" + userId + ":" + Integer.toHexString(refreshToken.hashCode());
    }
}
