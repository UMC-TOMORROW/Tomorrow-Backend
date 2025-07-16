package com.umc.tomorrow.domain.auth.security;

import com.umc.tomorrow.domain.auth.jwt.JWTUtil;
import com.umc.tomorrow.domain.member.entity.User;
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

        String username = customUserDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        User user = userRepository.findByUsername(username);
        if (user == null) {
            // 소셜 로그인 정보로 새 회원 생성
            user = new User();
            user.setName(customUserDetails.getName()); // 필요시
            userRepository.save(user);
        }
        String token = jwtUtil.createJwt(
            user.getId(),
            user.getName(),
            60*60*60L
        );

        // Refresh Token 생성 (2주)
        String refreshToken = jwtUtil.createRefreshToken(username, 60L * 60 * 24 * 14 * 1000); // 2주

        // DB에 저장
        if (user != null) {
            user.setRefreshToken(refreshToken);
            userRepository.save(user);
        }

        // 클라이언트에 전달 (쿠키/헤더)
        response.addCookie(createCookie("Authorization", token));
        response.addHeader("Authorization", "Bearer " + token);
        response.addCookie(createCookie("RefreshToken", refreshToken));
        response.addHeader("RefreshToken", refreshToken);
        //response.sendRedirect("http://localhost:3000/");
        //response.sendRedirect("/success");//로컬 테스트 확인용
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60*60*60);
        //cookie.setSecure(true);
        cookie.setSecure(false); // 로컬 테스트용
        cookie.setPath("/");
        cookie.setHttpOnly(true);



        return cookie;
    }
}