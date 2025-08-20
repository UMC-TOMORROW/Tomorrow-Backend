/**
 * 채팅방 참여 생성 및 메시지 저장 서비스 클래스
 * 작성자: 이승주
 * 작성일: 2025-08-06
 */
package com.umc.tomorrow.domain.chatting.service.command;

import com.umc.tomorrow.domain.chatting.dto.request.CreateChatMessageRequestDTO;
import com.umc.tomorrow.domain.chatting.entity.ChatPart;
import com.umc.tomorrow.domain.chatting.entity.ChattingRoom;
import com.umc.tomorrow.domain.chatting.entity.Message;
import com.umc.tomorrow.domain.chatting.exception.ChatException;
import com.umc.tomorrow.domain.chatting.exception.code.ChatErrorStatus;
import com.umc.tomorrow.domain.chatting.repository.ChatPartRepository;
import com.umc.tomorrow.domain.chatting.repository.ChattingRoomRepository;
import com.umc.tomorrow.domain.chatting.repository.MessageRepository;
import com.umc.tomorrow.domain.member.entity.User;
import com.umc.tomorrow.domain.member.repository.UserRepository;
import com.umc.tomorrow.global.common.exception.RestApiException;
import com.umc.tomorrow.global.common.exception.code.GlobalErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ChatCommandService {

    private final ChattingRoomRepository chattingRoomRepository;
    private final ChatPartRepository chatPartRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    /*
     * 메시지 저장만 담당
     */
    public Message saveMessage(CreateChatMessageRequestDTO request, Long userId) {
        ChattingRoom room = chattingRoomRepository.findById(request.getChattingRoomId())
                .orElseThrow(() -> new ChatException(ChatErrorStatus.CHATROOM_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestApiException(GlobalErrorStatus._NOT_FOUND));

        ChatPart chatPart = chatPartRepository.findByUserIdAndChattingRoomId(user.getId(), room.getId())
                .orElseThrow(() -> new ChatException(ChatErrorStatus.CHATPART_NOT_FOUND));


        Message saved = messageRepository.save(Message.builder()
                .user(user)
                .chatPart(chatPart)
                .chattingRoom(room)
                .content(request.getContent())
                .build());


        log.info("[CHAT] saved messageId={} userId={}", saved.getId(), userId);

        return saved;
    }

    /*
     * 유저의 채팅방 참여
     */
    public void joinChatRoom(Long chattingRoomId, Long userId) {

        ChattingRoom room = chattingRoomRepository.findById(chattingRoomId)
                .orElseThrow(() -> new ChatException(ChatErrorStatus.CHATROOM_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestApiException(GlobalErrorStatus._NOT_FOUND));

        boolean alreadyJoined = chatPartRepository.existsByUserAndChattingRoom(user, room);

        if (!alreadyJoined) {
            String anonymousName = generateAnonymousName(room,user);

            boolean duplicated = chatPartRepository.existsByChattingRoomAndAnonymousName(room, anonymousName);
            if (duplicated) {
                log.error("[CHAT]  Duplicate anonymousName detected! roomId={} name={}",
                        room.getId(), anonymousName);
            }


            ChatPart chatPart = ChatPart.builder()
                    .user(user)
                    .chattingRoom(room)
                    .anonymousName(anonymousName)
                    .build();

            chatPartRepository.save(chatPart);
        }
    }

    private String generateAnonymousName(ChattingRoom room, User user) {
        // 커리어톡 작성자 여부 확인
        if (room.getCareertalk().getUser().getId().equals(user.getId())) {
            return "작성자";
        }

        // 기존 익명 처리
        int index = chatPartRepository.countByChattingRoom(room) + 1;
        return "익명" + index;
    }
}
