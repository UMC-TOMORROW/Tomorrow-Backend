/**
 * 지원서 상태 업데이트 응답 DTO
 * - 지원자 합격/불합격 처리 응답
 *
 * 작성자: 정여진
 * 생성일: 2025-07-16
 */
package com.umc.tomorrow.domain.application.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateApplicationStatusResponseDTO {
    
    private Long applicationId;
    private String status;
} 