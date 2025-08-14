package com.umc.tomorrow.domain.auth.security;

import com.umc.tomorrow.domain.auth.jwt.JWTUtil;
import com.umc.tomorrow.domain.member.entity.User;
import com.umc.tomorrow.domain.member.enums.Provider;
import com.umc.tomorrow.domain.member.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    @Autowired
    public CustomSuccessHandler(JWTUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();
        String usernameToUseForTokens;

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        String providerStr = customUserDetails.getUserDTO().getProvider();
        String providerUserId = customUserDetails.getUserDTO().getProviderUserId();
        Provider provider = Provider.valueOf(providerStr.toUpperCase());

        User user = userRepository.findByProviderAndProviderUserId(provider, providerUserId);
        if (user == null) {
            user = new User();
            user.setName(customUserDetails.getName());
            user.setCreatedAt(java.time.LocalDateTime.now());
            user.setUpdatedAt(java.time.LocalDateTime.now());
            user.setProvider(provider);
            user.setProviderUserId(providerUserId);

            usernameToUseForTokens = (providerStr != null && providerUserId != null)
                    ? providerStr + "_" + providerUserId : null;
            user.setUsername(usernameToUseForTokens);
            user.setEmail(customUserDetails.getUserDTO().getEmail());
            userRepository.save(user);
        } else {
            usernameToUseForTokens = user.getUsername();
            if (usernameToUseForTokens == null) {
                usernameToUseForTokens = (providerStr != null && providerUserId != null)
                        ? providerStr + "_" + providerUserId : null;
                user.setUsername(usernameToUseForTokens);
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

        // DB에 저장
        user.setRefreshToken(refreshToken);
        System.out.println("[DEBUG] refresh Token 확인 : " + refreshToken);
        userRepository.save(user);

        // 환경 감지
        boolean isLocal = request.getServerName().equals("localhost");
        boolean secureFlag = !isLocal;

        // 쿠키 설정
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

        // 리다이렉트 URL
        String redirectUrl = isLocal
                ? "http://localhost:5173/onboarding"
                : "https://umctomorrow.shop/onboarding";

        response.sendRedirect(redirectUrl);
    }

    private Cookie createCookie(String key, String value, int maxAge, boolean secureFlag) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(maxAge);
        cookie.setSecure(secureFlag);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        return cookie;
    }
}
