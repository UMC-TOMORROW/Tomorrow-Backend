/**
 * 이메일 에러 상태코드
 * 작성자: 이승주
 * 작성일: 2025-07-27
 */
package com.umc.tomorrow.domain.email.exception.code;

import com.umc.tomorrow.global.common.exception.code.BaseCode;
import com.umc.tomorrow.global.common.exception.code.BaseCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum EmailErrorStatus implements BaseCodeInterface {

    INVALID_EMAIL_TYPE(HttpStatus.BAD_REQUEST, "EMAIL4001", "지원하지 않는 이메일 타입입니다."),
    INVALID_APPLICATION_STATUS(HttpStatus.BAD_REQUEST, "EMAIL4002", "현재 지원 상태로는 해당 이메일을 발송할 수 없습니다."),
    ALREADY_ACCEPTED(HttpStatus.BAD_REQUEST, "EMAIL4003", "이미 합격 처리된 지원서입니다."),
    ALREADY_REJECTED(HttpStatus.BAD_REQUEST, "EMAIL4004", "이미 불합격 처리된 지원서입니다."),
    APPLICATION_NOT_PENDING(HttpStatus.BAD_REQUEST, "EMAIL4005", "지원서가 아직 검토 대기 상태가 아닙니다."),
    EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "EMAIL5001", "메일 전송에 실패했습니다.");

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
