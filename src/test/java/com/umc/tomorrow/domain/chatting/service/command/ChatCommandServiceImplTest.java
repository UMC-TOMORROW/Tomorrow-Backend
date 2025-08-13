package com.umc.tomorrow.domain.chatting.service.command;

import com.umc.tomorrow.domain.chatting.dto.request.CreateChatMessageRequestDTO;
import com.umc.tomorrow.domain.chatting.entity.ChatPart;
import com.umc.tomorrow.domain.chatting.entity.ChattingRoom;
import com.umc.tomorrow.domain.chatting.entity.Message;
import com.umc.tomorrow.domain.careertalk.entity.Careertalk;
import com.umc.tomorrow.domain.chatting.exception.ChatException;
import com.umc.tomorrow.domain.chatting.repository.ChatPartRepository;
import com.umc.tomorrow.domain.chatting.repository.ChattingRoomRepository;
import com.umc.tomorrow.domain.chatting.repository.MessageRepository;
import com.umc.tomorrow.domain.member.entity.User;
import com.umc.tomorrow.domain.member.repository.UserRepository;
import com.umc.tomorrow.global.common.exception.RestApiException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("ChatCommandServiceImpl 단위 테스트")
@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class ChatCommandServiceImplTest {

    @InjectMocks
    private ChatCommandServiceImpl service;

    @Mock private ChattingRoomRepository chattingRoomRepository;
    @Mock private ChatPartRepository chatPartRepository;
    @Mock private UserRepository userRepository;
    @Mock private MessageRepository messageRepository;

    private final Long roomId = 10L;
    private final Long userId = 1L;

    // ---------- saveMessage ----------
    @Test
    @DisplayName("메시지 저장 성공: 방/유저/참여자 검증 통과 후 저장")
    void saveMessage_success() {
        // given
        User user = User.builder().id(userId).build();

        ChattingRoom room = ChattingRoom.builder()
                .id(roomId)
                .build();

        ChatPart part = ChatPart.builder()
                .id(99L)
                .user(user)
                .chattingRoom(room)
                .anonymousName("익명1")
                .build();

        CreateChatMessageRequestDTO req = mock(CreateChatMessageRequestDTO.class);
        when(req.getChattingRoomId()).thenReturn(roomId);
        when(req.getContent()).thenReturn("안녕");
        LocalDateTime now = LocalDateTime.now();
        when(req.getSentAt()).thenReturn(now);

        when(chattingRoomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(chatPartRepository.findByUserIdAndChattingRoomId(userId, roomId)).thenReturn(Optional.of(part));

        // save 시 전달된 Message를 그대로 리턴
        when(messageRepository.save(any(Message.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        Message saved = service.saveMessage(req, userId);

        // then
        assertNotNull(saved);
        assertEquals("안녕", saved.getContent());
        assertSame(room, saved.getChattingRoom());
        assertSame(part, saved.getChatPart());

        ArgumentCaptor<Message> msgCaptor = ArgumentCaptor.forClass(Message.class);
        verify(messageRepository).save(msgCaptor.capture());
        Message toSave = msgCaptor.getValue();
        assertEquals("안녕", toSave.getContent());
        assertSame(room, toSave.getChattingRoom());
        assertSame(part, toSave.getChatPart());
        // createdAt은 req.getSentAt() 그대로 들어감(타입은 DTO 정의에 따름)
        assertEquals(now, toSave.getCreatedAt());
    }

    @Test
    @DisplayName("메시지 저장 실패: 채팅방 없음 → ChatException")
    void saveMessage_roomNotFound() {
        CreateChatMessageRequestDTO req = mock(CreateChatMessageRequestDTO.class);
        when(req.getChattingRoomId()).thenReturn(roomId);
        when(chattingRoomRepository.findById(roomId)).thenReturn(Optional.empty());

        assertThrows(ChatException.class, () -> service.saveMessage(req, userId));
        verifyNoInteractions(userRepository, chatPartRepository, messageRepository);
    }

    @Test
    @DisplayName("메시지 저장 실패: 유저 없음 → RestApiException")
    void saveMessage_userNotFound() {
        CreateChatMessageRequestDTO req = mock(CreateChatMessageRequestDTO.class);
        when(req.getChattingRoomId()).thenReturn(roomId);
        when(chattingRoomRepository.findById(roomId)).thenReturn(Optional.of(ChattingRoom.builder().id(roomId).build()));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(RestApiException.class, () -> service.saveMessage(req, userId));
        verifyNoInteractions(chatPartRepository, messageRepository);
    }

    @Test
    @DisplayName("메시지 저장 실패: 내용 비어있음 → ChatException(MESSAGE_EMPTY)")
    void saveMessage_emptyContent() {
        CreateChatMessageRequestDTO req = mock(CreateChatMessageRequestDTO.class);
        when(req.getChattingRoomId()).thenReturn(roomId);
        when(req.getContent()).thenReturn("   "); // 공백

        when(chattingRoomRepository.findById(roomId)).thenReturn(Optional.of(ChattingRoom.builder().id(roomId).build()));
        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().id(userId).build()));

        assertThrows(ChatException.class, () -> service.saveMessage(req, userId));
        verifyNoInteractions(chatPartRepository, messageRepository);
    }

    @Test
    @DisplayName("메시지 저장 실패: 참여자 없음 → ChatException")
    void saveMessage_chatPartNotFound() {
        CreateChatMessageRequestDTO req = mock(CreateChatMessageRequestDTO.class);
        when(req.getChattingRoomId()).thenReturn(roomId);
        when(req.getContent()).thenReturn("내용");

        ChattingRoom room = ChattingRoom.builder().id(roomId).build();
        when(chattingRoomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().id(userId).build()));
        when(chatPartRepository.findByUserIdAndChattingRoomId(userId, roomId)).thenReturn(Optional.empty());

        assertThrows(ChatException.class, () -> service.saveMessage(req, userId));
        verify(messageRepository, never()).save(any());
    }

    @Test
    @DisplayName("채팅방 참여: 이미 참여한 경우 save 호출 없음")
    void joinChatRoom_alreadyJoined() {
        ChattingRoom room = ChattingRoom.builder()
                .id(roomId)
                .careertalk(Careertalk.builder().id(5L).user(User.builder().id(999L).build()).build())
                .build();

        when(chattingRoomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().id(userId).build()));
        when(chatPartRepository.existsByUserAndChattingRoom(any(User.class), any(ChattingRoom.class))).thenReturn(true);

        service.joinChatRoom(roomId, userId);

        verify(chatPartRepository, never()).save(any());
    }

    @Test
    @DisplayName("채팅방 참여: 작성자가 참여하면 닉네임은 '작성자'")
    void joinChatRoom_authorNickname() {
        User author = User.builder().id(userId).build();
        Careertalk post = Careertalk.builder().id(5L).user(author).build();

        ChattingRoom room = ChattingRoom.builder()
                .id(roomId)
                .careertalk(post)
                .build();

        when(chattingRoomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(userRepository.findById(userId)).thenReturn(Optional.of(author));
        when(chatPartRepository.existsByUserAndChattingRoom(author, room)).thenReturn(false);

        ArgumentCaptor<ChatPart> partCaptor = ArgumentCaptor.forClass(ChatPart.class);
        when(chatPartRepository.save(any(ChatPart.class))).thenAnswer(inv -> inv.getArgument(0));

        service.joinChatRoom(roomId, userId);

        verify(chatPartRepository).save(partCaptor.capture());
        ChatPart saved = partCaptor.getValue();
        assertEquals("작성자", saved.getAnonymousName());
        assertSame(author, saved.getUser());
        assertSame(room, saved.getChattingRoom());
    }

    @Test
    @DisplayName("채팅방 참여: 비작성자 참여 시 '익명n' 부여 (count+1)")
    void joinChatRoom_nonAuthorNickname() {
        User author = User.builder().id(999L).build();
        User joiner = User.builder().id(userId).build();
        Careertalk post = Careertalk.builder().id(5L).user(author).build();

        ChattingRoom room = ChattingRoom.builder()
                .id(roomId)
                .careertalk(post)
                .build();

        when(chattingRoomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(userRepository.findById(userId)).thenReturn(Optional.of(joiner));
        when(chatPartRepository.existsByUserAndChattingRoom(joiner, room)).thenReturn(false);
        when(chatPartRepository.countByChattingRoom(room)).thenReturn(3);

        ArgumentCaptor<ChatPart> partCaptor = ArgumentCaptor.forClass(ChatPart.class);
        when(chatPartRepository.save(any(ChatPart.class))).thenAnswer(inv -> inv.getArgument(0));

        service.joinChatRoom(roomId, userId);

        verify(chatPartRepository).save(partCaptor.capture());
        ChatPart saved = partCaptor.getValue();
        assertEquals("익명4", saved.getAnonymousName());
        assertSame(joiner, saved.getUser());
        assertSame(room, saved.getChattingRoom());
    }

    @Test
    @DisplayName("채팅방 참여 실패: 채팅방 없음 → ChatException")
    void joinChatRoom_roomNotFound() {
        when(chattingRoomRepository.findById(roomId)).thenReturn(Optional.empty());
        assertThrows(ChatException.class, () -> service.joinChatRoom(roomId, userId));
        verifyNoInteractions(userRepository, chatPartRepository);
    }

    @Test
    @DisplayName("채팅방 참여 실패: 유저 없음 → RestApiException")
    void joinChatRoom_userNotFound() {
        when(chattingRoomRepository.findById(roomId)).thenReturn(Optional.of(ChattingRoom.builder().id(roomId).build()));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(RestApiException.class, () -> service.joinChatRoom(roomId, userId));
        verify(chatPartRepository, never()).existsByUserAndChattingRoom(any(), any());
    }
}
