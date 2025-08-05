/**
 * GetJobBookmarkListResponseDTO
 * - 여러 개의 JobBookmark 정보를 담아 반환 DTO
 작성자: 정여진
 작성일: 2025-08-05
 */
package com.umc.tomorrow.domain.jobbookmark.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetJobBookmarkListResponseDTO {
    private List<JobBookmarkResponseDTO> bookmarks;
    private Boolean hasNext;
    private Long lastCursor; // 커서 기반 페이지네이션을 위해
}
