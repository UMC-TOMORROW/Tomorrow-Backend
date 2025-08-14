/**
 * PreferenceException
 * 작성자: 정여진
 * 생성일: 2025-07-17
 */
package com.umc.tomorrow.domain.preferences.exception;

import com.umc.tomorrow.domain.preferences.exception.code.PreferenceErrorStatus;
import com.umc.tomorrow.global.common.exception.RestApiException;

public class PreferenceException extends RestApiException {
    public PreferenceException(PreferenceErrorStatus status) {
        super(status);
    }
    
    public PreferenceException(PreferenceErrorStatus status, String customMessage) {
        super(status, customMessage);
    }
}
