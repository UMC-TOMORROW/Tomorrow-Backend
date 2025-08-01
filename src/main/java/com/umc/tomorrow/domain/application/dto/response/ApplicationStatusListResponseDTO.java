/**
 * 지원서 상태 업데이트 응답 DTO
 * - 지원자 합격/불합격 처리 응답
 *
 * 작성자: 정여진
 * 생성일: 2025-07-24
 */
package com.umc.tomorrow.domain.application.dto.response;

import com.umc.tomorrow.domain.application.enums.ApplicationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApplicationStatusListResponseDTO {
    private String postTitle;
    private String company;
    private String date;

    @NotNull(message = "{application.status.notblank}")
    private ApplicationStatus status; // "합격", "불합격" (default = 불합격)
}
