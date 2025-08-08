/**
 * 채팅 메시지를 구독 중인 사용자들에게 비동기적으로 브로드캐스팅하는 서비스 클래스
 * @Async 어노테이션을 통해 메시지 전송 작업을 별도의 스레드에서 비동기 처리하여
 *  *   응답 속도 향상 및 트래픽 분산 효과를 기대
 *  작성자: 이승주
 *  작성일: 2025-08-06
 */
package com.umc.tomorrow.domain.chatting.service.command;

import com.umc.tomorrow.domain.chatting.dto.response.CreateChatMessageResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatBroadcastService {

    private final SimpMessagingTemplate messagingTemplate;

    @Async("chatExecutor")
    public void broadcast(Long chattingRoomId, CreateChatMessageResponseDTO response) {
        messagingTemplate.convertAndSend(
                "/sub/chat/" + chattingRoomId,
                response
        );
    }
}
