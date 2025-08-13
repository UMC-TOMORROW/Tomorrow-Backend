package com.umc.tomorrow.domain.careertalk.service.query;

import com.umc.tomorrow.domain.careertalk.converter.CareertalkConverter;
import com.umc.tomorrow.domain.careertalk.dto.response.GetCareertalkListResponseDto;
import com.umc.tomorrow.domain.careertalk.dto.response.GetCareertalkResponseDto;
import com.umc.tomorrow.domain.careertalk.entity.Careertalk;
import com.umc.tomorrow.domain.careertalk.exception.CareertalkException;
import com.umc.tomorrow.domain.careertalk.repository.CareertalkRepository;
import com.umc.tomorrow.domain.chatting.entity.ChattingRoom;
import com.umc.tomorrow.domain.member.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("CareertalkQueryService 단위 테스트")
@ExtendWith(MockitoExtension.class)
class CareertalkQueryServiceImplTest {

    @InjectMocks
    private CareertalkQueryServiceImpl service;

    @Mock
    private CareertalkRepository careertalkRepository;

    @Test
    @DisplayName("getCareertalks: 첫 페이지(cursor=null)면 findAllByOrderByIdDesc 호출, 변환 성공")
    void getCareertalks_firstPage_success() {
        int size = 2;
        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "id"));

        Careertalk c1 = Careertalk.builder().id(3L).title("t3").category("cat").content("c").build();
        Careertalk c2 = Careertalk.builder().id(2L).title("t2").category("cat").content("c").build();
        Slice<Careertalk> slice = new SliceImpl<>(List.of(c1, c2), pageable, true);

        when(careertalkRepository.findAllByOrderByIdDesc(any(Pageable.class))).thenReturn(slice);

        try (MockedStatic<CareertalkConverter> mocked = mockStatic(CareertalkConverter.class)) {
            mocked.when(() -> CareertalkConverter.toGetCareertalkResponseDto(any(Careertalk.class)))
                    .thenAnswer(inv -> mock(GetCareertalkResponseDto.class));

            GetCareertalkListResponseDto res = service.getCareertalks(null, size);

            assertNotNull(res);
            assertTrue(res.isHasNext());
            assertEquals(2, res.getCareertalkList().size());
            verify(careertalkRepository).findAllByOrderByIdDesc(any(Pageable.class));
        }
    }

    @Test
    @DisplayName("getCareertalks: 다음 페이지(cursor 존재)면 findByIdLessThanOrderByIdDesc 호출")
    void getCareertalks_nextPage_success() {
        Long cursor = 10L;
        int size = 3;
        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "id"));
        Careertalk c1 = Careertalk.builder().id(9L).title("t9").category("cat").content("c").build();
        Slice<Careertalk> slice = new SliceImpl<>(List.of(c1), pageable, false);

        when(careertalkRepository.findByIdLessThanOrderByIdDesc(eq(cursor), any(Pageable.class)))
                .thenReturn(slice);

        try (MockedStatic<CareertalkConverter> mocked = mockStatic(CareertalkConverter.class)) {
            mocked.when(() -> CareertalkConverter.toGetCareertalkResponseDto(any(Careertalk.class)))
                    .thenAnswer(inv -> mock(GetCareertalkResponseDto.class));

            GetCareertalkListResponseDto res = service.getCareertalks(cursor, size);

            assertNotNull(res);
            assertFalse(res.isHasNext());
            assertEquals(1, res.getCareertalkList().size());
            verify(careertalkRepository).findByIdLessThanOrderByIdDesc(eq(cursor), any(Pageable.class));
        }
    }

    @Test
    @DisplayName("getCareertalk: 본인 글이면 isAuthor=true로 반환")
    void getCareertalk_detail_success_isAuthorTrue() {
        Long ctId = 100L;
        Long userId = 1L;

        User author = User.builder().id(userId).build();
        ChattingRoom room = ChattingRoom.builder().id(77L).build();
        Careertalk ct = Careertalk.builder()
                .id(ctId).user(author).title("제목").category("cat").content("내용").chattingRoom(room)
                .build();

        when(careertalkRepository.findById(ctId)).thenReturn(Optional.of(ct));

        GetCareertalkResponseDto res = service.getCareertalk(ctId, userId);

        assertNotNull(res);
        assertEquals(ctId, res.getId());
        assertEquals("제목", res.getTitle());
        assertEquals("cat", res.getCategory());
        assertEquals("내용", res.getContent());
        assertTrue(res.isAuthor());
        assertEquals(77L, res.getChatroomId());
    }

    @Test
    @DisplayName("getCareertalk: 타인 글이면 isAuthor=false")
    void getCareertalk_detail_success_isAuthorFalse() {
        Long ctId = 100L;
        Long viewerId = 9L;

        User author = User.builder().id(1L).build();
        ChattingRoom room = ChattingRoom.builder().id(77L).build();
        Careertalk ct = Careertalk.builder()
                .id(ctId).user(author).title("제목").category("cat").content("내용").chattingRoom(room)
                .build();

        when(careertalkRepository.findById(ctId)).thenReturn(Optional.of(ct));

        GetCareertalkResponseDto res = service.getCareertalk(ctId, viewerId);

        assertNotNull(res);
        assertFalse(res.isAuthor());
    }

    @Test
    @DisplayName("getCareertalk: 게시글이 없으면 CareertalkException")
    void getCareertalk_detail_notFound() {
        when(careertalkRepository.findById(123L)).thenReturn(Optional.empty());
        assertThrows(CareertalkException.class, () -> service.getCareertalk(123L, 1L));
    }

    @Test
    @DisplayName("getCareertalksByTitle: 첫 페이지(cursor=null)면 findByTitleContainingIgnoreCaseOrderByIdDesc 호출")
    void getCareertalksByTitle_firstPage_success() {
        String title = "자격증";
        int size = 2;
        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "id"));

        Careertalk c1 = Careertalk.builder().id(5L).title("자격증 t1").category("cat").content("c").build();
        Careertalk c2 = Careertalk.builder().id(4L).title("자격증 t2").category("cat").content("c").build();
        Slice<Careertalk> slice = new SliceImpl<>(List.of(c1, c2), pageable, true);

        when(careertalkRepository.findByTitleContainingIgnoreCaseOrderByIdDesc(eq(title), any(Pageable.class)))
                .thenReturn(slice);

        try (MockedStatic<CareertalkConverter> mocked = mockStatic(CareertalkConverter.class)) {
            mocked.when(() -> CareertalkConverter.toGetCareertalkResponseDto(any(Careertalk.class)))
                    .thenAnswer(inv -> mock(GetCareertalkResponseDto.class));

            GetCareertalkListResponseDto res = service.getCareertalksByTitle(title, null, size);

            assertNotNull(res);
            assertEquals(2, res.getCareertalkList().size());
            assertTrue(res.isHasNext());
            verify(careertalkRepository).findByTitleContainingIgnoreCaseOrderByIdDesc(eq(title), any(Pageable.class));
        }
    }

    @Test
    @DisplayName("getCareertalksByTitle: 다음 페이지(cursor 존재)면 findByTitleContainingIgnoreCaseAndIdLessThanOrderByIdDesc 호출")
    void getCareertalksByTitle_nextPage_success() {
        String title = "자격증";
        Long cursor = 50L;
        int size = 2;
        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "id"));

        Careertalk c1 = Careertalk.builder().id(49L).title("자격증 t").category("cat").content("c").build();
        Slice<Careertalk> slice = new SliceImpl<>(List.of(c1), pageable, false);

        when(careertalkRepository.findByTitleContainingIgnoreCaseAndIdLessThanOrderByIdDesc(
                eq(title), eq(cursor), any(Pageable.class))).thenReturn(slice);

        try (MockedStatic<CareertalkConverter> mocked = mockStatic(CareertalkConverter.class)) {
            mocked.when(() -> CareertalkConverter.toGetCareertalkResponseDto(any(Careertalk.class)))
                    .thenAnswer(inv -> mock(GetCareertalkResponseDto.class));

            GetCareertalkListResponseDto res = service.getCareertalksByTitle(title, cursor, size);

            assertNotNull(res);
            assertEquals(1, res.getCareertalkList().size());
            assertFalse(res.isHasNext());
            verify(careertalkRepository).findByTitleContainingIgnoreCaseAndIdLessThanOrderByIdDesc(
                    eq(title), eq(cursor), any(Pageable.class));
        }
    }

    // ---------- getCareertalksByCategory ----------
    @Test
    @DisplayName("getCareertalksByCategory: 첫 페이지(cursor=null)면 findByCategoryOrderByIdDesc 호출")
    void getCareertalksByCategory_firstPage_success() {
        String category = "이직";
        int size = 3;
        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "id"));

        Careertalk c1 = Careertalk.builder().id(30L).title("a").category(category).content("c").build();
        Careertalk c2 = Careertalk.builder().id(29L).title("b").category(category).content("c").build();
        Careertalk c3 = Careertalk.builder().id(28L).title("c").category(category).content("c").build();
        Slice<Careertalk> slice = new SliceImpl<>(List.of(c1, c2, c3), pageable, false);

        when(careertalkRepository.findByCategoryOrderByIdDesc(eq(category), any(Pageable.class)))
                .thenReturn(slice);

        try (MockedStatic<CareertalkConverter> mocked = mockStatic(CareertalkConverter.class)) {
            mocked.when(() -> CareertalkConverter.toGetCareertalkResponseDto(any(Careertalk.class)))
                    .thenAnswer(inv -> mock(GetCareertalkResponseDto.class));

            GetCareertalkListResponseDto res = service.getCareertalksByCategory(category, null, size);

            assertNotNull(res);
            assertEquals(3, res.getCareertalkList().size());
            assertFalse(res.isHasNext());
            verify(careertalkRepository).findByCategoryOrderByIdDesc(eq(category), any(Pageable.class));
        }
    }

    @Test
    @DisplayName("getCareertalksByCategory: 다음 페이지(cursor 존재)면 findByCategoryAndIdLessThanOrderByIdDesc 호출")
    void getCareertalksByCategory_nextPage_success() {
        String category = "이직";
        Long cursor = 100L;
        int size = 1;
        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "id"));

        Careertalk c1 = Careertalk.builder().id(99L).title("x").category(category).content("c").build();
        Slice<Careertalk> slice = new SliceImpl<>(List.of(c1), pageable, true);

        when(careertalkRepository.findByCategoryAndIdLessThanOrderByIdDesc(eq(category), eq(cursor), any(Pageable.class)))
                .thenReturn(slice);

        try (MockedStatic<CareertalkConverter> mocked = mockStatic(CareertalkConverter.class)) {
            mocked.when(() -> CareertalkConverter.toGetCareertalkResponseDto(any(Careertalk.class)))
                    .thenAnswer(inv -> mock(GetCareertalkResponseDto.class));

            GetCareertalkListResponseDto res = service.getCareertalksByCategory(category, cursor, size);

            assertNotNull(res);
            assertEquals(1, res.getCareertalkList().size());
            assertTrue(res.isHasNext());
            verify(careertalkRepository).findByCategoryAndIdLessThanOrderByIdDesc(eq(category), eq(cursor), any(Pageable.class));
        }
    }
}
