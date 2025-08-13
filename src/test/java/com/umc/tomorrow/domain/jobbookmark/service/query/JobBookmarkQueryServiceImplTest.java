package com.umc.tomorrow.domain.jobbookmark.service.query;

import com.umc.tomorrow.domain.job.entity.Job;
import com.umc.tomorrow.domain.jobbookmark.dto.response.GetJobBookmarkListResponseDTO;
import com.umc.tomorrow.domain.jobbookmark.dto.response.JobBookmarkResponseDTO;
import com.umc.tomorrow.domain.jobbookmark.entity.JobBookmark;
import com.umc.tomorrow.domain.jobbookmark.repository.JobBookmarkRepository;
import com.umc.tomorrow.domain.member.entity.User;
import com.umc.tomorrow.domain.member.exception.code.MemberStatus;
import com.umc.tomorrow.domain.member.repository.UserRepository;
import com.umc.tomorrow.global.common.exception.RestApiException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("JobBookmarkQueryServiceImpl 단위 테스트")
@ExtendWith(MockitoExtension.class)
class JobBookmarkQueryServiceImplTest {

    @InjectMocks
    private JobBookmarkQueryServiceImpl service;

    @Mock
    private JobBookmarkRepository jobBookmarkRepository;

    @Mock
    private UserRepository userRepository;

    private final Long userId = 1L;
    private final Long jobId1 = 100L;
    private final Long jobId2 = 101L;
    private final Long bookmarkId1 = 1000L;
    private final Long bookmarkId2 = 1001L;

    @Test
    @DisplayName("getList: 첫 페이지(cursor=null) 조회 성공 - 사용자 검증 통과 후 북마크 목록 반환")
    void getList_firstPage_success() {
        // given
        int size = 2;
        PageRequest pageRequest = PageRequest.of(0, size);

        User user = User.builder().id(userId).build();
        Job job1 = Job.builder().id(jobId1).title("백엔드 개발자").companyName("테크컴퍼니").build();
        Job job2 = Job.builder().id(jobId2).title("프론트엔드 개발자").companyName("웹컴퍼니").build();

        JobBookmark bookmark1 = JobBookmark.builder()
                .id(bookmarkId1)
                .user(user)
                .job(job1)
                .createdAt(LocalDateTime.now())
                .build();
        JobBookmark bookmark2 = JobBookmark.builder()
                .id(bookmarkId2)
                .user(user)
                .job(job2)
                .createdAt(LocalDateTime.now())
                .build();

        List<JobBookmark> bookmarks = List.of(bookmark1, bookmark2);
        Slice<JobBookmark> slice = new SliceImpl<>(bookmarks, pageRequest, true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(jobBookmarkRepository.findByUserIdAndIdLessThanOrderByIdDesc(eq(userId), eq(null), any(PageRequest.class)))
                .thenReturn(slice);

        // when
        GetJobBookmarkListResponseDTO result = service.getList(userId, null, size);

        // then
        assertNotNull(result);
        assertEquals(2, result.getBookmarks().size());
        assertTrue(result.isHasNext());
        assertEquals(bookmarkId2, result.getLastCursor()); // 마지막 북마크의 ID가 lastCursor

        // 첫 번째 북마크 검증
        JobBookmarkResponseDTO firstBookmark = result.getBookmarks().get(0);
        assertEquals(bookmarkId1, firstBookmark.getId());
        assertEquals(jobId1, firstBookmark.getJobId());
        assertEquals("백엔드 개발자", firstBookmark.getJobTitle());
        assertEquals("테크컴퍼니", firstBookmark.getCompanyName());
        assertNotNull(firstBookmark.getBookmarkedAt());

        // 두 번째 북마크 검증
        JobBookmarkResponseDTO secondBookmark = result.getBookmarks().get(1);
        assertEquals(bookmarkId2, secondBookmark.getId());
        assertEquals(jobId2, secondBookmark.getJobId());
        assertEquals("프론트엔드 개발자", secondBookmark.getJobTitle());
        assertEquals("웹컴퍼니", secondBookmark.getCompanyName());
        assertNotNull(secondBookmark.getBookmarkedAt());

        verify(userRepository).findById(userId);
        verify(jobBookmarkRepository).findByUserIdAndIdLessThanOrderByIdDesc(eq(userId), eq(null), any(PageRequest.class));
    }

    @Test
    @DisplayName("getList: 다음 페이지(cursor 존재) 조회 성공 - cursor 기반 페이지네이션")
    void getList_nextPage_success() {
        // given
        Long cursor = 50L;
        int size = 1;
        PageRequest pageRequest = PageRequest.of(0, size);

        User user = User.builder().id(userId).build();
        Job job = Job.builder().id(jobId1).title("데이터 엔지니어").companyName("데이터컴퍼니").build();
        JobBookmark bookmark = JobBookmark.builder()
                .id(bookmarkId1)
                .user(user)
                .job(job)
                .createdAt(LocalDateTime.now())
                .build();

        List<JobBookmark> bookmarks = List.of(bookmark);
        Slice<JobBookmark> slice = new SliceImpl<>(bookmarks, pageRequest, false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(jobBookmarkRepository.findByUserIdAndIdLessThanOrderByIdDesc(eq(userId), eq(cursor), any(PageRequest.class)))
                .thenReturn(slice);

        // when
        GetJobBookmarkListResponseDTO result = service.getList(userId, cursor, size);

        // then
        assertNotNull(result);
        assertEquals(1, result.getBookmarks().size());
        assertFalse(result.isHasNext());
        assertEquals(bookmarkId1, result.getLastCursor());

        JobBookmarkResponseDTO bookmarkDto = result.getBookmarks().get(0);
        assertEquals(bookmarkId1, bookmarkDto.getId());
        assertEquals(jobId1, bookmarkDto.getJobId());
        assertEquals("데이터 엔지니어", bookmarkDto.getJobTitle());
        assertEquals("데이터컴퍼니", bookmarkDto.getCompanyName());

        verify(jobBookmarkRepository).findByUserIdAndIdLessThanOrderByIdDesc(eq(userId), eq(cursor), any(PageRequest.class));
    }

    @Test
    @DisplayName("getList: 빈 페이지 조회 - 북마크가 없는 경우")
    void getList_emptyPage_success() {
        // given
        int size = 5;
        PageRequest pageRequest = PageRequest.of(0, size);

        User user = User.builder().id(userId).build();
        List<JobBookmark> emptyBookmarks = List.of();
        Slice<JobBookmark> slice = new SliceImpl<>(emptyBookmarks, pageRequest, false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(jobBookmarkRepository.findByUserIdAndIdLessThanOrderByIdDesc(eq(userId), eq(null), any(PageRequest.class)))
                .thenReturn(slice);

        // when
        GetJobBookmarkListResponseDTO result = service.getList(userId, null, size);

        // then
        assertNotNull(result);
        assertEquals(0, result.getBookmarks().size());
        assertFalse(result.isHasNext());
        assertNull(result.getLastCursor()); // 빈 페이지면 lastCursor는 null
    }

    @Test
    @DisplayName("getList: 사용자가 존재하지 않으면 MemberStatus.MEMBER_NOT_FOUND 예외 발생")
    void getList_userNotFound_throwsException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        RestApiException exception = assertThrows(RestApiException.class, 
            () -> service.getList(userId, null, 5));

        assertEquals(MemberStatus.MEMBER_NOT_FOUND, exception.getBaseCode());
        verifyNoInteractions(jobBookmarkRepository);
    }

    @Test
    @DisplayName("getList: 마지막 페이지 - hasNext=false, lastCursor 설정")
    void getList_lastPage_success() {
        // given
        int size = 3;
        PageRequest pageRequest = PageRequest.of(0, size);

        User user = User.builder().id(userId).build();
        Job job1 = Job.builder().id(jobId1).title("직무1").companyName("회사1").build();
        Job job2 = Job.builder().id(jobId2).title("직무2").companyName("회사2").build();

        JobBookmark bookmark1 = JobBookmark.builder()
                .id(bookmarkId1)
                .user(user)
                .job(job1)
                .createdAt(LocalDateTime.now())
                .build();
        JobBookmark bookmark2 = JobBookmark.builder()
                .id(bookmarkId2)
                .user(user)
                .job(job2)
                .createdAt(LocalDateTime.now())
                .build();

        List<JobBookmark> bookmarks = List.of(bookmark1, bookmark2);
        Slice<JobBookmark> slice = new SliceImpl<>(bookmarks, pageRequest, false); // hasNext = false

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(jobBookmarkRepository.findByUserIdAndIdLessThanOrderByIdDesc(eq(userId), eq(null), any(PageRequest.class)))
                .thenReturn(slice);

        // when
        GetJobBookmarkListResponseDTO result = service.getList(userId, null, size);

        // then
        assertNotNull(result);
        assertEquals(2, result.getBookmarks().size());
        assertFalse(result.isHasNext()); // 마지막 페이지
        assertEquals(bookmarkId2, result.getLastCursor()); // 마지막 북마크의 ID
    }

    @Test
    @DisplayName("getList: repository 예외 발생 시 적절한 예외 전파")
    void getList_repositoryException_propagatesException() {
        User user = User.builder().id(userId).build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(jobBookmarkRepository.findByUserIdAndIdLessThanOrderByIdDesc(eq(userId), eq(null), any(PageRequest.class)))
                .thenThrow(new RuntimeException("DB 조회 실패"));

        assertThrows(RuntimeException.class, () -> service.getList(userId, null, 5));
    }

    @Test
    @DisplayName("getList: PageRequest 생성 검증 - 올바른 size로 PageRequest 생성")
    void getList_pageRequestCreation_verification() {
        // given
        int size = 10;
        User user = User.builder().id(userId).build();
        List<JobBookmark> bookmarks = List.of();
        Slice<JobBookmark> slice = new SliceImpl<>(bookmarks, PageRequest.of(0, size), false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(jobBookmarkRepository.findByUserIdAndIdLessThanOrderByIdDesc(eq(userId), eq(null), any(PageRequest.class)))
                .thenReturn(slice);

        // when
        service.getList(userId, null, size);

        // then
        // PageRequest가 올바른 size로 생성되었는지 검증
        verify(jobBookmarkRepository).findByUserIdAndIdLessThanOrderByIdDesc(
            eq(userId), eq(null), argThat(pageable -> pageable.getPageSize() == size)
        );
    }
}



