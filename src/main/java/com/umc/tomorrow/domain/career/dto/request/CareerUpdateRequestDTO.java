package com.umc.tomorrow.domain.career.dto.request;

import com.umc.tomorrow.domain.career.enums.WorkPeriodType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "이력서 - 경력 수정 요청 DTO")
public class CareerUpdateRequestDTO {

}
