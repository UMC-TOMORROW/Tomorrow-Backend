/**
 * 이메일커스텀예외클래스
 * 작성자: 이승주
 * 작성일: 2025-07-28
 */
package com.umc.tomorrow.domain.email.exception;

import com.umc.tomorrow.domain.email.exception.code.EmailErrorStatus;
import com.umc.tomorrow.global.common.exception.RestApiException;

public class EmailException extends RestApiException {
    public EmailException(EmailErrorStatus status) {
        super(status);
    }
}
