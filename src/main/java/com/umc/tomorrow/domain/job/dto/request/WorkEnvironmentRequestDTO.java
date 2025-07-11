package com.umc.tomorrow.domain.job.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkEnvironmentRequestDTO {

    @NotNull
    private Boolean canWorkSitting;

    @NotNull
    private Boolean canWorkStanding;

    @NotNull
    private Boolean canLiftHeavyObjects;

    @NotNull
    private Boolean canLiftLightObjects;

    @NotNull
    private Boolean canMoveActively;

    @NotNull
    private Boolean canCommunicate;
}
