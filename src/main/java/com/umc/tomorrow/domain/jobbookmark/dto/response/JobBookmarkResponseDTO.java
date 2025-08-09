/**
 * JobBookmarkResponseDTO
 * - JobBookmark에 대한 응답 정보를 담는 DTO (Data Transfer Object).
 * - 클라이언트에게 북마크된 정보와 함께 직무 관련 세부 정보를 제공합니다.
 작성자 : 정여진
 작성일 : 2025-08-05
 */
package com.umc.tomorrow.domain.jobbookmark.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JobBookmarkResponseDTO {
    private Long id;
    private Long jobId;
    private String jobTitle;
    private String companyName;
    private String bookmarkedAt;
}
