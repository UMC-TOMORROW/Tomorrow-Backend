package com.umc.tomorrow.domain.preferences.exception;

import com.umc.tomorrow.domain.preferences.exception.code.PreferenceErrorStatus;
import com.umc.tomorrow.global.common.exception.RestApiException;

public class PreferenceException extends RestApiException {
    public PreferenceException(PreferenceErrorStatus status) {
        super(status);
    }
}
