package com.umc.tomorrow.domain.careertalk.exception.code;

import com.umc.tomorrow.global.common.exception.code.BaseCode;
import com.umc.tomorrow.global.common.exception.code.BaseCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CareertalkStatus implements BaseCodeInterface {

    CAREERTALK_NOT_FOUND(HttpStatus.NOT_FOUND, "CAREERTALK404", "해당 커리어톡 게시글을 찾을 수 없습니다."),
    CAREERTALK_FORBIDDEN(HttpStatus.FORBIDDEN, "CAREERTALK403", "해당 커리어톡 게시글에 대한 권한이 없습니다.");


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
