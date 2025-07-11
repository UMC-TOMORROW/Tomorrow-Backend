package com.umc.tomorrow.domain.job.dto.request;

import com.umc.tomorrow.domain.job.enums.PaymentType;
import com.umc.tomorrow.domain.job.enums.RegistrantType;
import com.umc.tomorrow.domain.job.enums.WorkPeriod;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

//제거 예정
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateJobRequestDTO {

    private String title;
    private String description;
    private String jobCategory;
    private Integer salary;
    private String jobImageUrl;

    private String startTime;
    private String endTime;

    private Boolean isTimeNegotiable;
    private Boolean isPeriodNegotiable;

    private String workEnvironment;
    private List<String> workDays;

    private LocalDateTime deadline;

    @NotNull(message = "근무유형은 필수입니다.")
    private RegistrantType registrantType;

    private WorkPeriod workPeriod;

    private PaymentType paymentType;
}
