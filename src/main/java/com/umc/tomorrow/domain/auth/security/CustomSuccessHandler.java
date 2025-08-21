package com.umc.tomorrow.domain.auth.security;

import com.umc.tomorrow.domain.introduction.repository.IntroductionRepository;
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
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
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
    private final Environment env; // profile 확인용
    private final IntroductionRepository introductionRepository;

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
            introductionRepository.save(defaultIntroduction);
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

        boolean isLocal = request.getServerName().equals("localhost"); // 프론트 로컬 구분
        boolean isOnboarded = Boolean.TRUE.equals(user.getIsOnboarded());
        boolean secureFlag = !isLocal;

        // Swagger/Postman 요청 분기
        boolean isSwaggerRequest = (isLocalProfile());

        String accessCookie = String.format(
                "Authorization=%s; Max-Age=%d; Path=/; HttpOnly; Secure=%b; SameSite=None",
                accessToken, (int) accessTokenExpiredSeconds, secureFlag
        );
        response.addHeader("Set-Cookie", accessCookie);

        String refreshCookie = String.format(
                "RefreshToken=%s; Max-Age=%d; Path=/; HttpOnly; Secure=%b; SameSite=None",
                refreshToken, (int) refreshTokenExpiredSeconds, secureFlag
        );
        response.addHeader("Set-Cookie", refreshCookie);

        // 헤더 추가
        response.addHeader("Authorization", "Bearer " + accessToken);
        response.addHeader("RefreshToken", refreshToken);

        // === 분기 처리 ===
        if (isSwaggerRequest) {
            // Swagger/Postman (로컬 테스트) → JSON 반환
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(
                    String.format(
                            "{\"accessToken\":\"%s\",\"refreshToken\":\"%s\",\"isOnboarded\":%b}",
                            accessToken, refreshToken, isOnboarded
                    )
            );
        } else {            // 프론트 (로컬/배포) → Redirect
            String redirectUrl = isLocal
                    ? (isOnboarded ? "http://localhost:5173" : "http://localhost:5173/onboarding")
                    : (isOnboarded ? "https://umctomorrow.shop" : "https://umctomorrow.shop/onboarding");

            response.sendRedirect(redirectUrl);
        }

    }

    private boolean isLocalProfile() {
        return Arrays.asList(env.getActiveProfiles()).contains("local");
    }
}
