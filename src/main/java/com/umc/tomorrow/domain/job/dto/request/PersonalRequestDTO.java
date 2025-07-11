package com.umc.tomorrow.domain.job.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "개인 사유 등록 DTO")
public class PersonalRequestDTO {

    @Schema(
            description = "이름",
            example = "가나다",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "이름은 필수입니다.")
    private String name;

    @Schema(
            description = "위도",
            example = "122323.3233",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private BigDecimal latitude; //지도 api구현하고 필수로

    @Schema(
            description = "경도",
            example = "12.32211",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private BigDecimal longitude;

    @Schema(
            description = "전화번호",
            example = "010-1234-5678",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "연락처는 필수입니다.")
    private String contact;

    @Schema(
            description = "등록목적",
            example = "강아지 산책알바 구인",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String registrationPurpose;

    @Schema(
            description = "주소",
            example = "인천광역시 서구",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String address; // notnull로 수정 예정

}
