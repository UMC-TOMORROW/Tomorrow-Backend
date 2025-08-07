/**
 * JobBookmarkCommandService
 * - JobBookmark CRD 기능을 정의하는 서비스 인터페이스
 * - 데이터 변경(쓰기)과 관련된 메서드 명세
 작성자 : 정여진
 작성일 : 2025-08-05
 */
package com.umc.tomorrow.domain.jobbookmark.service.command;

import com.umc.tomorrow.domain.jobbookmark.dto.response.JobBookmarkResponseDTO;

public interface JobBookmarkCommandService {
    JobBookmarkResponseDTO save(Long userId, Long jobId);
    void delete(Long userId, Long jobId);
}