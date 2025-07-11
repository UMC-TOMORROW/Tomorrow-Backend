package com.umc.tomorrow.domain.job.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonalRequestDTO {

    @NotNull(message = "이름은 필수입니다.")
    private String name;

    private BigDecimal latitude; //지도 api구현하고 필수로

    private BigDecimal longitude;

    @NotNull(message = "연락처는 필수입니다.")
    private String contact;

    private String registrationPurpose;


    private String address;

}
