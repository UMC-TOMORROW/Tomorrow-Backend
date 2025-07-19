/**
 * 이력서 요약 응답 DTO
 * - 이력서 요약 API의 응답 구조 정의
 * 작성자: 정여진
 * 생성일: 2025-07-20
 */
package com.umc.tomorrow.domain.resume.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class ResumeSummaryResponseDTO {

    private final String introduction; // 자기소개서 내용

    private final List<ExperienceSummary> experiences; // 경력 목록

    private final List<String> certificates; // 자격증 목록

    /**
     * 경력 요약 내부 클래스
     */
    @Getter
    @Builder
    public static class ExperienceSummary {
        private final String place;
        private final String task;
        private final int year;
        private final String duration;
    }
} 