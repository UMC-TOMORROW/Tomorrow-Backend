package com.umc.tomorrow.domain.careertalk.service.command;

import com.umc.tomorrow.domain.careertalk.converter.CareertalkConverter;
import com.umc.tomorrow.domain.careertalk.dto.request.CreateCareertalkRequestDTO;
import com.umc.tomorrow.domain.careertalk.dto.request.UpdateCareertalkRequestDTO;
import com.umc.tomorrow.domain.careertalk.dto.response.CareertalkResponseDto;
import com.umc.tomorrow.domain.careertalk.entity.Careertalk;
import com.umc.tomorrow.domain.careertalk.exception.CareertalkException;
import com.umc.tomorrow.domain.careertalk.repository.CareertalkRepository;
import com.umc.tomorrow.domain.chatting.entity.ChattingRoom;
import com.umc.tomorrow.domain.chatting.repository.ChattingRoomRepository;
import com.umc.tomorrow.domain.member.entity.User;
import com.umc.tomorrow.domain.member.repository.UserRepository;
import com.umc.tomorrow.global.common.exception.RestApiException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.util.Optional;

import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;

@DisplayName("CareertalkCommandService 단위 테스트")
@ExtendWith(MockitoExtension.class)
class CareertalkCommandServiceImplTest {

    @InjectMocks
    private CareertalkCommandServiceImpl service;

    @Mock private CareertalkRepository careertalkRepository;
    @Mock private UserRepository userRepository;
    @Mock private ChattingRoomRepository chattingRoomRepository;

    private final Long userId = 1L;
    private final Long otherUserId = 9L;
    private final Long careertalkId = 100L;

    private User user;
    private User otherUser;

    @BeforeEach
    void setUp() {
        user = User.builder().id(userId).build();
        otherUser = User.builder().id(otherUserId).build();
    }

    @Test
    @DisplayName("커리어톡 생성 성공: 커리어톡 저장 후 같은 커리어톡으로 채팅방 생성 + Converter 동일 인자 + 저장 순서")
    void createCareertalk_success_withChatRoom_andOrder_andConverterArg() {
        // given
        CreateCareertalkRequestDTO req = CreateCareertalkRequestDTO.builder()
                .category("무료자격증 추천")
                .title("첫 글")
                .content("내용")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(careertalkRepository.save(any(Careertalk.class))).thenAnswer(returnsFirstArg());
        when(chattingRoomRepository.save(any(ChattingRoom.class))).thenAnswer(returnsFirstArg());

        CareertalkResponseDto fakeDto = mock(CareertalkResponseDto.class);

        try (MockedStatic<CareertalkConverter> mocked = mockStatic(CareertalkConverter.class)) {
            mocked.when(() -> CareertalkConverter.toCareertalkResponseDto(any(Careertalk.class)))
                    .thenReturn(fakeDto);

            // when
            CareertalkResponseDto res = service.createCareertalk(userId, req);

            // then
            assertNotNull(res);
            assertSame(fakeDto, res);

            ArgumentCaptor<Careertalk> ctCaptor = ArgumentCaptor.forClass(Careertalk.class); //mock 객체의 메서드 호출 시 전달된 인자 캡처
            ArgumentCaptor<ChattingRoom> roomCaptor = ArgumentCaptor.forClass(ChattingRoom.class);

            verify(careertalkRepository).save(ctCaptor.capture());
            verify(chattingRoomRepository).save(roomCaptor.capture());

            Careertalk savedCt = ctCaptor.getValue(); //실제 저장 로직에 넘어간 객체
            ChattingRoom savedRoom = roomCaptor.getValue();

            assertNotNull(savedCt);
            assertNotNull(savedRoom);

            assertSame(savedCt, savedRoom.getCareertalk()); //완전히 동일한 객체인지 비교

            mocked.verify(() -> CareertalkConverter.toCareertalkResponseDto(savedCt));

            InOrder inOrder = inOrder(careertalkRepository, chattingRoomRepository); //메서드 호출 순서 검증
            inOrder.verify(careertalkRepository).save(any(Careertalk.class));
            inOrder.verify(chattingRoomRepository).save(any(ChattingRoom.class));
            inOrder.verifyNoMoreInteractions();
        }
    }

    @Test
    @DisplayName("커리어톡 생성 실패: 사용자 없음이면 저장 시도 없음")
    void createCareertalk_userNotFound_noSideEffects() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        CreateCareertalkRequestDTO req = CreateCareertalkRequestDTO.builder()
                .category("취업").title("t").content("c").build();

        assertThrows(RestApiException.class, () -> service.createCareertalk(userId, req));
        verify(userRepository).findById(userId);
        verifyNoInteractions(careertalkRepository, chattingRoomRepository);
    }

    @Test
    @DisplayName("커리어톡 수정 성공: 본인 글이면 수정되고 DTO 반환")
    void updateCareertalk_success() {
        UpdateCareertalkRequestDTO req = UpdateCareertalkRequestDTO.builder()
                .category("이직")
                .title("제목수정")
                .content("내용수정")
                .build();

        Careertalk existing = Careertalk.builder()
                .id(careertalkId)
                .user(user)
                .category("취업")
                .title("제목")
                .content("내용")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(careertalkRepository.findById(careertalkId)).thenReturn(Optional.of(existing));

        CareertalkResponseDto fakeDto = mock(CareertalkResponseDto.class);

        try (MockedStatic<CareertalkConverter> mocked = mockStatic(CareertalkConverter.class)) {
            mocked.when(() -> CareertalkConverter.toCareertalkResponseDto(existing)).thenReturn(fakeDto);

            CareertalkResponseDto res = service.updateCareertalk(userId, careertalkId, req);

            assertNotNull(res);
            assertSame(fakeDto, res);
            verify(userRepository).findById(userId);
            verify(careertalkRepository).findById(careertalkId);
        }
    }

    @Test
    @DisplayName("커리어톡 수정 실패: 작성자 아님 → FORBIDDEN")
    void updateCareertalk_forbidden_whenOtherUser() {
        UpdateCareertalkRequestDTO req = UpdateCareertalkRequestDTO.builder()
                .category("이직").title("t").content("c").build();

        Careertalk existing = Careertalk.builder()
                .id(careertalkId)
                .user(otherUser)
                .category("취업").title("제목").content("내용").build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(careertalkRepository.findById(careertalkId)).thenReturn(Optional.of(existing));

        assertThrows(CareertalkException.class,
                () -> service.updateCareertalk(userId, careertalkId, req));
        verify(userRepository).findById(userId);
        verify(careertalkRepository).findById(careertalkId);
    }

    @Test
    @DisplayName("커리어톡 수정 실패: 게시글 없음 → NOT_FOUND")
    void updateCareertalk_notFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(careertalkRepository.findById(careertalkId)).thenReturn(Optional.empty());
        UpdateCareertalkRequestDTO req = UpdateCareertalkRequestDTO.builder()
                .category("x").title("y").content("z").build();

        assertThrows(CareertalkException.class,
                () -> service.updateCareertalk(userId, careertalkId, req));
        verify(userRepository).findById(userId);
        verify(careertalkRepository).findById(careertalkId);
    }

    @Test
    @DisplayName("커리어톡 삭제 성공: 본인 글이면 삭제되고 DTO 반환")
    void deleteCareertalk_success() {
        Careertalk existing = Careertalk.builder()
                .id(careertalkId)
                .user(user)
                .category("취업").title("제목").content("내용").build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(careertalkRepository.findById(careertalkId)).thenReturn(Optional.of(existing));

        CareertalkResponseDto fakeDto = mock(CareertalkResponseDto.class);

        try (MockedStatic<CareertalkConverter> mocked = mockStatic(CareertalkConverter.class)) {
            mocked.when(() -> CareertalkConverter.toCareertalkResponseDto(existing)).thenReturn(fakeDto);

            CareertalkResponseDto res = service.deleteCareertalk(userId, careertalkId);

            assertNotNull(res);
            assertSame(fakeDto, res);
            verify(careertalkRepository).delete(existing);
        }
    }

    @Test
    @DisplayName("커리어톡 삭제 실패: 작성자 아님 → delete 호출 안 됨")
    void deleteCareertalk_forbidden() {
        Careertalk existing = Careertalk.builder()
                .id(careertalkId)
                .user(otherUser)
                .category("취업").title("제목").content("내용").build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(careertalkRepository.findById(careertalkId)).thenReturn(Optional.of(existing));

        assertThrows(CareertalkException.class,
                () -> service.deleteCareertalk(userId, careertalkId));
        verify(careertalkRepository, never()).delete(any());
    }

    @Test
    @DisplayName("커리어톡 삭제 실패: 게시글 없음 → delete 호출 안 됨")
    void deleteCareertalk_notFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(careertalkRepository.findById(careertalkId)).thenReturn(Optional.empty());

        assertThrows(CareertalkException.class,
                () -> service.deleteCareertalk(userId, careertalkId));
        verify(careertalkRepository, never()).delete(any());
    }
}
