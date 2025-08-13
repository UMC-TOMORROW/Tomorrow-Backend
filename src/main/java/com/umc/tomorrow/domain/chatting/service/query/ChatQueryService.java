/**
 * 채팅 관련 쿼리(조회) 서비스
 * 작성자: 이승주
 * 작성일: 2025-08-11
 */
package com.umc.tomorrow.domain.chatting.service.query;

import com.umc.tomorrow.domain.chatting.dto.response.GetChatMessageListResponseDTO;

public interface ChatQueryService {

    GetChatMessageListResponseDTO getMessages(Long chattingRoomId, Long cursor, int size);
}
