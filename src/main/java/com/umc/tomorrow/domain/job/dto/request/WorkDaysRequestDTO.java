package com.umc.tomorrow.domain.job.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkDaysRequestDTO {

    @NotNull
    private Boolean MON;

    @NotNull
    private Boolean TUE;

    @NotNull
    private Boolean WED;

    @NotNull
    private Boolean THU;

    @NotNull
    private Boolean FRI;

    @NotNull
    private Boolean SAT;

    @NotNull
    private Boolean SUN;

    @NotNull(message = "요일 협의 가능 여부는 필수입니다.")
    private Boolean isDayNegotiable;
}
