/**
 * 커리어톡 에 대한 예외처리 클래스
 * 작성자: 이승주
 * 생성일: 2025-07-24
 */
package com.umc.tomorrow.domain.careertalk.exception;

import com.umc.tomorrow.domain.careertalk.exception.code.CareertalkErrorStatus;
import com.umc.tomorrow.global.common.exception.RestApiException;

public class CareertalkException extends RestApiException {

    public CareertalkException(CareertalkErrorStatus status) {
        super(status);
    }
}
