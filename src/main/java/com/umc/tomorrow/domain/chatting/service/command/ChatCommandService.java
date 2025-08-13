/**
 * 채팅방 관련 command 서비스
 * 작성자: 이승주
 * 작성일: 2025-08-11
 */
package com.umc.tomorrow.domain.chatting.service.command;

import com.umc.tomorrow.domain.chatting.dto.request.CreateChatMessageRequestDTO;
import com.umc.tomorrow.domain.chatting.entity.Message;

public interface ChatCommandService {

    Message saveMessage(CreateChatMessageRequestDTO request, Long userId);

    void joinChatRoom(Long chattingRoomId, Long userId);
}
