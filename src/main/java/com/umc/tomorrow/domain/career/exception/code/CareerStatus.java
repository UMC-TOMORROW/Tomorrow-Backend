package com.umc.tomorrow.domain.career.exception.code;

import com.umc.tomorrow.global.common.exception.code.BaseCode;
import com.umc.tomorrow.global.common.exception.code.BaseCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CareerStatus implements BaseCodeInterface {
    CAREER_NOT_FOUND(HttpStatus.NOT_FOUND, "CAREER404", "해당 경력 페이지를 찾을 수 없습니다."),
    CAREER_DELETE_NOT_FOUND(HttpStatus.NOT_FOUND, "CAREER405", "삭제할 경력이 존재하지 않습니다."),
    CAREER_FORBIDDEN(HttpStatus.FORBIDDEN, "CAREER403", "해당 경력 페이지에 대한 권한이 없습니다.");



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
