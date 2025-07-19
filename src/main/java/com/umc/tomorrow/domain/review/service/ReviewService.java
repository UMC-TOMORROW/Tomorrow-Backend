/**
 * ReviewService
 * - 후기 저장 비즈니스 로직 인터페이스
 *
 * 작성자: 정여진
 * 생성일: 2025-07-17
 */
package com.umc.tomorrow.domain.review.service;

import com.umc.tomorrow.domain.review.dto.ReviewRequestDTO;

public interface ReviewService {
    /**
     * 후기 저장
     * @param userId 회원 ID
     * @param dto 후기 요청 DTO
     */
    void saveReview(Long userId, ReviewRequestDTO dto);
}