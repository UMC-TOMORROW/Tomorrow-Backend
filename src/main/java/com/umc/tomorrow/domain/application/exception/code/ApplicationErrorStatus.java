/**
 * 지원자 상태 에 대한 예외처리
 * 작성자: 정여진
 * 생성일: 2025-07-23
 */
package com.umc.tomorrow.domain.application.exception.code;

import com.umc.tomorrow.global.common.exception.code.BaseCode;
import com.umc.tomorrow.global.common.exception.code.BaseCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ApplicationErrorStatus implements BaseCodeInterface {

    APPLICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "APPLICATION401", "지원서를 찾을 수 없습니다."),
    APPLICATION_ALREADY_PROCESSED(HttpStatus.BAD_REQUEST, "APPLICATION402", "이미 처리된 지원서입니다."),
    APPLICATION_JOB_MISMATCH(HttpStatus.BAD_REQUEST, "APPLICATION403", "해당 공고의 지원서가 아닙니다."),
    INVALID_STATUS(HttpStatus.BAD_REQUEST, "APPLICATION404", "지원서 상태는 '합격' 또는 '불합격'이어야 합니다."),
    APPLICATION_DUPLICATED(HttpStatus.BAD_REQUEST, "APPLICATION405", "이미 해당 공고에 지원했습니다."),
    APPLICANTS_NOT_FOUND(HttpStatus.NOT_FOUND, "APPLICATION406", "해당 공고의 지원자를 찾을 수 없습니다."),
    JOB_NOT_FOUND(HttpStatus.NOT_FOUND, "APPLICATION407", "해당 공고를 찾을 수 없습니다.");


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
