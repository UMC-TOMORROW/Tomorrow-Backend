/**
 * ReviewException
 * 작성자: 정여진
 * 생성일: 2025-07-11
 */
package com.umc.tomorrow.domain.review.exception;

import com.umc.tomorrow.domain.review.exception.code.ReviewErrorStatus;
import com.umc.tomorrow.global.common.exception.RestApiException;

public class ReviewException extends RestApiException {
    public ReviewException(ReviewErrorStatus status) {
        super(status);
    }
    
    public ReviewException(ReviewErrorStatus status, String customMessage) {
        super(status, customMessage);
    }
}
