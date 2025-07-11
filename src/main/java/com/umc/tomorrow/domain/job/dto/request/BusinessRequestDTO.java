package com.umc.tomorrow.domain.job.dto.request;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessRequestDTO {

    @NotNull(message = "사업자 등록 번호는 필수입니다.")
    private String bizNumber;

    @NotNull(message = "회사명은 필수입니다.")
    private String companyName;

    @NotNull(message = "대표 이름은 필수입니다.")
    private String ownerName;

    @NotNull(message = "창립일은 필수입니다.")
    private LocalDate openingDate;

}
