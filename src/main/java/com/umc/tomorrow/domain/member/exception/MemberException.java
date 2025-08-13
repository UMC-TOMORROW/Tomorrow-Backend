package com.umc.tomorrow.domain.member.exception;

import com.umc.tomorrow.domain.member.exception.code.MemberErrorStatus;
import com.umc.tomorrow.global.common.exception.RestApiException;

public class MemberException extends RestApiException {
    public MemberException(MemberErrorStatus status) {
        super(status);
    }
}