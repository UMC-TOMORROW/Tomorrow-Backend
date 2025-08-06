/**
 * 채팅방 관련 커스텀 예외 클래스
 * 작성자: 이승주
 * 작성일: 2025-08-06
 */
package com.umc.tomorrow.domain.chatting.exception;

import com.umc.tomorrow.domain.chatting.exception.code.ChatErrorStatus;
import com.umc.tomorrow.global.common.exception.RestApiException;

public class ChatException extends RestApiException {
    public ChatException(ChatErrorStatus status) {
        super(status);
    }
}
