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

        //OAuth2User
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();
        String usernameToUseForTokens;
        // String username = customUserDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        String providerStr = customUserDetails.getUserDTO().getProvider();
        String providerUserId = customUserDetails.getUserDTO().getProviderUserId();
        Provider provider = Provider.valueOf(providerStr.toUpperCase());

        User user = userRepository.findByProviderAndProviderUserId(provider, providerUserId);
        if (user == null) {
            // 소셜 로그인 정보로 새 회원 생성
            user = new User();
            user.setName(customUserDetails.getName());
            // 필수값 세팅
            user.setCreatedAt(java.time.LocalDateTime.now());
            user.setUpdatedAt(java.time.LocalDateTime.now());
            // provider, providerUserId, username, email 세팅
            user.setProvider(Provider.valueOf(providerStr.toUpperCase()));
            user.setProviderUserId(providerUserId);

            // DB에 저장할 username 형식과 토큰에 담을 username 형식을 일치시킴
            usernameToUseForTokens = (providerStr != null && providerUserId != null) ? providerStr + "_" + providerUserId : null;
            user.setUsername(usernameToUseForTokens); // DB에 저장
            user.setEmail(customUserDetails.getUserDTO().getEmail());
            userRepository.save(user);
        }else {
            // 기존 사용자일 경우 db에 저장된 username 사용
            usernameToUseForTokens = user.getUsername();
            // null 체크 추가 (혹시 기존 유저의 username이 null일 경우 대비)
            if (usernameToUseForTokens == null) {
                // DB에 username이 없었던 경우, 새로 생성하여 업데이트
                usernameToUseForTokens = (providerStr != null && providerUserId != null) ? providerStr + "_" + providerUserId : null;
                user.setUsername(usernameToUseForTokens);
                userRepository.save(user);
            }
        }

        // Access Token 유효기간 (60시간)
        long accessTokenExpiredMs = 60L * 60 * 60 * 1000; // 밀리초
        long accessTokenExpiredSeconds = 60L * 60 * 60; // 초

        // access token 생
        String accessToken = jwtUtil.createJwt(
            user.getId(),
            user.getName(),
            user.getUsername(),
            (user.getStatus() != null ? user.getStatus().name() : null),
                accessTokenExpiredMs
        );

        // Refresh Token 유효기간 (2주)
        long refreshTokenExpiredMs = 60L * 60 * 24 * 14 * 1000; // 밀리초
        long refreshTokenExpiredSeconds = 60L * 60 * 24 * 14; // 초

        // Refresh Token 생성 (2주)

        String refreshToken = jwtUtil.createRefreshToken(user.getId(), usernameToUseForTokens, refreshTokenExpiredMs); // 2주


        // DB에 저장
        if (user != null) {
            user.setRefreshToken(refreshToken);
            System.out.println("[DEBUG] refresh Token 확인 : " + refreshToken);
            userRepository.save(user);
        }


        // 쿠키 직접 문자열로 세팅 (로컬 테스트용 - Secure=false)
        String accessCookie = String.format("Authorization=%s; Max-Age=%d; Path=/; HttpOnly; Secure=false; SameSite=None",
                accessToken, (int) accessTokenExpiredSeconds);
        response.addHeader("Set-Cookie", accessCookie);

        String refreshCookie = String.format("RefreshToken=%s; Max-Age=%d; Path=/; HttpOnly; Secure=false; SameSite=None",
                refreshToken, (int) refreshTokenExpiredSeconds);
        response.addHeader("Set-Cookie", refreshCookie);

        //헤더 전달
        response.addHeader("Authorization", "Bearer " + accessToken);
        response.addHeader("RefreshToken", refreshToken);

         // 프론트로 리다이렉트
        response.sendRedirect("http://localhost:5173/onboarding");


//        response.addCookie(createCookie("Authorization", accessToken, (int)accessTokenExpiredSeconds));
//        response.addHeader("Authorization", "Bearer " + accessToken);
//
//        response.addCookie(createCookie("RefreshToken", refreshToken, (int)refreshTokenExpiredSeconds));
//        response.addHeader("RefreshToken", refreshToken);
//        response.sendRedirect("https://umctomorrow.shop/onboarding");
//        //response.sendRedirect("/success");//로컬 테스트 확인용
    }

    private Cookie createCookie(String key, String value, int maxAge) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(maxAge);
        //cookie.setSecure(true);
        cookie.setSecure(false); // 로컬 테스트용
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        return cookie;
    }
}
