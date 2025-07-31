/**
 * 이력서에 대한 에러상태코드
 * 작성자: 이승주
 * 생성일: 2025-07-24
 */
package com.umc.tomorrow.domain.resume.exception;

import com.umc.tomorrow.domain.resume.exception.code.ResumeErrorStatus;
import com.umc.tomorrow.global.common.exception.RestApiException;

public class ResumeException extends RestApiException {
    public ResumeException(ResumeErrorStatus status) {
        super(status);
    }
}
