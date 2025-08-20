/**
 * 채팅방 조회 서비스 클래스
 * 작성자: 이승주
 * 작성일: 2025-08-06
 */
package com.umc.tomorrow.domain.chatting.service.query;

import com.umc.tomorrow.domain.chatting.converter.ChatConverter;
import com.umc.tomorrow.domain.chatting.dto.response.GetChatMessageListResponseDTO;
import com.umc.tomorrow.domain.chatting.dto.response.GetChatMessageResponseDTO;
import com.umc.tomorrow.domain.chatting.entity.ChattingRoom;
import com.umc.tomorrow.domain.chatting.entity.Message;
import com.umc.tomorrow.domain.chatting.exception.ChatException;
import com.umc.tomorrow.domain.chatting.exception.code.ChatErrorStatus;
import com.umc.tomorrow.domain.chatting.repository.ChattingRoomRepository;
import com.umc.tomorrow.domain.chatting.repository.MessageRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatQueryService {

    private final MessageRepository messageRepository;
    private final ChattingRoomRepository chattingRoomRepository;

    public GetChatMessageListResponseDTO getMessages(Long userId,Long chattingRoomId, Long cursor, int size) {

        ChattingRoom room = chattingRoomRepository.findById(chattingRoomId)
                .orElseThrow(() -> new ChatException(ChatErrorStatus.CHATROOM_NOT_FOUND));

        Pageable pageable = PageRequest.of(0, size);
        Slice<Message> messageSlice;

        if (cursor == null) {
            // 첫 요청: 최신 메시지부터
            messageSlice = messageRepository.findByChattingRoomIdOrderByIdDesc(chattingRoomId, pageable);
        } else {
            // 이후 요청: cursor 이전 메시지
            messageSlice = messageRepository.findByChattingRoomIdAndIdLessThanOrderByIdDesc(chattingRoomId, cursor,
                    pageable);
        }

        List<GetChatMessageResponseDTO> messages = messageSlice.getContent().stream()
                .map(message -> ChatConverter.toGetChatMessageResponseDTO(message, userId))
                .toList();

        return GetChatMessageListResponseDTO.builder()
                .chatMessageList(messages)
                .hasNext(messageSlice.hasNext())
                .build();

    }
}
