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
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JWTUtil jwtUtil;

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

                //  1) 프론트가 STOMP CONNECT 헤더로 보낸 토큰 처리
                if (acc.getCommand() == StompCommand.CONNECT) {
                    String authz = acc.getFirstNativeHeader("Authorization");
                    if (authz == null) authz = acc.getFirstNativeHeader("authorization");
                    if (authz != null && authz.startsWith("Bearer ")) {
                        String token = authz.substring(7);

                        // 너희 유틸은 validateToken이 아니라 isExpired 사용
                        if (!jwtUtil.isExpired(token)) {
                            Long userId = jwtUtil.getUserId(token);

                            // name = userId 로 넣어두면 Principal.getName()으로 꺼내기 쉬움
                            Authentication auth = new UsernamePasswordAuthenticationToken(
                                    String.valueOf(userId), null, Collections.emptyList()
                            );

                            acc.setUser(auth); // Authentication은 Principal 구현 → OK
                            if (acc.getSessionAttributes() != null) {
                                acc.getSessionAttributes().put("user", auth); // Map<String,Object>라 OK
                            }
                        } else {
                            throw new RuntimeException("Invalid/Expired JWT");
                        }
                    }
                }

                // ✅ 2) Handshake 인터셉터가 세션에 올려둔 값도 함께 커버
                if (acc.getSessionAttributes() != null) {
                    Object u = acc.getSessionAttributes().get("user");
                    if (u instanceof Principal p) {
                        acc.setUser(p);
                    } else if (u instanceof CustomOAuth2User cou) {
                        // getAuthorities()가 없을 수도 있으니 빈 권한으로 래핑
                        Authentication auth = new UsernamePasswordAuthenticationToken(
                                cou, null, Collections.emptyList()
                        );
                        acc.setUser(auth);
                    }
                }

                return message;
            }
        });
    }
}
