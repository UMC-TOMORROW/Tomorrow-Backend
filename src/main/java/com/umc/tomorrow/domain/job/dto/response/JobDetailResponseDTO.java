package com.umc.tomorrow.domain.job.dto.response;

import com.umc.tomorrow.domain.job.dto.request.WorkDaysRequestDTO;
import com.umc.tomorrow.domain.job.dto.request.WorkEnvironmentRequestDTO;
import com.umc.tomorrow.domain.job.enums.JobCategory;
import com.umc.tomorrow.domain.job.enums.PaymentType;
import com.umc.tomorrow.domain.job.enums.WorkPeriod;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "일자리 상세 조회 응답 DTO")
public class JobDetailResponseDTO {

    @Schema(description = "제목", example = "편의점 알바 구인")
    private String title;

    @Schema(description = "설명", example = "알바 구인합니다.")
    private String jobDescription;

    @Schema(description = "근무기간",
            example = "OVER_ONE_YEAR")
    private WorkPeriod workPeriod;

    @Schema(description = "근무기간 협의", example = "true")
    private Boolean isPeriodNegotiable = false;

    @Schema(description = "근무 시작 시간", example = "12:00")
    private LocalTime workStart;

    @Schema(description = "근무 종료 시간", example = "17:00")
    private LocalTime workEnd;

    @Schema(description = "근무 시작 협의", example = "true")
    private Boolean isTimeNegotiable = false;

    @Schema(description = "급여 형태", example = "HOURLY")
    private PaymentType paymentType;

    @Schema(description = "일자리 카테고리", example = "TUTORING")
    private JobCategory jobCategory;

    @Schema(description = "급여",
            example = "12000")
    private Integer salary;

    @Schema(description = "시설 이미지", example = "...")
    private String jobImageUrl;

    @Schema(description = "회사명", example = "내일")
    private String companyName;

    @Schema(description = "공고 활성화 여부", example = "true")
    private Boolean isActive;

    @Schema(description = "모집인원", example = "2")
    private Integer recruitmentLimit;

    @Schema(description = "공고 마감일", example = "2025-08-01")
    private LocalDateTime deadline;

    @Schema(description = "우대사항", example = "대학생 우대")
    private String preferredQualifications;

    @Schema(description = "주소", example = "서울특별시 종로구")
    private String location;

    @Schema(description = "상시모집 여부", example = "true")
    private Boolean alwaysHiring = false;

    @Schema(description = "근무요일", example = "[\"mon\"]")

    private WorkDaysRequestDTO workDays;

    @Schema(description = "근무환경", example = "[\"TUTORING\"]")
    private WorkEnvironmentRequestDTO workEnvironment;


}
