package com.umc.tomorrow.global.config;

import com.umc.tomorrow.domain.auth.jwt.JWTUtil;
import com.umc.tomorrow.domain.auth.security.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.security.Principal;
import java.util.Collections;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JWTUtil jwtUtil; // 현재 직접 쓰진 않지만 생성자 주입 유지

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/sub");
        registry.setApplicationDestinationPrefixes("/pub");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/wss")
                .addInterceptors(new AuthHandshakeInterceptor(jwtUtil))
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor acc = StompHeaderAccessor.wrap(message);

                // 기본 프레임 정보 로깅
                String cmd = acc.getCommand() != null ? acc.getCommand().name() : "UNKNOWN";
                String sid = acc.getSessionId();
                Principal beforeUser = acc.getUser();
                log.info("[WS-IN] cmd={}, sessionId={}, beforeUser={}({})",
                        cmd, sid,
                        beforeUser != null ? beforeUser.getName() : "null",
                        beforeUser != null ? beforeUser.getClass().getSimpleName() : "null"
                );

                // 1) Handshake에서 세션에 넣어둔 user 확인
                Object u = (acc.getSessionAttributes() != null)
                        ? acc.getSessionAttributes().get("user")
                        : null;

                if (u == null) {
                    log.warn("[WS-IN] sessionId={} no 'user' attribute in sessionAttributes", sid);
                } else {
                    log.info("[WS-IN] sessionId={} session.user type={}", sid, u.getClass().getName());

                    if (u instanceof Authentication auth) {
                        // 이미 Authentication이면 그대로 사용
                        log.info("[WS-IN] sessionId={} using Authentication(name={}) from session", sid, auth.getName());
                        acc.setUser(auth);

                    } else if (u instanceof CustomOAuth2User cou) {
                        // CustomOAuth2User → userId로 정규화
                        Long userId = cou.getUserDTO().getId();
                        log.info("[WS-IN] sessionId={} found CustomOAuth2User(name={}, id={}), normalizing to userId",
                                sid, cou.getUserDTO().getName(), userId);

                        Authentication normalized = new UsernamePasswordAuthenticationToken(
                                String.valueOf(userId), null, Collections.emptyList()
                        );
                        acc.setUser(normalized);
                        log.info("[WS-IN] sessionId={} setUser -> name={}", sid, normalized.getName());
                    } else {
                        log.warn("[WS-IN] sessionId={} unexpected session.user type: {}", sid, u.getClass().getName());
                    }
                }

                // 최종 setUser 상태 출력
                Principal afterUser = acc.getUser();
                log.info("[WS-IN] cmd={}, sessionId={}, afterUser={}({})",
                        cmd, sid,
                        afterUser != null ? afterUser.getName() : "null",
                        afterUser != null ? afterUser.getClass().getSimpleName() : "null"
                );

                // (옵션) CONNECT 네이티브 헤더 Authorization 존재 여부만 참고용으로 찍기 (값은 노출 안 함)
                try {
                    var authz = acc.getFirstNativeHeader("Authorization");
                    if (authz != null) {
                        log.info("[WS-IN] sessionId={} CONNECT native 'Authorization' header present", sid);
                    }
                } catch (Exception ignore) { /* no-op */ }

                return message;
            }
        });
    }
}
