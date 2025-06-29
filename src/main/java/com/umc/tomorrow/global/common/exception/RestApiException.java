package com.umc.tomorrow.global.common.exception;

import com.umc.tomorrow.global.common.exception.code.BaseCode;
import com.umc.tomorrow.global.common.exception.code.BaseCodeInterface;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RestApiException extends RuntimeException {

    private final BaseCodeInterface errorCode; //추상화 시킨 인터페이스를 받아서 사용

    //추상화 시킨 ErrorCode의 getCode()를 사용하여 ErrorCode를 반환
    public BaseCode getErrorCode() {
        return this.errorCode.getCode();
    }
}
