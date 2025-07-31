package com.umc.tomorrow.domain.auth.exception.code;

import com.umc.tomorrow.global.common.exception.code.BaseCode;
import com.umc.tomorrow.global.common.exception.code.BaseCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorStatus implements BaseCodeInterface {

    REFRESH_TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "AUTH4001", "리프레시 토큰이 존재하지 않습니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH4002", "리프레시 토큰이 만료되었습니다. 다시 로그인해주세요."),
    REFRESH_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "AUTH4003", "유효하지 않은 리프레시 토큰입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public BaseCode getCode() {
        return BaseCode.builder()
                .httpStatus(httpStatus)
                .code(code)
                .message(message)
                .build();
    }
}