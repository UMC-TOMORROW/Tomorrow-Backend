package com.umc.tomorrow.domain.careertalk.exception;

import com.umc.tomorrow.domain.careertalk.exception.code.CareertalkStatus;
import com.umc.tomorrow.global.common.exception.RestApiException;

public class CareertalkException extends RestApiException {

    public CareertalkException(CareertalkStatus status) {
        super(status);
    }
}
