package com.umc.tomorrow.domain.searchAndFilter.dto.response;

import com.umc.tomorrow.domain.job.dto.request.WorkDaysRequestDTO;
import com.umc.tomorrow.domain.job.entity.WorkDays;
import com.umc.tomorrow.domain.job.enums.JobCategory;
import com.umc.tomorrow.domain.job.enums.PaymentType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "검색 기능 응답 DTO")
public class JobSearchResponseDTO {

    @Schema(description = "일자리ID", example = "1")
    private Long jobId;

    @Schema(description = "공고 제목", example = "편의점")
    private String title;

    @Schema(description = "근무 장소", example = "인천광역시")
    private String location;

    @Schema(description = "회사명", example = "(주)내일")
    private String companyName;

    @Schema(description = "급여", example = "12,000")
    private Integer salary;

    @Schema(description = "근무 시작 시간", example = "09:00")
    private LocalTime workStart;

    @Schema(description = "근무 종료 시감", example = "20:00")
    private LocalTime workEnd;

    @Schema(description = "업무유형", example = "SERVING")
    private JobCategory jobCategory;

    @Schema(description = "근무요일", example = "mon")
    private WorkDaysDTO workDays;

    @Schema(description = "이미지", example = "...")
    private String jobImageUrl;

    @Schema(description = "급여타입", example = "HOURLY")
    private PaymentType paymentType;

}
