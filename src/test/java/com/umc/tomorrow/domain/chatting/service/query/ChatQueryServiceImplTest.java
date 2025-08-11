package com.umc.tomorrow.domain.chatting.service.query;

import com.umc.tomorrow.domain.chatting.converter.ChatConverter;
import com.umc.tomorrow.domain.chatting.dto.response.GetChatMessageListResponseDTO;
import com.umc.tomorrow.domain.chatting.dto.response.GetChatMessageResponseDTO;
import com.umc.tomorrow.domain.chatting.entity.ChattingRoom;
import com.umc.tomorrow.domain.chatting.entity.Message;
import com.umc.tomorrow.domain.chatting.exception.ChatException;
import com.umc.tomorrow.domain.chatting.repository.ChattingRoomRepository;
import com.umc.tomorrow.domain.chatting.repository.MessageRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("ChatQueryServiceImpl 단위 테스트")
@ExtendWith(MockitoExtension.class)
class ChatQueryServiceImplTest {

    @InjectMocks
    private ChatQueryServiceImpl service;

    @Mock private MessageRepository messageRepository;
    @Mock private ChattingRoomRepository chattingRoomRepository;

    private final Long roomId = 10L;

    @Test
    @DisplayName("첫 페이지(cursor=null): 최신 메시지 조회 메서드 호출, 변환 성공, hasNext=true")
    void getMessages_firstPage_success() {
        // given
        int size = 3;
        ChattingRoom room = ChattingRoom.builder().id(roomId).build();
        when(chattingRoomRepository.findById(roomId)).thenReturn(Optional.of(room));

        Message m1 = Message.builder().id(30L).content("m30").chattingRoom(room).build();
        Message m2 = Message.builder().id(29L).content("m29").chattingRoom(room).build();
        Message m3 = Message.builder().id(28L).content("m28").chattingRoom(room).build();

        // Slice(hasNext=true)
        Slice<Message> slice = new SliceImpl<>(List.of(m1, m2, m3), PageRequest.of(0, size), true);
        when(messageRepository.findByChattingRoomIdOrderByIdDesc(eq(roomId), any(Pageable.class)))
                .thenReturn(slice);

        try (MockedStatic<ChatConverter> mocked = mockStatic(ChatConverter.class)) {
            mocked.when(() -> ChatConverter.toGetChatMessageResponseDTO(any(Message.class)))
                    .thenAnswer(inv -> mock(GetChatMessageResponseDTO.class));

            // when
            GetChatMessageListResponseDTO res = service.getMessages(roomId, null, size);

            // then
            assertNotNull(res);
            assertEquals(3, res.getChatMessageList().size());
            assertTrue(res.isHasNext());

            // Pageable 사이즈 검증
            ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
            verify(messageRepository).findByChattingRoomIdOrderByIdDesc(eq(roomId), pageableCaptor.capture());
            assertEquals(size, pageableCaptor.getValue().getPageSize());
        }
    }

    @Test
    @DisplayName("다음 페이지(cursor 존재): cursor 이전 메시지 조회 메서드 호출, 변환 성공, hasNext=false")
    void getMessages_nextPage_success() {
        // given
        int size = 2;
        Long cursor = 25L;
        ChattingRoom room = ChattingRoom.builder().id(roomId).build();
        when(chattingRoomRepository.findById(roomId)).thenReturn(Optional.of(room));

        Message m1 = Message.builder().id(24L).content("m24").chattingRoom(room).build();
        Message m2 = Message.builder().id(23L).content("m23").chattingRoom(room).build();

        // Slice(hasNext=false)
        Slice<Message> slice = new SliceImpl<>(List.of(m1, m2), PageRequest.of(0, size), false);
        when(messageRepository.findByChattingRoomIdAndIdLessThanOrderByIdDesc(eq(roomId), eq(cursor), any(Pageable.class)))
                .thenReturn(slice);

        try (MockedStatic<ChatConverter> mocked = mockStatic(ChatConverter.class)) {
            mocked.when(() -> ChatConverter.toGetChatMessageResponseDTO(any(Message.class)))
                    .thenAnswer(inv -> mock(GetChatMessageResponseDTO.class));

            // when
            GetChatMessageListResponseDTO res = service.getMessages(roomId, cursor, size);

            // then
            assertNotNull(res);
            assertEquals(2, res.getChatMessageList().size());
            assertFalse(res.isHasNext());

            ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
            verify(messageRepository).findByChattingRoomIdAndIdLessThanOrderByIdDesc(eq(roomId), eq(cursor), pageableCaptor.capture());
            assertEquals(size, pageableCaptor.getValue().getPageSize());
        }
    }

    @Test
    @DisplayName("채팅방 없음: ChatException")
    void getMessages_roomNotFound() {
        when(chattingRoomRepository.findById(roomId)).thenReturn(Optional.empty());
        assertThrows(ChatException.class, () -> service.getMessages(roomId, null, 10));
        verifyNoInteractions(messageRepository);
    }
}
