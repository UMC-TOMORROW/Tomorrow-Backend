package com.umc.tomorrow.global.config;

import com.umc.tomorrow.domain.auth.jwt.JWTUtil;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JWTUtil jwtUtil;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {   //STOMP 메시지 브로커 구성
        registry.enableSimpleBroker("/sub");
        registry.setApplicationDestinationPrefixes("/pub");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {   //웹소켓 연결시 https이므로 wss적용, Jwt토큰을 검사하는 인터셉터 추가
        registry.addEndpoint("/wss")
                .addInterceptors(new AuthHandshakeInterceptor(jwtUtil))
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) { //웹소켓 연결 후, 메시지를 보낼 때마다 실행
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
                Object user = accessor.getSessionAttributes().get("user");
                if (user instanceof Principal principal) {
                    accessor.setUser(principal);
                }
                return message;
            }
        });
    }
}
