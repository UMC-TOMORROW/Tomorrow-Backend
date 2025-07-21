/**
 * 지원서 상태 업데이트 요청 DTO
 * - 지원자 합격/불합격 처리 요청
 *
 * 작성자: 정여진
 * 생성일: 2025-07-16
 */
package com.umc.tomorrow.domain.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateApplicationStatusRequestDTO {
    
    @NotBlank(message = "상태는 필수입니다.")
    @Pattern(regexp = "^(합격|불합격)$", message = "상태는 '합격' 또는 '불합격'이어야 합니다.")
    private String status;
} 