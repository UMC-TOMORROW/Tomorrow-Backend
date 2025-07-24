package com.umc.tomorrow.domain.job.dto;

import com.umc.tomorrow.domain.job.dto.request.WorkDaysRequestDTO;
import com.umc.tomorrow.domain.job.dto.request.WorkEnvironmentRequestDTO;
import com.umc.tomorrow.domain.job.enums.PaymentType;
import com.umc.tomorrow.domain.job.enums.RegistrantType;
import com.umc.tomorrow.domain.job.enums.WorkPeriod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "일자리 등록 임시저장 DTO")
public class JobSessionDTO {

    private String title;
    private LocalDateTime deadline;
    private RegistrantType registrantType;
    private WorkPeriod workPeriod;
    private PaymentType paymentType;
    private LocalDateTime workStart;
    private LocalDateTime workEnd;
    private Boolean isTimeNegotiable;
    private Integer salary;
    private String jobDescription;
    private String jobImageUrl;
    private String companyName;
    private Boolean isActive;
    private Integer recruitmentLimit;
    private String preferredQualifications;
    private BigDecimal latitude;
    private  BigDecimal longitude;
    private String location;
    private Boolean alwaysHiring;

    private WorkDaysRequestDTO workDays;
    private WorkEnvironmentRequestDTO workEnvironment;

}