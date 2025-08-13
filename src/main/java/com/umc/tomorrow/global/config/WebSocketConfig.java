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

                // 핸드셰이크 인터셉터(AuthHandshakeInterceptor)가 세션에 넣어둔 user를 복원
                if (acc.getSessionAttributes() != null) {
                    Object u = acc.getSessionAttributes().get("user");

                    // Handshake에서 Authentication(=Principal)로 넣었으면 그대로 사용
                    if (u instanceof Authentication auth) {
                        acc.setUser(auth);

                        // 혹시 Principal로만 들어온 경우도 커버
                    } else if (u instanceof Principal p) {
                        acc.setUser(p);

                        // CustomOAuth2User 형태로 들어온 경우 권한 없이 래핑
                    } else if (u instanceof CustomOAuth2User cou) {
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
