//package com.umc.tomorrow.domain.review.service;
//
//import com.umc.tomorrow.domain.job.entity.Job;
//import com.umc.tomorrow.domain.job.exception.JobException;
//import com.umc.tomorrow.domain.job.exception.code.JobErrorStatus;
//import com.umc.tomorrow.domain.job.repository.JobRepository;
//import com.umc.tomorrow.domain.member.entity.User;
//import com.umc.tomorrow.domain.member.repository.UserRepository;
//import com.umc.tomorrow.domain.review.dto.ReviewResponseDTO;
//import com.umc.tomorrow.domain.review.entity.Review;
//import com.umc.tomorrow.domain.review.repository.ReviewRepository;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.Mockito.when;
//
//@DisplayName("ReviewService 후기 조회 테스트")
//@ExtendWith(MockitoExtension.class)
//class ReviewServiceImplTest {
//
//    @InjectMocks
//    private ReviewServiceImpl reviewService;
//
//    @Mock
//    private ReviewRepository reviewRepository;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private JobRepository jobRepository;
//
//    @Test
//    @DisplayName("후기 조회 성공: 해당 게시물의 리뷰 리스트 반환")
//    void getReviewsByPostId_성공() {
//        // given
//        Long userId = 1L;
//        Long postId = 10L;
//
//        User mockUser = User.builder()
//                .id(userId)
//                .username("testUser")
//                .build();
//
//        Job mockJob = Job.builder().id(postId).build();
//
//        Review review1 = Review.builder()
//                .stars(5)
//                .review("좋아요")
//                .user(mockUser)
//                .job(mockJob)
//                .build();
//
//        Review review2 = Review.builder()
//                .stars(4)
//                .review("괜찮아요")
//                .user(mockUser)
//                .job(mockJob)
//                .build();
//
//        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
//        when(jobRepository.findById(postId)).thenReturn(Optional.of(mockJob));
//        when(reviewRepository.findByJobId(postId)).thenReturn(List.of(review1, review2));
//
//        // when
//        List<ReviewResponseDTO> result = reviewService.getReviewsById(postId, userId);
//
//        // then
//        assertAll(
//                () -> assertNotNull(result),
//                () -> assertEquals(2, result.size()),
//                () -> assertEquals(5, result.get(0).getStars()),
//                () -> assertEquals("좋아요", result.get(0).getReview()),
//                () -> assertEquals(4, result.get(1).getStars())
//        );
//    }
//
//    @Test
//    @DisplayName("후기 조회 실패: 존재하지 않는 회원")
//    void getReviewsByPostId_회원없음_실패() {
//        // when & then //실패는 when, then 한번에 처리
//        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
//
//        assertThrows(IllegalArgumentException.class,
//                () -> reviewService.getReviewsByPostId(10L, 1L));
//    }
//
//    @Test
//    @DisplayName("후기 조회 실패: 존재하지 않는 Job")
//    void getReviewsByPostId_Job없음_실패() {
//        // given
//        Long userId = 1L;
//        Long postId = 10L;
//        User mockUser = User.builder()
//                .id(userId)
//                .username("testUser")
//                .build();
//
//        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
//        when(jobRepository.findById(postId)).thenReturn(Optional.empty());
//
//        // when & then
//        JobException exception = assertThrows(JobException.class,
//                () -> reviewService.getReviewsByPostId(postId, userId));
//
//        assertEquals(JobErrorStatus.JOB_NOT_FOUND.getCode().getCode(), exception.getErrorCode().getCode());
//    }
//
//
//}
