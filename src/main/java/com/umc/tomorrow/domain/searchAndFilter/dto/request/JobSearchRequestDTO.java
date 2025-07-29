package com.umc.tomorrow.domain.searchAndFilter.dto.request;

import com.umc.tomorrow.domain.job.enums.JobCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobSearchRequestDTO {

    @Schema(description = "일자리 공고 제목으로 검색",
            example = "편의점",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String keyword;

    @Schema(description = "업무 유형",
            example = "SERVING",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<JobCategory> jobCategories;

    @Schema(description = "일자리 주소",
            example = "서울특별시 종로구",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String locationKeyword;

    @Schema(description = "근무 시작 시간",
            example = "09:00",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String timeStart;

    @Schema(description = "근무 종류 시간",
            example = "20:00",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String timeEnd;

    @Schema(description = "근무 요일",
            example = "mon",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<String> workDays;

}
