package com.umc.tomorrow.domain.preferences.exception;

import com.umc.tomorrow.domain.resume.exception.code.ResumeErrorStatus;
import com.umc.tomorrow.global.common.exception.RestApiException;

public class PreferenceException extends RestApiException {
    public PreferenceException(ResumeErrorStatus status) {
        super(status);
    }
}
