package com.umc.tomorrow.domain.job.dto.request;

import com.umc.tomorrow.domain.job.enums.PaymentType;
import com.umc.tomorrow.domain.job.enums.RegistrantType;
import com.umc.tomorrow.domain.job.enums.WorkPeriod;
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
public class CreateJobRequestDTO {

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "카테고리는 필수입니다.")
    private String jobCategory;

    @NotNull(message = "근무 기간은 필수입니다.")
    private WorkPeriod workPeriod;

    private Boolean isPeriodNegotiable = false;

    private LocalDateTime workStart;
    private LocalDateTime workEnd;

    private Boolean isTimeNegotiable = false;

    @NotNull(message = "급여 형태는 필수입니다.")
    private PaymentType paymentType;

    @NotNull(message = "급여는 필수입니다.")
    @Min(value = 1, message = "급여는 1 이상이어야 합니다.")
    private Integer salary;

    private String jobDescription;
    private String jobImageUrl;
    private String companyName;

    @NotNull(message = "공고 활성 여부는 필수입니다.")
    private Boolean isActive;

    @NotNull(message = "모집 인원은 필수입니다.")
    @Min(value = 1, message = "모집 인원은 1 이상이어야 합니다.")
    private Integer recruitmentLimit;

    @NotNull(message = "등록자 유형은 필수입니다.")
    private RegistrantType registrantType;

    @NotNull(message = "마감일은 필수입니다.")
    private LocalDateTime deadline;

    private String preferredQualifications;

    @NotNull(message = "위도는 필수입니다.")
    private BigDecimal latitude;

    @NotNull(message = "경도는 필수입니다.")
    private  BigDecimal longitude;

    @NotBlank(message = "주소는 필수입니다.")
    private String location;

    private Boolean alwaysHiring = false;

    //서브 테이블 DTO 포함
    @Valid
    @NotNull(message = "근무 요일 정보는 필수입니다.")
    private WorkDaysRequestDTO workDays;

    @Valid
    @NotNull(message = "근무 환경 정보는 필수입니다.")
    private WorkEnvironmentRequestDTO workEnvironment;
}
