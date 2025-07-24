/**
 * ApplicationException
 * 작성자: 정여진
 * 생성일: 2025-07-24
 */
package com.umc.tomorrow.domain.application.exception;
import com.umc.tomorrow.domain.application.exception.code.ApplicationErrorStatus;
import com.umc.tomorrow.global.common.exception.RestApiException;

public class ApplicationException extends RestApiException {
    public ApplicationException(ApplicationErrorStatus status) {
        super(status);
    }
}