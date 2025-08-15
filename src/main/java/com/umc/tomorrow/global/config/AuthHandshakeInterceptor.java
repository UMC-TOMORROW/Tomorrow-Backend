package com.umc.tomorrow.global.config;

import com.umc.tomorrow.domain.auth.jwt.JWTUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

public class AuthHandshakeInterceptor implements HandshakeInterceptor {

    private final JWTUtil jwtUtil;

    private static final List<String> TOKEN_COOKIE_CANDIDATES = List.of("Authorization");

    public AuthHandshakeInterceptor(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {

        String token = null;

        // 1) HttpOnly 쿠키에서 찾기
        if (request instanceof ServletServerHttpRequest sreq) {
            HttpServletRequest http = sreq.getServletRequest();
            Cookie[] cookies = http.getCookies();
            if (cookies != null) {
                for (Cookie c : cookies) {
                    if (TOKEN_COOKIE_CANDIDATES.contains(c.getName())) {
                        token = c.getValue();
                        break;
                    }
                }
            }
        }

        // 2) (옵션) Authorization 헤더도 허용
        if (token == null) {
            String authz = request.getHeaders().getFirst("Authorization");
            if (authz != null && authz.startsWith("Bearer ")) {
                token = authz.substring(7);
            }
        }

        // 3) 토큰 검증 후 세션에 인증 정보 저장 (name = userId)
        if (token != null) {
            try {
                if (!jwtUtil.isExpired(token)) {
                    Long userId = jwtUtil.getUserId(token);

                    Authentication auth = new UsernamePasswordAuthenticationToken(
                            String.valueOf(userId),  // Principal name을 userId 문자열로 고정
                            null,
                            List.of()                // 권한 비워도 무방
                    );
                    attributes.put("user", auth);
                }
            } catch (Exception ignored) {
                // 토큰 파싱/검증 실패 시 WS 연결 자체는 막지 않음 (필요하면 false 반환으로 차단 가능)
            }
        }

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
        // no-op
    }
}
