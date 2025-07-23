package com.umc.tomorrow.domain.member.exception;

import com.umc.tomorrow.global.common.exception.code.BaseCode;
import com.umc.tomorrow.global.common.exception.code.BaseCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberStatus implements BaseCodeInterface {

    // 200번대 성공
    MEMBER_DEACTIVATED(HttpStatus.OK, true, "MEMBER2002", "회원 탈퇴가 완료되었습니다."),
    MEMBER_RECOVERED(HttpStatus.OK, true, "MEMBER2003", "회원 복구가 완료되었습니다."),

    // 400번대 실패
    INVALID_RECOVERY_PERIOD(HttpStatus.BAD_REQUEST, false, "MEMBER4001", "복구 가능 기간이 지났습니다."),
    NOT_DELETED_USER(HttpStatus.BAD_REQUEST, false, "MEMBER4002", "탈퇴한 회원이 아닙니다.");

    private final HttpStatus httpStatus;
    private final boolean isSuccess;
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