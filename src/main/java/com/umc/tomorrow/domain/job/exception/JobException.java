/**
 * 공고 도메인 예외 클래스
 * 작성자: 정여진
 * 생성일: 2025-07-25
 */
package com.umc.tomorrow.domain.job.exception;

import com.umc.tomorrow.domain.job.exception.code.JobErrorStatus;
import com.umc.tomorrow.global.common.exception.RestApiException;

public class JobException extends RestApiException {
    public JobException(JobErrorStatus status) {
        super(status);
    }
}