package com.umc.tomorrow.domain.auth.jwt;

import com.umc.tomorrow.domain.auth.security.CustomOAuth2User;
import com.umc.tomorrow.domain.member.dto.UserDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        System.out.println("JWTFilter: Request URI = " + path); // 요청 URI 로그 추가

        // /login, /oauth2 경로는 필터를 건너뜀
        if (path.startsWith("/login") || path.startsWith("/oauth2")) {
            System.out.println("JWTFilter: Skipping filter for path: " + path);
            filterChain.doFilter(request, response);
            return;
        }

        String authorization = null;
        Cookie[] cookies = request.getCookies();

        // 쿠키에서 Authorization 토큰 찾기
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                // System.out.println("JWTFilter: Cookie found - " + cookie.getName()); // 모든 쿠키 이름 로깅 (필요 시 주석 해제)
                if (cookie.getName().equals("Authorization")) {
                    authorization = cookie.getValue();
                    System.out.println("JWTFilter: Authorization cookie found.");
                    break; // 찾았으면 더 이상 반복할 필요 없음
                }
            }
        }

        // 쿠키에 없으면 Authorization 헤더에서 찾기
        if (authorization == null) {
            String headerAuth = request.getHeader("Authorization");
            if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
                authorization = headerAuth.substring(7);
                System.out.println("JWTFilter: Authorization header (Bearer) found.");
            }
        }

        // Authorization 토큰이 없는 경우
        if (authorization == null) {
            System.out.println("JWTFilter: Token not found in cookies or Authorization header. Proceeding without authentication.");
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization;
        System.out.println("JWTFilter: Token received (first few chars): " + token.substring(0, Math.min(token.length(), 20)) + "..."); // 토큰 일부 로깅

        try {
            // 토큰 소멸 시간 검증
            if (jwtUtil.isExpired(token)) {
                System.out.println("JWTFilter: Token is EXPIRED.");
                // 토큰이 만료되었지만, 스프링 시큐리티의 다른 필터에서 401 Unauthorized를 처리할 수 있도록 filterChain을 계속 진행
                filterChain.doFilter(request, response);
                return;
            }

            // 토큰에서 정보 획득
            String username = jwtUtil.getUsername(token);
            String role = jwtUtil.getRole(token);
            Long id = null;
            String name = null;

            try {
                id = jwtUtil.getId(token);
            } catch (Exception e) {
                System.out.println("JWTFilter: Could not get ID from token (might be for refresh token or specific payload format): " + e.getMessage());
            }
            try {
                name = jwtUtil.getName(token);
            } catch (Exception e) {
                System.out.println("JWTFilter: Could not get Name from token (might be for refresh token or specific payload format): " + e.getMessage());
            }

            System.out.println("JWTFilter: Token Payload - Username: " + username + ", Role: " + role + ", ID: " + id + ", Name: " + name);

            // UserDTO 생성 및 값 설정
            UserDTO userDTO = new UserDTO();
            userDTO.setId(id);
            userDTO.setUsername(username);
            userDTO.setRole(role);
            userDTO.setName(name);

            // UserDetails에 회원 정보 객체 담기
            CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDTO);

            // 스프링 시큐리티 인증 토큰 생성
            Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());
            // 세션에 사용자 등록 (SecurityContextHolder에 Authentication 객체 설정)
            SecurityContextHolder.getContext().setAuthentication(authToken);
            System.out.println("JWTFilter: Authentication successful for user: " + username);

        } catch (io.jsonwebtoken.security.SignatureException e) {
            System.out.println("JWTFilter: Token parsing failed - Invalid Signature: " + e.getMessage());
            // 서명 검증 실패 시, 이후 필터에서 401 처리될 수 있도록 진행
            filterChain.doFilter(request, response);
            return;
        } catch (Exception e) {
            System.out.println("JWTFilter: Token parsing failed - General Error: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            // 기타 토큰 파싱 실패 시, 이후 필터에서 401 처리될 수 있도록 진행
            filterChain.doFilter(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }
}