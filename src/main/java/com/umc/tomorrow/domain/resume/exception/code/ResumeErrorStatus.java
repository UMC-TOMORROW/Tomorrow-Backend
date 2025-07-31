/**
 * 이력서에 대한 예외처리코드
 * 작성자: 이승주
 * 생성일: 2025-07-24
 */
package com.umc.tomorrow.domain.resume.exception.code;

import com.umc.tomorrow.global.common.exception.code.BaseCode;
import com.umc.tomorrow.global.common.exception.code.BaseCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ResumeErrorStatus implements BaseCodeInterface {

    RESUME_NOT_FOUND(HttpStatus.NOT_FOUND, "RESUME404", "이력서를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final boolean isSuccess = false;
    private final String code;
    private final String message;

    @Override
    public BaseCode getCode() {
        return BaseCode.builder()
                .httpStatus(httpStatus)
                .isSuccess(isSuccess)
                .code(code)
                .message(message)
                .build();
    }
}