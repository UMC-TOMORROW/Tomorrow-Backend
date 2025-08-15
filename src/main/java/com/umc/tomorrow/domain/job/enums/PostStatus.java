/**
 * 공고 상태 Enum (모집중, 모집완료)
 * 작성자: 정여진
 * 생성일: 2025-07-25
 * 수정일 : 2025-08-05
 */
package com.umc.tomorrow.domain.job.enums;

import com.umc.tomorrow.domain.job.exception.code.JobErrorStatus;
import com.umc.tomorrow.global.common.exception.RestApiException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PostStatus {
    OPEN("모집중"),   // 모집중
    CLOSED("모집완료"); // 모집완료

    private final String displayValue;

    public static PostStatus from(String status) {
        return switch (status.toLowerCase()) {
            case "open", "모집중" -> OPEN;
            case "closed", "모집완료" -> CLOSED;
            default -> throw new RestApiException(JobErrorStatus.JOB_NOT_FOUND);
        };
    }

}