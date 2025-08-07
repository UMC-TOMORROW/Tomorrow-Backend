package com.umc.tomorrow.domain.job.dto.request;

import com.umc.tomorrow.domain.job.enums.JobCategory;
import com.umc.tomorrow.domain.job.enums.PaymentType;
import com.umc.tomorrow.domain.job.enums.RegistrantType;
import com.umc.tomorrow.domain.job.enums.WorkPeriod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "일자리 생성 DTO")
public class JobRequestDTO {

    @Schema(description = "제목",
            example = "편의점 알바 구인",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{job.title.notblank}")
    private String title;

    @Schema(description = "근무기간",
            example = "OVER_ONE_YEAR",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "{job.workPeriod.notnull}")
    private WorkPeriod workPeriod;

    @Schema(description = "근무기간 협의", example = "true")
    private Boolean isPeriodNegotiable = false;

    @Schema(description = "근무 시작 시간", example = "12:00")
    private LocalTime workStart;

    @Schema(description = "근무 종료 시간", example = "17:00")
    private LocalTime workEnd;

    @Schema(description = "근무 시작 협의", example = "true")
    private Boolean isTimeNegotiable = false;

    @Schema(description = "급여 형태", example = "HOURLY", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "{job.paymentType.notnull}")
    private PaymentType paymentType;

    @Schema(description = "일자리 카테고리", example = "TUTORING", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "{job.jobCategory.notnull}")
    private JobCategory jobCategory;

    @Schema(description = "급여",
            example = "12000",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "{job.salary.notnull}")
    @Min(value = 1, message = "{job.salary.min}")
    private Integer salary;

    @Schema(description = "근무 설명", example = "음료 제조 및 서빙")
    private String jobDescription;

    @Schema(description = "시설 이미지", example = "...")
    private String jobImageUrl;

    @Schema(description = "회사명",
            example = "내일",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "{job.companyName.notnull}")
    private String companyName;

    @Schema(description = "공고 활성화 여부",
            example = "true",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "{job.isActive.notnull}")
    private Boolean isActive = true;

    @Schema(description = "모집인원",
            example = "2",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "{job.recruitmentLimit.notnull}")
    @Min(value = 1, message = "{job.recruitmentLimit.min}")
    private Integer recruitmentLimit;

    @Schema(description = "등록유형",
            example = "PERSONAL",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "{job.registrantType.notnull}")
    private RegistrantType registrantType;

    @Schema(description = "공고 마감일",
            example = "2025-08-01",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "{job.deadline.notnull}")
    private LocalDateTime deadline;

    @Schema(description = "우대사항",
            example = "대학생 우대")
    private String preferredQualifications;

    @Schema(description = "위도",
            example = "37.572950",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "{job.latitude.notnull}")
    private BigDecimal latitude;

    @Schema(description = "경도",
            example = "126.979357",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "{job.longitude.notnull}")
    private BigDecimal longitude;

    @Schema(description = "주소",
            example = "서울특별시 종로구",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{job.location.notblank}")
    private String location;

    @Schema(description = "상시모집 여부",
            example = "true",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "{job.alwaysHiring.notnull}")
    private Boolean alwaysHiring = false;

    @Valid
    @NotNull(message = "{job.workDays.notnull}")
    private WorkDaysRequestDTO workDays;

    @Valid
    @NotNull(message = "{job.workEnvironment.notnull}")
    private WorkEnvironmentRequestDTO workEnvironment;
}
