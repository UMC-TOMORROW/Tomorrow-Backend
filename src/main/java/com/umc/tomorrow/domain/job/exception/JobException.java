/**
 * 일자리(job)예외처리 클래스
 * 작성자: 이승주
 * 생성일: 2025-07-24
 */
package com.umc.tomorrow.domain.job.exception;
import com.umc.tomorrow.domain.job.exception.code.JobErrorStatus;
import com.umc.tomorrow.global.common.exception.RestApiException;

public class JobException extends RestApiException {
    public JobException(JobErrorStatus status) {
        super(status);
    }
}