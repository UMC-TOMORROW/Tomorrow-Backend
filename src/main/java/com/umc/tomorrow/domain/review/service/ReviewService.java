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
     * 특정 공고에 대한 리뷰 목록 조회
     * @param userId 사용자 ID
     * @param jobId 공고 ID
     * @return 리뷰 목록
     */
    List<ReviewResponseDTO> getReviewsByJobId(Long userId, Long jobId);
}
