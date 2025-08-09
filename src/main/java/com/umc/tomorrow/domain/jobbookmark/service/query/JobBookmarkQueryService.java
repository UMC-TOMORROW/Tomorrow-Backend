/**
 * JobBookmarkQueryServiceImpl
 * - JobBookmark 조회(Read) 로직을 처리하는 서비스 구현체
 * - 사용자의 북마크 목록을 가져옴
 * 작성자 : 정여진
 * 작성일 : 2025-08-05
 */
package com.umc.tomorrow.domain.jobbookmark.service.query;

import com.umc.tomorrow.domain.jobbookmark.dto.response.GetJobBookmarkListResponseDTO;

public interface JobBookmarkQueryService {
    GetJobBookmarkListResponseDTO getList(Long userId, Long cursor, int size);
}