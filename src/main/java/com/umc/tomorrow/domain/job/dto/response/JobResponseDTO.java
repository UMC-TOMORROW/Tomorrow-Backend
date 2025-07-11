package com.umc.tomorrow.domain.job.dto.response;

import com.umc.tomorrow.domain.job.enums.RegistrantType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobResponseDTO {

    private RegistrantType registrantType;
    private String step;

}
