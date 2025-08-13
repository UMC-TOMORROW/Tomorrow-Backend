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

                // Handshake에서 올려둔 Authentication 그대로 사용
                if (acc.getSessionAttributes() != null) {
                    Object u = acc.getSessionAttributes().get("user");

                    // (중요) Authentication(=Principal)인 경우만 setUser
                    if (u instanceof org.springframework.security.core.Authentication auth) {
                        acc.setUser(auth);
                    }
                    // 아래처럼 CustomOAuth2User를 다시 래핑하는 분기는 제거해야 함
                    // else if (u instanceof CustomOAuth2User cou) {
                    //    Authentication auth = new UsernamePasswordAuthenticationToken(
                    //            cou, null, Collections.emptyList()
                    //    );
                    //    acc.setUser(auth);
                    // }
                }

                return message;
            }
        });
    }
}
