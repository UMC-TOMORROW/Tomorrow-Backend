package com.umc.tomorrow.domain.introduction.exception;

import com.umc.tomorrow.domain.introduction.exception.code.IntroductionStatus;
import com.umc.tomorrow.global.common.exception.RestApiException;

public class IntroductionException extends RestApiException {

    public IntroductionException (IntroductionStatus status) {
        super(status);
    }
}