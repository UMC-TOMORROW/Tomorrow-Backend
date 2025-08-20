/**
 * 직업job 에 대한 예외처리
 * 작성자: 정여진
 * 생성일: 2025-07-23
 */
package com.umc.tomorrow.domain.job.exception.code;

import com.umc.tomorrow.global.common.exception.code.BaseCode;
import com.umc.tomorrow.global.common.exception.code.BaseCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum JobErrorStatus implements BaseCodeInterface {

    JOB_NOT_FOUND(HttpStatus.NOT_FOUND, "JOB404", "공고를 찾을 수 없습니다."),
    JOB_ALREADY_CLOSED(HttpStatus.BAD_REQUEST, "JOB401", "마감된 공고입니다."),
    JOB_ALREADY_OPEN(HttpStatus.BAD_REQUEST, "JOB401", "아직 마감 안 된 공고입니다."),
    POST_STATUS_INVALID(HttpStatus.BAD_REQUEST, "JOB400", "잘못된 공고 상태입니다."),
    JOB_FORBIDDEN(HttpStatus.FORBIDDEN, "JOB403", "공고에 대한 권한이 없습니다."),
    JOB_DATA_NOT_FOUND(HttpStatus.BAD_REQUEST, "JOB405", "일자리 정보 등록 데이터가 존재하지 않습니다."),
    INVALID_REGISTRANT_TYPE(HttpStatus.BAD_REQUEST, "JOB406", "등록 유형에 맞지 않는 접근 입니다."),
    BUSINESS_NOT_FOUND(HttpStatus.BAD_REQUEST, "JOB407", "저장된 사업자가 존재하지 않습니다.");

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