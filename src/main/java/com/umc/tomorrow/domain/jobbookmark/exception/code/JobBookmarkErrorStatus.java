/**
 * 찜 에러 상태코드
 * 작성자: 정여진
 * 작성일: 2025-08-05
 */
package com.umc.tomorrow.domain.jobbookmark.exception.code;
import com.umc.tomorrow.global.common.exception.code.BaseCode;
import com.umc.tomorrow.global.common.exception.code.BaseCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum JobBookmarkErrorStatus implements BaseCodeInterface {
    JOB_BOOKMARK_NOT_FOUND(HttpStatus.NOT_FOUND, "BOOKMARK4001", "찜을 찾을 수 없습니다."),
    JOB_BOOKMARK_ALREADY_EXISTS(HttpStatus.CONFLICT, "BOOKMARK4002", "이미 찜한 공고입니다.");

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
