/**
 * 리뷰 도메인 예외 상태 코드
 * 작성자: 정여진
 * 생성일: 2025-07-11
 */
package com.umc.tomorrow.domain.review.exception.code;

import com.umc.tomorrow.global.common.exception.code.BaseCode;
import com.umc.tomorrow.global.common.exception.code.BaseCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ReviewErrorStatus implements BaseCodeInterface {

    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "REVIEW404", "리뷰를 찾을 수 없습니다."),
    INVALID_STARS_RANGE(HttpStatus.BAD_REQUEST, "REVIEW4001", "별점은 0-5 사이여야 합니다."),
    JOB_NOT_FOUND(HttpStatus.NOT_FOUND, "REVIEW4002", "해당 공고를 찾을 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "REVIEW4003", "사용자를 찾을 수 없습니다."),
    APPLICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "REVIEW4004", "지원 내역을 찾을 수 없습니다.");

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
