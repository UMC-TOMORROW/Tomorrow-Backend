/**
 * ReviewServiceImpl
 * - 후기 저장 비즈니스 로직 구현체
 *
 * 작성자: 정여진
 * 생성일: 2025-07-17
 */
package com.umc.tomorrow.domain.review.service;

import com.umc.tomorrow.domain.review.dto.ReviewRequestDTO;
import com.umc.tomorrow.domain.review.entity.Review;
import com.umc.tomorrow.domain.review.repository.ReviewRepository;
import com.umc.tomorrow.domain.member.entity.User;
import com.umc.tomorrow.domain.member.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    @Autowired
    public ReviewServiceImpl(ReviewRepository reviewRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
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
            .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        Review review = Review.builder()
                .postId(dto.getPostId())
                .stars(dto.getStars())
                .review(dto.getReview())
                .user(user)
                .build();
        reviewRepository.save(review);
    }
}