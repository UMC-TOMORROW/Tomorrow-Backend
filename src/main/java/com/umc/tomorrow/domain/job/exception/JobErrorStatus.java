/**
 * 직업job 에 대한 예외처리
 * 작성자: 정여진
 * 생성일: 2025-07-23
 */
package com.umc.tomorrow.domain.job.exception;

import com.umc.tomorrow.global.common.exception.code.BaseCode;
import com.umc.tomorrow.global.common.exception.code.BaseCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum JobErrorStatus implements BaseCodeInterface {

    JOB_NOT_FOUND(HttpStatus.NOT_FOUND, "JOB404", "공고를 찾을 수 없습니다."),
    JOB_ALREADY_CLOSED(HttpStatus.BAD_REQUEST, "JOB4001", "마감된 공고입니다.");

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