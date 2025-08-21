/**
 * 지원서 상태 업데이트 응답 DTO
 * - 지원자 합격/불합격 처리 응답
 *
 * 작성자: 정여진
 * 생성일: 2025-07-24
 */
package com.umc.tomorrow.domain.application.dto.response;

import com.umc.tomorrow.domain.application.enums.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApplicationStatusListResponseDTO {
    private String postTitle;
    private Long jobId;
    private String company;
    private String date;
    private String jobImageUrl;
    private WorkEnvironmentDTO jobWorkEnvironment;

    @NotNull(message = "지원 상태는 필수입니다.")
    private ApplicationStatus status;

    // static 클래스
    @Getter
    @Builder
    public static class WorkEnvironmentDTO {
        private boolean canCommunicate;
        private boolean canMoveActively;
        private boolean canWorkSitting;
        private boolean canWorkStanding;
        private boolean canCarryObjects;
    }
}
