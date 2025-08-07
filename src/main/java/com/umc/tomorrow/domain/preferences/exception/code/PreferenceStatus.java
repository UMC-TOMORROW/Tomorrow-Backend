/**
 * 온보딩에 대한 예외처리코드
 * 작성자: 정여진
 * 생성일: 2025-08-07
 */
package com.umc.tomorrow.domain.preferences.exception.code;

import com.umc.tomorrow.global.common.exception.code.BaseCode;
import com.umc.tomorrow.global.common.exception.code.BaseCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PreferenceStatus implements BaseCodeInterface { // class -> enum으로 변경

    // 400번대 실패
    PREFERENCE_NOT_FOUND(HttpStatus.NOT_FOUND,"PREFERENCE4001", "희망 조건(Preference)을 찾을 수 없습니다."),
    PREFERENCE_ALREADY_EXISTS(HttpStatus.CONFLICT,"PREFERENCE4002", "이미 희망 조건이 설정되었습니다."),
    INVALID_PREFERENCE_TYPE(HttpStatus.BAD_REQUEST,"PREFERENCE4003", "유효하지 않은 희망 조건 유형입니다.");

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
