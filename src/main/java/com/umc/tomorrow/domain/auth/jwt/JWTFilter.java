package com.umc.tomorrow.domain.auth.jwt;

import com.umc.tomorrow.domain.auth.security.CustomOAuth2User;
import com.umc.tomorrow.domain.member.dto.UserResponseDTO;
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
import java.time.LocalDateTime;

public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        System.out.println("JWTFilter: Request URI = " + path); // 요청 URI 로그 추가

        if (path.startsWith("/login") || path.startsWith("/oauth2") || path.startsWith("/swagger-ui") ||
                        path.startsWith("/v3/api-docs")) {
            System.out.println("JWTFilter: Skipping filter for path: " + path);
            filterChain.doFilter(request, response);
            return;
        }

        String authorization = null;
        Cookie[] cookies = request.getCookies();

        // 쿠키에서 Authorization 토큰 찾기
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("Authorization")) {
                    authorization = cookie.getValue();
                    System.out.println("JWTFilter: Authorization cookie found.");
                    break;
                }
            }
        }

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
            if (jwtUtil.isExpired(token)) {
                System.out.println("JWTFilter: Token is EXPIRED.");
                filterChain.doFilter(request, response);
                return;
            }

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

            // UserDTO 생성 및 값 설정
            UserResponseDTO userDTO = new UserResponseDTO(
                id,
                role,
                username,
                null,
                name,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
            );


            CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDTO);
            Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);
        } catch (io.jsonwebtoken.security.SignatureException e) {
            System.out.println("JWTFilter: Token parsing failed - Invalid Signature: " + e.getMessage());
            filterChain.doFilter(request, response);
            return;
        } catch (Exception e) {
            System.out.println("JWTFilter: Token parsing failed - General Error: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
