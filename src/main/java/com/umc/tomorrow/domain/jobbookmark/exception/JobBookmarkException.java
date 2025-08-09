/**
 *  찜 커스텀 예외클래스
 * 작성자: 정여진
 * 작성일: 2025-08-05
 */
package com.umc.tomorrow.domain.jobbookmark.exception;

import com.umc.tomorrow.domain.jobbookmark.exception.code.JobBookmarkErrorStatus;
import com.umc.tomorrow.global.common.exception.RestApiException;

public class JobBookmarkException extends RestApiException {
    public JobBookmarkException(JobBookmarkErrorStatus status){
        super(status);
    }
}
