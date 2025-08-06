package com.umc.tomorrow.domain.review.service;

import com.umc.tomorrow.domain.review.dto.ReviewRequestDTO;
import com.umc.tomorrow.domain.review.dto.ReviewResponseDTO;

import java.util.List;

public interface ReviewService {

    /**
     * 후기 저장
     * @param userId 회원 ID
     * @param dto 후기 요청 DTO
     */
    void saveReview(Long userId, ReviewRequestDTO dto);

    /**
     * 특정 공고의 후기 목록 조회
     * @param postId 공고 ID
     * @param userId 로그인한 유저 ID
     */
    List<ReviewResponseDTO> getReviewsByPostId( Long userId, Long postId);
}
