/**
 * 자격증 관련 에러 상태 코드
 * 작성자: 이승주
 * 작성일: 2025-07-28
 */
package com.umc.tomorrow.domain.certificate.exception.code;

import com.umc.tomorrow.global.common.exception.code.BaseCode;
import com.umc.tomorrow.global.common.exception.code.BaseCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CertificateErrorStatus implements BaseCodeInterface {

    CERTIFICATE_NOT_FOUND(HttpStatus.NOT_FOUND, "CERTIFICATE404", "해당 자격증을 찾을 수 없습니다."),
    CERTIFICATE_FORBIDDEN(HttpStatus.FORBIDDEN, "CERTIFICATE403", "해당 자격증에 접근할 권한이 없습니다.");
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
