package com.umc.tomorrow.domain.review.service;

import com.umc.tomorrow.domain.application.entity.Application;
import com.umc.tomorrow.domain.application.repository.ApplicationRepository;
import com.umc.tomorrow.domain.job.entity.Job;
import com.umc.tomorrow.domain.job.exception.JobException;
import com.umc.tomorrow.domain.job.exception.code.JobErrorStatus;
import com.umc.tomorrow.domain.job.repository.JobRepository;
import com.umc.tomorrow.domain.member.entity.User;
import com.umc.tomorrow.domain.member.repository.UserRepository;
import com.umc.tomorrow.domain.review.dto.ReviewRequestDTO;
import com.umc.tomorrow.domain.review.dto.ReviewResponseDTO;
import com.umc.tomorrow.domain.review.entity.Review;
import com.umc.tomorrow.domain.review.exception.ReviewException;
import com.umc.tomorrow.domain.review.exception.code.ReviewErrorStatus;
import com.umc.tomorrow.domain.review.repository.ReviewRepository;
import com.umc.tomorrow.global.common.exception.RestApiException;
import com.umc.tomorrow.global.common.exception.code.GlobalErrorStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("ReviewServiceImpl 후기 저장/조회 단위 테스트")
@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @InjectMocks
    private ReviewServiceImpl service;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private JobRepository jobRepository;

    // ===== saveReview =====
    @Test
    @DisplayName("후기 저장 성공: 정상적인 별점과 내용으로 후기가 저장됨")
    void saveReview_성공() {
        // given
        Long userId = 1L;
        Long jobId = 10L;
        
        ReviewRequestDTO dto = ReviewRequestDTO.builder()
                .jobId(jobId)
                .stars(4)
                .review("정말 좋은 일자리였습니다!")
                .build();
        
        User user = User.builder().id(userId).build();
        Job job = Job.builder().id(jobId).title("편의점 알바").build();
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(reviewRepository.save(any(Review.class))).thenReturn(
                Review.builder().id(1L).stars(4).review("정말 좋은 일자리였습니다!").build()
        );
        
        // when
        assertDoesNotThrow(() -> service.saveReview(userId, dto));
        
        // then
        verify(userRepository).findById(userId);
        verify(jobRepository).findById(jobId);
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    @DisplayName("후기 저장 실패: 별점이 0보다 작음 -> INVALID_STARS_RANGE")
    void saveReview_실패_별점음수() {
        // given
        Long userId = 1L;
        Long jobId = 10L;
        
        ReviewRequestDTO dto = ReviewRequestDTO.builder()
                .jobId(jobId)
                .stars(-1)
                .review("테스트 리뷰")
                .build();
        
        // when & then
        ReviewException exception = assertThrows(ReviewException.class, 
                () -> service.saveReview(userId, dto));
        
        assertEquals(ReviewErrorStatus.INVALID_STARS_RANGE.getCode(), exception.getErrorCode().getCode());
        verify(userRepository, never()).findById(any());
        verify(jobRepository, never()).findById(any());
        verify(reviewRepository, never()).save(any());
    }

    @Test
    @DisplayName("후기 저장 실패: 별점이 5보다 큼 -> INVALID_STARS_RANGE")
    void saveReview_실패_별점초과() {
        // given
        Long userId = 1L;
        Long jobId = 10L;
        
        ReviewRequestDTO dto = ReviewRequestDTO.builder()
                .jobId(jobId)
                .stars(6)
                .review("테스트 리뷰")
                .build();
        
        // when & then
        ReviewException exception = assertThrows(ReviewException.class, 
                () -> service.saveReview(userId, dto));
        
        assertEquals(ReviewErrorStatus.INVALID_STARS_RANGE.getCode(), exception.getErrorCode().getCode());
        verify(userRepository, never()).findById(any());
        verify(jobRepository, never()).findById(any());
        verify(reviewRepository, never()).save(any());
    }

    @Test
    @DisplayName("후기 저장 실패: 사용자를 찾을 수 없음 -> _NOT_FOUND")
    void saveReview_실패_사용자없음() {
        // given
        Long userId = 99L;
        Long jobId = 10L;
        
        ReviewRequestDTO dto = ReviewRequestDTO.builder()
                .jobId(jobId)
                .stars(3)
                .review("테스트 리뷰")
                .build();
        
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        
        // when & then
        RestApiException exception = assertThrows(RestApiException.class, 
                () -> service.saveReview(userId, dto));
        
        assertEquals(GlobalErrorStatus._NOT_FOUND.getCode(), exception.getErrorCode().getCode());
        verify(userRepository).findById(userId);
        verify(jobRepository, never()).findById(any());
        verify(reviewRepository, never()).save(any());
    }

    @Test
    @DisplayName("후기 저장 실패: 공고를 찾을 수 없음 -> JOB_NOT_FOUND")
    void saveReview_실패_공고없음() {
        // given
        Long userId = 1L;
        Long jobId = 99L;
        
        ReviewRequestDTO dto = ReviewRequestDTO.builder()
                .jobId(jobId)
                .stars(3)
                .review("테스트 리뷰")
                .build();
        
        User user = User.builder().id(userId).build();
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(jobRepository.findById(jobId)).thenReturn(Optional.empty());
        
        // when & then
        JobException exception = assertThrows(JobException.class, 
                () -> service.saveReview(userId, dto));
        
        assertEquals(JobErrorStatus.JOB_NOT_FOUND.getCode(), exception.getErrorCode().getCode());
        verify(userRepository).findById(userId);
        verify(jobRepository).findById(jobId);
        verify(reviewRepository, never()).save(any());
    }

    // ===== getReviewsByJobId =====
    @Test
    @DisplayName("리뷰 목록 조회 성공: 특정 공고의 리뷰들이 정상적으로 조회됨")
    void getReviewsByJobId_성공() {
        // given
        Long jobId = 10L;
        Long userId = 1L;
        
        Job job = Job.builder().id(jobId).title("편의점 알바").build();
        User user1 = User.builder().id(1L).name("사용자1").build();
        User user2 = User.builder().id(2L).name("사용자2").build();
        
        Review review1 = Review.builder()
                .id(1L)
                .stars(5)
                .review("정말 좋았습니다!")
                .user(user1)
                .build();
        
        Review review2 = Review.builder()
                .id(2L)
                .stars(4)
                .review("괜찮았습니다")
                .user(user2)
                .build();
        
        List<Review> reviews = Arrays.asList(review1, review2);
        
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(reviewRepository.findByJobId(jobId)).thenReturn(reviews);
        
        // when
        List<ReviewResponseDTO> result = service.getReviewsByJobId(jobId, userId);
        
        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(2, result.size()),
                () -> assertEquals(5, result.get(0).getStars()),
                () -> assertEquals("정말 좋았습니다!", result.get(0).getReview()),
                () -> assertEquals(4, result.get(1).getStars()),
                () -> assertEquals("괜찮았습니다", result.get(1).getReview())
        );
        
        verify(jobRepository).findById(jobId);
        verify(reviewRepository).findByJobId(jobId);
    }

    @Test
    @DisplayName("리뷰 목록 조회 실패: 공고를 찾을 수 없음 -> JOB_NOT_FOUND")
    void getReviewsByJobId_실패_공고없음() {
        // given
        Long jobId = 99L;
        Long userId = 1L;
        
        when(jobRepository.findById(jobId)).thenReturn(Optional.empty());
        
        // when & then
        JobException exception = assertThrows(JobException.class, 
                () -> service.getReviewsByJobId(jobId, userId));
        
        assertEquals(JobErrorStatus.JOB_NOT_FOUND.getCode(), exception.getErrorCode().getCode());
        verify(jobRepository).findById(jobId);
        verify(reviewRepository, never()).findByJobId(any());
    }

    @Test
    @DisplayName("리뷰 목록 조회 성공: 리뷰가 없는 경우 빈 리스트 반환")
    void getReviewsByJobId_성공_리뷰없음() {
        // given
        Long jobId = 10L;
        Long userId = 1L;
        
        Job job = Job.builder().id(jobId).title("편의점 알바").build();
        
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(reviewRepository.findByJobId(jobId)).thenReturn(Arrays.asList());
        
        // when
        List<ReviewResponseDTO> result = service.getReviewsByJobId(jobId, userId);
        
        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertTrue(result.isEmpty()),
                () -> assertEquals(0, result.size())
        );
        
        verify(jobRepository).findById(jobId);
        verify(reviewRepository).findByJobId(jobId);
    }

    @Test
    @DisplayName("후기 저장 성공: 별점 0으로 저장 가능")
    void saveReview_성공_별점0() {
        // given
        Long userId = 1L;
        Long jobId = 10L;
        
        ReviewRequestDTO dto = ReviewRequestDTO.builder()
                .jobId(jobId)
                .stars(0)
                .review("별점 없음")
                .build();
        
        User user = User.builder().id(userId).build();
        Job job = Job.builder().id(jobId).title("편의점 알바").build();
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(reviewRepository.save(any(Review.class))).thenReturn(
                Review.builder().id(1L).stars(0).review("별점 없음").build()
        );
        
        // when
        assertDoesNotThrow(() -> service.saveReview(userId, dto));
        
        // then
        verify(userRepository).findById(userId);
        verify(jobRepository).findById(jobId);
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    @DisplayName("후기 저장 성공: 별점 5로 저장 가능")
    void saveReview_성공_별점5() {
        // given
        Long userId = 1L;
        Long jobId = 10L;
        
        ReviewRequestDTO dto = ReviewRequestDTO.builder()
                .jobId(jobId)
                .stars(5)
                .review("완벽한 일자리!")
                .build();
        
        User user = User.builder().id(userId).build();
        Job job = Job.builder().id(jobId).title("편의점 알바").build();
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(reviewRepository.save(any(Review.class))).thenReturn(
                Review.builder().id(1L).stars(5).review("완벽한 일자리!").build()
        );
        
        // when
        assertDoesNotThrow(() -> service.saveReview(userId, dto));
        
        // then
        verify(userRepository).findById(userId);
        verify(jobRepository).findById(jobId);
        verify(reviewRepository).save(any(Review.class));
    }
}
