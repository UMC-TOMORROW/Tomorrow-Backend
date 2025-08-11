/**
 * 채팅 관련 에러 코드
 * 작성자: 이승주
 * 생성일: 2025-08-06
 */
package com.umc.tomorrow.domain.chatting.exception.code;

import com.umc.tomorrow.global.common.exception.code.BaseCode;
import com.umc.tomorrow.global.common.exception.code.BaseCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ChatErrorStatus implements BaseCodeInterface{

    CHATROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "CHATROOM404", "해당 채팅방을 찾을 수 없습니다."),
    CHATPART_NOT_FOUND(HttpStatus.NOT_FOUND, "CHATPART404", "해당 채팅방에 참여하고 있지 않습니다."),
    MESSAGE_EMPTY(HttpStatus.BAD_REQUEST, "MESSAGE400", "메시지 내용이 비어있습니다.");
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
