package com.umc.tomorrow.domain.job.dto.response;

import com.umc.tomorrow.domain.job.enums.PaymentType;
import com.umc.tomorrow.domain.job.enums.WorkPeriod;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@Schema(description = "내일 추천 게시물 DTO")
public class GetRecommendationResponse {

    @Schema(description = "게시글 ID", example = "1")
    private Long id;

    private String companyName;

    private String title;

    private String location;

    private WorkPeriod workPeriod; // 조건부 포함
    private Boolean isPeriodNegotiable;

    private LocalTime workStart;   // 조건부 포함
    private LocalTime workEnd;     // 조건부 포함
    private Boolean isTimeNegotiable;

    private Boolean isSalaryNegotiable;
    private Integer salary;
    private PaymentType paymentType;

    private long reviewCount;
}
