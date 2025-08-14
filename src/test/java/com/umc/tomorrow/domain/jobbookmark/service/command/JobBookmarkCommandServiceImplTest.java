package com.umc.tomorrow.domain.jobbookmark.service.command;

import com.umc.tomorrow.domain.job.entity.Job;
import com.umc.tomorrow.domain.job.exception.code.JobErrorStatus;
import com.umc.tomorrow.domain.job.repository.JobRepository;
import com.umc.tomorrow.domain.jobbookmark.dto.response.JobBookmarkResponseDTO;
import com.umc.tomorrow.domain.jobbookmark.entity.JobBookmark;
import com.umc.tomorrow.domain.jobbookmark.exception.code.JobBookmarkErrorStatus;
import com.umc.tomorrow.domain.jobbookmark.repository.JobBookmarkRepository;
import com.umc.tomorrow.domain.member.entity.User;
import com.umc.tomorrow.domain.member.exception.code.MemberErrorStatus;
import com.umc.tomorrow.domain.member.repository.UserRepository;
import com.umc.tomorrow.global.common.exception.RestApiException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("JobBookmarkCommandServiceImpl 단위 테스트")
@ExtendWith(MockitoExtension.class)
class JobBookmarkCommandServiceImplTest {

    @InjectMocks
    private JobBookmarkCommandServiceImpl service;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private JobBookmarkRepository jobBookmarkRepository;

    private final Long userId = 1L;
    private final Long jobId = 100L;
    private final Long bookmarkId = 1000L;

    @Test
    @DisplayName("save: 북마크 생성 성공 - 모든 검증 통과 후 저장 및 응답 반환")
    void save_success() {
        // given
        User user = User.builder().id(userId).build();
        Job job = Job.builder()
                .id(jobId)
                .title("백엔드 개발자")
                .companyName("테크컴퍼니")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(jobBookmarkRepository.existsByUserIdAndJobId(userId, jobId)).thenReturn(false);

        // DB save는 받은 엔티티에 id만 세팅된 걸 반환한다고 가정
        Answer<JobBookmark> returnsWithId = inv -> {
            JobBookmark bookmark = inv.getArgument(0);
            return JobBookmark.builder()
                    .id(bookmarkId)
                    .user(bookmark.getUser())
                    .job(bookmark.getJob())

                    .build();
        };
        when(jobBookmarkRepository.save(any(JobBookmark.class))).thenAnswer(returnsWithId);

        // when
        JobBookmarkResponseDTO result = service.save(userId, jobId);

        // then
        assertNotNull(result);
        assertEquals(bookmarkId, result.getId());
        assertEquals(jobId, result.getJobId());
        assertEquals("백엔드 개발자", result.getJobTitle());
        assertEquals("테크컴퍼니", result.getCompanyName());
        assertNotNull(result.getBookmarkedAt());

        // 저장 시 전달된 엔티티 내용도 확인
        ArgumentCaptor<JobBookmark> captor = ArgumentCaptor.forClass(JobBookmark.class);
        verify(jobBookmarkRepository).save(captor.capture());
        JobBookmark saved = captor.getValue();
        assertSame(user, saved.getUser());
        assertSame(job, saved.getJob());
    }

    @Test
    @DisplayName("save: 사용자가 존재하지 않으면 MemberStatus.MEMBER_NOT_FOUND 예외 발생")
    void save_userNotFound_throwsException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        RestApiException exception = assertThrows(RestApiException.class, 
            () -> service.save(userId, jobId));

        assertEquals(MemberErrorStatus.MEMBER_NOT_FOUND, exception.getErrorCode().getCode());
        verifyNoInteractions(jobRepository, jobBookmarkRepository);
    }

    @Test
    @DisplayName("save: 직무가 존재하지 않으면 JobErrorStatus.JOB_NOT_FOUND 예외 발생")
    void save_jobNotFound_throwsException() {
        User user = User.builder().id(userId).build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(jobRepository.findById(jobId)).thenReturn(Optional.empty());

        RestApiException exception = assertThrows(RestApiException.class, 
            () -> service.save(userId, jobId));

        assertEquals(JobErrorStatus.JOB_NOT_FOUND, exception.getErrorCode().getCode());
        verifyNoInteractions(jobBookmarkRepository);
    }

    @Test
    @DisplayName("save: 이미 북마크가 존재하면 JobBookmarkErrorStatus.JOB_BOOKMARK_ALREADY_EXISTS 예외 발생")
    void save_bookmarkAlreadyExists_throwsException() {
        User user = User.builder().id(userId).build();
        Job job = Job.builder().id(jobId).title("직무").companyName("회사").build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(jobBookmarkRepository.existsByUserIdAndJobId(userId, jobId)).thenReturn(true);

        RestApiException exception = assertThrows(RestApiException.class, 
            () -> service.save(userId, jobId));

        assertEquals(JobBookmarkErrorStatus.JOB_BOOKMARK_ALREADY_EXISTS, exception.getErrorCode().getCode());
        verifyNoInteractions(jobBookmarkRepository);
    }

    @Test
    @DisplayName("delete: 북마크 삭제 성공 - 조회 후 삭제")
    void delete_success() {
        // given
        User user = User.builder().id(userId).build();
        Job job = Job.builder().id(jobId).title("직무").companyName("회사").build();
        JobBookmark bookmark = JobBookmark.builder()
                .id(bookmarkId)
                .user(user)
                .job(job)
                .build();

        when(jobBookmarkRepository.findByUserIdAndJobId(userId, jobId))
                .thenReturn(Optional.of(bookmark));

        // when
        assertDoesNotThrow(() -> service.delete(userId, jobId));

        // then
        verify(jobBookmarkRepository).findByUserIdAndJobId(userId, jobId);
        verify(jobBookmarkRepository).delete(bookmark);
    }

    @Test
    @DisplayName("delete: 북마크가 존재하지 않으면 JobBookmarkErrorStatus.JOB_BOOKMARK_NOT_FOUND 예외 발생")
    void delete_bookmarkNotFound_throwsException() {
        when(jobBookmarkRepository.findByUserIdAndJobId(userId, jobId))
                .thenReturn(Optional.empty());

        RestApiException exception = assertThrows(RestApiException.class, 
            () -> service.delete(userId, jobId));

        assertEquals(JobBookmarkErrorStatus.JOB_BOOKMARK_NOT_FOUND, exception.getErrorCode().getCode());
        verify(jobBookmarkRepository, never()).delete(any());
    }

    @Test
    @DisplayName("save: 북마크 저장 중 예외 발생 시 적절한 예외 전파")
    void save_repositoryException_propagatesException() {
        User user = User.builder().id(userId).build();
        Job job = Job.builder().id(jobId).title("직무").companyName("회사").build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(jobBookmarkRepository.existsByUserIdAndJobId(userId, jobId)).thenReturn(false);
        when(jobBookmarkRepository.save(any(JobBookmark.class)))
                .thenThrow(new RuntimeException("DB 저장 실패"));

        assertThrows(RuntimeException.class, () -> service.save(userId, jobId));
    }

    @Test
    @DisplayName("delete: 북마크 삭제 중 예외 발생 시 적절한 예외 전파")
    void delete_repositoryException_propagatesException() {
        User user = User.builder().id(userId).build();
        Job job = Job.builder().id(jobId).title("직무").companyName("회사").build();
        JobBookmark bookmark = JobBookmark.builder()
                .id(bookmarkId)
                .user(user)
                .job(job)
                .build();

        when(jobBookmarkRepository.findByUserIdAndJobId(userId, jobId))
                .thenReturn(Optional.of(bookmark));
        doThrow(new RuntimeException("DB 삭제 실패"))
                .when(jobBookmarkRepository).delete(bookmark);

        assertThrows(RuntimeException.class, () -> service.delete(userId, jobId));
    }
}




