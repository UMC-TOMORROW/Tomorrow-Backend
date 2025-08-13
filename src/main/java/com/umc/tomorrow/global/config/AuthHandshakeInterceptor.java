package com.umc.tomorrow.global.config;

import com.umc.tomorrow.domain.auth.jwt.JWTUtil;
import java.util.Map;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

// AuthHandshakeInterceptor.java
public class AuthHandshakeInterceptor implements HandshakeInterceptor {

    private final JWTUtil jwtUtil;

    public AuthHandshakeInterceptor(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {

        String token = null;

        // 1) HttpOnly 쿠키에서 찾기 (쿠키명: Authorization)
        if (request instanceof org.springframework.http.server.ServletServerHttpRequest sreq) {
            var http = sreq.getServletRequest();
            var cookies = http.getCookies();
            if (cookies != null) {
                for (var c : cookies) {
                    if ("Authorization".equals(c.getName())) {   // ← 쿠키명
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

        // 3) 토큰 검증 후 세션에 인증 정보 저장 (name=userId)
        if (token != null && !jwtUtil.isExpired(token)) {
            Long userId = jwtUtil.getUserId(token);
            var auth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                    String.valueOf(userId), null, java.util.List.of()
            );
            attributes.put("user", auth);
        }

        return true;
    }

    @Override public void afterHandshake(ServerHttpRequest r, ServerHttpResponse s, WebSocketHandler w, Exception e) {}
}
