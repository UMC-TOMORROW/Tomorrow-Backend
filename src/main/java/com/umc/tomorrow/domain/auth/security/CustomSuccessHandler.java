package com.umc.tomorrow.domain.auth.security;

import com.umc.tomorrow.domain.member.entity.User;
import com.umc.tomorrow.domain.member.enums.Provider;
import com.umc.tomorrow.domain.member.repository.UserRepository;
import com.umc.tomorrow.domain.resume.entity.Resume;
import com.umc.tomorrow.domain.resume.repository.ResumeRepository;
import com.umc.tomorrow.domain.introduction.entity.Introduction;
import com.umc.tomorrow.domain.auth.jwt.JWTUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();
        String usernameToUseForTokens;

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        String providerStr = customUserDetails.getUserResponseDTO().getProvider();
        String providerUserId = customUserDetails.getUserResponseDTO().getProviderUserId();
        Provider provider = Provider.valueOf(providerStr.toUpperCase());

        User user = userRepository.findByProviderAndProviderUserId(provider, providerUserId);

        if (user == null) {
            // 신규 사용자 생성
            user = new User();
            user.setName(customUserDetails.getName());
            user.setCreatedAt(java.time.LocalDateTime.now());
            user.setUpdatedAt(java.time.LocalDateTime.now());
            user.setProvider(provider);
            user.setProviderUserId(providerUserId);

            usernameToUseForTokens = providerStr + "_" + providerUserId;
            user.setUsername(usernameToUseForTokens);
            user.setEmail(customUserDetails.getUserResponseDTO().getEmail());

            user = userRepository.save(user);

            // 기본 이력서 + 자기소개
            Resume defaultResume = Resume.builder()
                    .user(user)
                    .build();
            Introduction defaultIntroduction = Introduction.builder()
                    .content("안녕하세요! 저는 " + user.getName() + "입니다.")
                    .resume(defaultResume)
                    .build();
            defaultResume.setIntroduction(defaultIntroduction);

            Resume savedResume = resumeRepository.save(defaultResume);
            user.setResumeId(savedResume.getId());
            userRepository.save(user);

        } else {
            usernameToUseForTokens = user.getUsername();
            if (usernameToUseForTokens == null) {
                usernameToUseForTokens = providerStr + "_" + providerUserId;
                user.setUsername(usernameToUseForTokens);
                userRepository.save(user);
            }

            if (user.getResumeId() == null) {
                Resume defaultResume = Resume.builder()
                        .user(user)
                        .build();
                Introduction defaultIntroduction = Introduction.builder()
                        .content("안녕하세요! 저는 " + user.getName() + "입니다.")
                        .resume(defaultResume)
                        .build();
                defaultResume.setIntroduction(defaultIntroduction);

                Resume savedResume = resumeRepository.save(defaultResume);
                user.setResumeId(savedResume.getId());
                userRepository.save(user);
            }
        }

        // Access Token (60시간)
        long accessTokenExpiredMs = 60L * 60 * 60 * 1000;
        long accessTokenExpiredSeconds = 60L * 60 * 60;

        String accessToken = jwtUtil.createJwt(
                user.getId(),
                user.getName(),
                user.getUsername(),
                (user.getStatus() != null ? user.getStatus().name() : null),
                accessTokenExpiredMs
        );

        // Refresh Token (2주)
        long refreshTokenExpiredMs = 60L * 60 * 24 * 14 * 1000;
        long refreshTokenExpiredSeconds = 60L * 60 * 24 * 14;

        String refreshToken = jwtUtil.createRefreshToken(user.getId(), usernameToUseForTokens, refreshTokenExpiredMs);

        // DB 저장
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        // 환경 감지
        String origin = request.getHeader("Origin");
        String referer = request.getHeader("Referer");
        boolean isLocalSwagger = (origin == null && referer == null); // Swagger/Postman 요청
        boolean isLocalFrontend = (origin != null && origin.contains("localhost:5173"));
        boolean isProdFrontend = (origin != null && origin.contains("umctomorrow.shop"));

        boolean isOnboarded = Boolean.TRUE.equals(user.getIsOnboarded());

        // Refresh Token은 HttpOnly 쿠키로 저장
        StringBuilder refreshCookieBuilder = new StringBuilder();
        refreshCookieBuilder.append("refreshToken=").append(refreshToken)
                .append("; Max-Age=").append((int) refreshTokenExpiredSeconds)
                .append("; Path=/; HttpOnly; SameSite=None; Secure"); // 배포 대비 Secure 고정
        response.addHeader("Set-Cookie", refreshCookieBuilder.toString().trim());

        // Access Token은 헤더로 내려줌
        response.addHeader("Authorization", "Bearer " + accessToken);

        // 분기 처리
        if (isLocalSwagger) {
            // Swagger/Postman → JSON 응답
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(
                    String.format(
                            "{\"accessToken\":\"%s\",\"refreshToken\":\"%s\",\"isOnboarded\":%b}",
                            accessToken, refreshToken, isOnboarded
                    )
            );
        } else if (isLocalFrontend) {
            // 로컬 프론트 (5173)
            String redirectUrl = isOnboarded
                    ? "http://localhost:5173"
                    : "http://localhost:5173/onboarding";
            response.sendRedirect(redirectUrl);
        } else if (isProdFrontend) {
            // 배포 프론트
            String redirectUrl = isOnboarded
                    ? "https://umctomorrow.shop"
                    : "https://umctomorrow.shop/onboarding";
            response.sendRedirect(redirectUrl);
        } else {
            //혹시 모르는 케이스 -> SON
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(
                    String.format(
                            "{\"accessToken\":\"%s\",\"refreshToken\":\"%s\",\"isOnboarded\":%b}",
                            accessToken, refreshToken, isOnboarded
                    )
            );
        }
    }
}
