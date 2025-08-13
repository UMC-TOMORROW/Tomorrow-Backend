package com.umc.tomorrow.global.config;

import com.umc.tomorrow.domain.auth.jwt.JWTUtil;
import com.umc.tomorrow.domain.auth.security.CustomOAuth2User;
import java.security.Principal;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
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

/**
 * - /wss 엔드포인트(SockJS) + 핸드셰이크 인터셉터(쿠키 인증)
 * - 인바운드 채널에서 세션의 user를 setUser로 복원 + name을 userId로 “정규화”
 * - 브로커: pub/sub 프리픽스
 */
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JWTUtil jwtUtil; // (현재 파일에서는 직접 사용하진 않지만, 생성자 주입 유지)

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/sub");        // 구독 prefix
        registry.setApplicationDestinationPrefixes("/pub"); // 발행 prefix
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

                // 1) Handshake 인터셉터가 세션에 넣어둔 user 복원
                if (acc.getSessionAttributes() != null) {
                    Object u = acc.getSessionAttributes().get("user");

                    if (u instanceof org.springframework.security.core.Authentication auth) {
                        // 이미 Authentication이면 그대로 사용
                        acc.setUser(auth);

                    } else if (u instanceof com.umc.tomorrow.domain.auth.security.CustomOAuth2User cou) {
                        // ✅ CustomOAuth2User → userId 추출해 name=userid 로 정규화
                        Long userId = cou.getUserDTO().getId();
                        org.springframework.security.core.Authentication auth2 =
                                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                                        String.valueOf(userId), null, java.util.Collections.emptyList()
                                );
                        acc.setUser(auth2);
                    }
                }

                // (옵션) CONNECT 헤더 Authorization 처리도 유지하고 싶으면 여기서 토큰 파싱해 acc.setUser(...) 추가

                return message;
            }
        });
    }
}