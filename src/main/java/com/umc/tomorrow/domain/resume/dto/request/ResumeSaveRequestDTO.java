/**
 * 이력서 저장 요청 DTO
 * - 이력서 저장 API의 요청 구조 정의
 * 작성자: 정여진
 * 생성일: 2025-07-20
 */
package com.umc.tomorrow.domain.resume.dto.request;

import com.umc.tomorrow.domain.career.entity.Career;
import com.umc.tomorrow.domain.career.enums.WorkPeriodType;
import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class ResumeSaveRequestDTO {

    private final String introduction; // 자기소개서 내용

    private final List<Career> career; // 경력 목록

    private final List<String> certificates; // 자격증 목록

    /**
     * 경력 저장 요청 내부 클래스
     */
    @Getter
    @Builder
    public static class CareerSaveRequest {
        private final String company;
        private final String description;
        private final int workedYear;
        private WorkPeriodType workedPeriod;
    }
} 