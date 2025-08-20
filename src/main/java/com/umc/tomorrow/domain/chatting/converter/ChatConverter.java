/**
 * ChatConverter
 * Message Entity <-> DTO 변환 및 매핑
 * 작성자: 이승주
 * 생성일: 2025-08-06
 */
package com.umc.tomorrow.domain.chatting.converter;

import com.umc.tomorrow.domain.chatting.dto.response.GetChatMessageResponseDTO;
import com.umc.tomorrow.domain.chatting.entity.Message;

public class ChatConverter {

    /**
     * Message 엔티티 → GetChatMessageResponseDTO 변환
     */
    public static GetChatMessageResponseDTO toGetChatMessageResponseDTO(Message message,Long currentUserId) {

        boolean isMessageMine = false;
        if (message.getUser() != null) {
            isMessageMine = message.getUser().getId().equals(currentUserId);
        }

        return GetChatMessageResponseDTO.builder()
                .messageId(message.getId())
                .anonymousName(message.getChatPart().getAnonymousName())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .isMine(isMessageMine)
                .build();
    }
}

