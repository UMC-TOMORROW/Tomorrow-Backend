package com.umc.tomorrow.domain.job.dto.request;

import com.umc.tomorrow.domain.job.enums.PaymentType;
import com.umc.tomorrow.domain.job.enums.RegistrantType;
import com.umc.tomorrow.domain.job.enums.WorkPeriod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "일자리 생성 DTO")
public class JobRequestDTO {

    //notnull수정 예정

    @Schema(
            description = "제목",
            example = "편의점 알바 구인",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @Schema(
            description = "근무기간",
            example = "OVER_ONE_YEAR",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "근무 기간은 필수입니다.")
    private WorkPeriod workPeriod;

    @Schema(
            description = "근무기간 협의",
            example = "true",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Boolean isPeriodNegotiable = false;

    @Schema(
            description = "근무 시작 시간",
            example = "12:00",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private LocalDateTime workStart;

    @Schema(
            description = "근무 종료 시간",
            example = "17:00",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private LocalDateTime workEnd;

    @Schema(
            description = "근무 시작 협의",
            example = "true",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private Boolean isTimeNegotiable = false;

    @Schema(
            description = "급여 형태",
            example =  "HOURLY",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "급여 형태는 필수입니다.")
    private PaymentType paymentType;

    @Schema(
            description = "급여",
            example =  "12000",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "급여는 필수입니다.")
    @Min(value = 1, message = "급여는 1 이상이어야 합니다.")
    private Integer salary;

    @Schema(
            description = "근무 설명",
            example =  "음료 제조 및 서빙",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String jobDescription;

    @Schema(
            description = "시설 이미지",
            example =  "...",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String jobImageUrl;

    @Schema(
            description = "회사명",
            example =  "내일",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "회사명은 필수입니다.")
    private String companyName;

    @Schema(
            description = "공고 활성화 여부",
            example =  "true",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "공고 활성 여부는 필수입니다.")
    private Boolean isActive;

    @Schema(
            description = "모집인원",
            example =  "2",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "모집 인원은 필수입니다.")
    @Min(value = 1, message = "모집 인원은 1 이상이어야 합니다.")
    private Integer recruitmentLimit;

    @Schema(
            description = "등록유형",
            example =  "BUSINESS",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "등록자 유형은 필수입니다.")
    private RegistrantType registrantType;

    @Schema(
            description = "공고 마감일",
            example =  "2025-08-01",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "마감일은 필수입니다.")
    private LocalDateTime deadline;

    @Schema(
            description = "우대사항",
            example =  "대학생 우대",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String preferredQualifications;

    @Schema(
            description = "위도",
            example =  "123.566",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "위도는 필수입니다.")
    private BigDecimal latitude;

    @Schema(
            description = "경도",
            example =  "123.566",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "경도는 필수입니다.")
    private  BigDecimal longitude;

    @Schema(
            description = "주소",
            example =  "서울특별시 종로구",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "주소는 필수입니다.")
    private String location;

    @Schema(
            description = "상시모집 여부",
            example =  "true",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "상시 모집 여부는 필수입니다.")
    private Boolean alwaysHiring = false;

    //서브 테이블 DTO 포함
    @Valid
    @NotNull(message = "근무 요일 정보는 필수입니다.")
    private WorkDaysRequestDTO workDays;

    @Valid
    @NotNull(message = "근무 환경 정보는 필수입니다.")
    private WorkEnvironmentRequestDTO workEnvironment;
}
