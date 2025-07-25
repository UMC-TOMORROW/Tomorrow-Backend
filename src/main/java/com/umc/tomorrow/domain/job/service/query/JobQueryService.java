/**
 * 내 공고 조회 서비스 인터페이스
 * 작성자: 정여진
 * 생성일: 2025-07-25
 */
package com.umc.tomorrow.domain.job.service.query;

import com.umc.tomorrow.domain.job.dto.request.MyPostResponseDTO;

import java.util.List;

public interface JobQueryService {

    /**
     * 상태별 공고 조회 (모집중 / 모집완료)
     * @param userId 로그인한 사용자 ID
     * @param status "open" 또는 "closed"
     * @return 공고 응답 리스트
     */
    List<MyPostResponseDTO> getMyPosts(Long userId, String status);
}
