package com.umc.tomorrow.domain.job.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "개인 사유 등록 DTO")
public class PersonalRequestDTO {

    @Schema(description = "이름",
            example = "가나다",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "{personal.name.notnull}")
    private String name;

    @Schema(description = "위도",
            example = "37.572950",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "{personal.latitude.notnull}")
    private BigDecimal latitude;

    @Schema(description = "경도",
            example = "126.979357",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "{personal.longitude.notnull}")
    private BigDecimal longitude;

    @NotBlank(message = "{user.phoneNumber.notblank}")
    @Pattern(regexp = "^01[0-9]-?[0-9]{3,4}-?[0-9]{4}$",
            message = "{user.phoneNumber.pattern}")
    @Size(max = 20, message = "{user.phoneNumber.size}")
    private String contact;

    @Schema(description = "등록목적",
            example = "강아지 산책알바 구인",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "{personal.registrationPurpose.notnull}")
    private String registrationPurpose;

    @Schema(description = "주소",
            example = "인천광역시 부평구 부평동",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String address;
}