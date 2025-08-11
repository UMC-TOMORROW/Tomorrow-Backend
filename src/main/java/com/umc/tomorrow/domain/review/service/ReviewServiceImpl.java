/**
 * ReviewServiceImpl
 * - 후기 저장 비즈니스 로직 구현체
 *
 * 작성자: 정여진
 * 생성일: 2025-07-17
 */
package com.umc.tomorrow.domain.review.service;

import com.umc.tomorrow.domain.application.entity.Application;
import com.umc.tomorrow.domain.application.repository.ApplicationRepository;
import com.umc.tomorrow.domain.job.entity.Job;
import com.umc.tomorrow.domain.job.exception.JobException;
import com.umc.tomorrow.domain.job.exception.code.JobErrorStatus;
import com.umc.tomorrow.domain.job.repository.JobRepository;
import com.umc.tomorrow.domain.resume.exception.ResumeException;
import com.umc.tomorrow.domain.review.dto.ReviewRequestDTO;
import com.umc.tomorrow.domain.review.dto.ReviewResponseDTO;
import com.umc.tomorrow.domain.review.entity.Review;
import com.umc.tomorrow.domain.review.repository.ReviewRepository;
import com.umc.tomorrow.domain.member.entity.User;
import com.umc.tomorrow.domain.member.repository.UserRepository;
import com.umc.tomorrow.global.common.exception.RestApiException;
import com.umc.tomorrow.global.common.exception.code.GlobalErrorStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;

    private final JobRepository jobRepository;

    @Autowired
    public ReviewServiceImpl(ReviewRepository reviewRepository, UserRepository userRepository,  ApplicationRepository applicationRepository,JobRepository jobRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.applicationRepository = applicationRepository;
        this.jobRepository = jobRepository;
    }

    /**
     * 후기 저장
     * @param userId 회원 ID
     * @param dto 후기 요청 DTO
     */
    @Transactional
    @Override
    public void saveReview(Long userId, ReviewRequestDTO dto) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RestApiException(GlobalErrorStatus._NOT_FOUND));

        Job post = jobRepository.findById(dto.getPostId())
                .orElseThrow(()-> new JobException(JobErrorStatus.JOB_NOT_FOUND));

        Review review = Review.builder()
                .job(post)
                .stars(dto.getStars())
                .review(dto.getReview())
                .user(user)
                .build();
        reviewRepository.save(review);
    }

    /**
     * 후기 조회
     * @param userId 회원 ID
     * @param postId 일자리 ID
     */
    @Override
    public List<ReviewResponseDTO> getReviewsByPostId(Long postId, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        List<Review> reviews = reviewRepository.findByJobId(postId);

        return reviews.stream()
                .map(r -> new ReviewResponseDTO(r.getStars(), r.getReview(), r.getCreatedAt()))
                .toList();
    }
}